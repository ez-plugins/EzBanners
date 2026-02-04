package com.skyblockexp.ezbanners.http;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.util.HmacUtil;
import com.skyblockexp.ezbanners.util.JsonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiClient {
    private final EzBannersPlugin plugin;

    public ApiClient(EzBannersPlugin plugin) {
        this.plugin = plugin;
    }

    public ApiResponse postPayload(EzBannersConfig config, Map<String, Object> data) {
        String endpoint = config.getApiEndpoint();
        String token = config.getApiToken();
        if (token == null || token.trim().isEmpty()) {
            return new ApiResponse(false, 0, "Missing api.token");
        }

        long timestamp = System.currentTimeMillis();
        String serverUuid = config.getServerUuid();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("data", data);

        String json = JsonUtil.toJson(payload);
        String signature = HmacUtil.hmacSha256(token, json); // HMAC_SHA256(requestBody, token)
        
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Server-UUID", serverUuid);
        headers.put("X-Server-Id", serverUuid);
        headers.put("X-Server-Token", token);
        headers.put("X-Signature", signature);
        headers.put("X-Timestamp", String.valueOf(timestamp));
        
        return postPayloadWithHeaders(endpoint, json, signature, headers);
    }

    public ApiResponse postPayloadWithHeaders(String endpoint, String json, String signature, Map<String, String> customHeaders) {
    public ApiResponse postPayloadWithHeaders(String endpoint, String json, String signature, Map<String, String> customHeaders) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", getUserAgent());
            
            // Apply custom headers
            if (customHeaders != null) {
                for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // Always set signature if provided
            if (signature != null) {
                connection.setRequestProperty("X-Signature", signature);
            }
            
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(8000);
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseMessage = readResponse(connection);
            boolean success = responseCode >= 200 && responseCode < 300;
            return new ApiResponse(success, responseCode, responseMessage);
        } catch (Exception ex) {
            return new ApiResponse(false, 0, ex.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public ApiResponse postPluginUsageStats(String pluginUuid, String pluginName, String pluginVersion, int serverCount, int playerCount) {
        String endpoint = "https://ezbanners.org/api/plugins/" + pluginUuid + "/data";
        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> pluginInfo = new LinkedHashMap<>();
        pluginInfo.put("name", pluginName);
        pluginInfo.put("version", pluginVersion);
        payload.put("plugin", pluginInfo);
        
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("servers", serverCount);
        stats.put("players", playerCount);
        payload.put("stats", stats);
        
        String json = JsonUtil.toJson(payload);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("User-Agent", getUserAgent());
        
        return postPayloadWithHeaders(endpoint, json, null, headers);
    }

    private String getUserAgent() {
        String version = plugin.getDescription().getVersion();
        return "EzBanners/" + version;
    }

    public ApiResponse postPluginPayload(EzBannersConfig config, String pluginUuid, Map<String, Object> payload) {
        String endpoint = resolvePluginEndpoint(config, pluginUuid);
        String token = config.getPluginToken();
        if (endpoint == null || endpoint.trim().isEmpty()) {
            return new ApiResponse(false, 0, "Missing plugin.endpoint");
        }
        if (token == null || token.trim().isEmpty()) {
            return new ApiResponse(false, 0, "Missing plugin.token");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        String json = JsonUtil.toJson(payload);
        String signature = HmacUtil.hmacSha256(token, timestamp + "." + json);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", getUserAgent());
            connection.setRequestProperty("X-Plugin-Token", token);
            connection.setRequestProperty("X-Signature", signature);
            connection.setRequestProperty("X-Timestamp", String.valueOf(timestamp));
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(8000);
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseMessage = readResponse(connection);
            boolean success = responseCode >= 200 && responseCode < 300;
            return new ApiResponse(success, responseCode, responseMessage);
        } catch (Exception ex) {
            return new ApiResponse(false, 0, ex.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String resolvePluginEndpoint(EzBannersConfig config, String pluginUuidOverride) {
        String endpoint = config.getPluginEndpoint();
        if (endpoint == null) {
            return "";
        }
        String pluginUuid = pluginUuidOverride;
        if (pluginUuid == null || pluginUuid.trim().isEmpty()) {
            pluginUuid = config.getPluginUuid();
        }
        if (pluginUuid == null || pluginUuid.trim().isEmpty()) {
            return endpoint;
        }
        return endpoint.replace("{plugin_uuid}", pluginUuid);
    }

    private String readResponse(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            connection.getResponseCode() >= 200 && connection.getResponseCode() < 400
                ? connection.getInputStream()
                : connection.getErrorStream(),
            StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception ex) {
            plugin.debug("Failed to read API response: " + ex.getMessage());
            return "";
        }
    }

    public static class ApiResponse {
        private final boolean success;
        private final int statusCode;
        private final String message;

        public ApiResponse(boolean success, int statusCode, String message) {
            this.success = success;
            this.statusCode = statusCode;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }
}
