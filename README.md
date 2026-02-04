# EzBanners

EzBanners collects live server data and securely syncs it to your banner API for dynamic image generation.

**Official Repository:** [github.com/ez-plugins/EzBanners](https://github.com/ez-plugins/EzBanners)

## Artifacts

This project produces two JAR files:

1. **EzBanners.jar** - The full plugin JAR for Minecraft servers
   - Contains all plugin classes and resources
   - Deploy this to your server's `plugins/` folder

2. **EzBanners-api.jar** - The API-only JAR for plugin developers
   - Contains only the public API interfaces and domain models
   - Use as a Maven/Gradle dependency when developing plugins that integrate with EzBanners
   - Available from GitHub Packages: `com.github.ez-plugins:ezbanners-api:0.3.0`

## Features
- Compatible with Minecraft 1.7 – 1.21+ (Bukkit/Spigot/Paper)
- Async HTTP sync with exponential backoff and jitter
- Optional PlaceholderAPI support
- Secure token + HMAC signatures
- Public API for external plugins
- Configurable feature flags

## Architecture

### Module Structure

#### Domain Layer (`domain/`)
- **ServerData**: Immutable model for collected server metrics
- **SyncPayload**: Immutable model for sync payloads with headers

#### Service Layer (`sync/`, `metrics/`, `http/`)
- **SyncScheduler**: Handles timing and exponential backoff with jitter
- **SyncExecutor**: Builds payloads and executes HTTP requests
- **SyncService**: Coordinates scheduling and execution
- **ServerDataCollector**: Collects server metrics based on config
- **ApiClient**: Standardized HTTP client with consistent headers

#### Plugin Layer (`lifecycle/`, `command/`)
- **PluginLifecycle**: Manages startup/shutdown flows
- **EzBannersPlugin**: Bukkit plugin entry point
- **Commands**: LinkCommand, ReloadCommand, StatusCommand

#### Configuration (`config/`)
- **EzBannersConfig**: Type-safe config wrapper with validation
- **ConfigKeys**: Centralized config key constants

#### Public API (`api/provider/`)
- **DataProvider**: Interface for custom data providers
- **PlaceholderProvider**: Interface for custom placeholder providers
- **EzBannersApi**: Public API for external plugin integration

## Setup
1. Drop `EzBanners.jar` into your server `plugins/` folder.
2. Start the server to generate `plugins/EzBanners/config.yml`.
3. Edit `config.yml` and set:
   - `api.endpoint`
   - `api.token` (or use `/ezbanners link <token>`)
4. (Optional) Configure `placeholderapi.mappings` if you use PlaceholderAPI.
5. Restart or reload the plugin.

## Commands
- `/ezbanners link <token>` — Links the server to your API token
- `/ezbanners reload` — Reloads the configuration
- `/ezbanners status` — Shows detailed plugin status

## Permissions
- `ezbanners.link` — Allows the link command (default: op)
- `ezbanners.reload` — Allows the reload command (default: op)
- `ezbanners.status` — Allows the status command (default: op)

## Configuration Reference

### API Configuration
```yaml
api:
  endpoint: "https://ezbanners.org/api/server/update"  # Banner API endpoint
  token: ""  # Your API token (use /ezbanners link <token>)
```

### Plugin Stats Configuration
```yaml
plugin:
  endpoint: "https://ezbanners.org/api/plugins/{plugin_uuid}/data"
  uuid: ""  # Plugin UUID for stats tracking
  token: ""  # Plugin token for authentication
```

### Server Configuration
```yaml
server:
  uuid: ""  # Auto-generated server UUID (do not modify)
```

### Sync Configuration
```yaml
sync:
  interval: 300  # Sync interval in seconds (min: 5)
  max-backoff: 1200  # Maximum backoff on failure in seconds
```

### Feature Flags
```yaml
features:
  metrics:
    enabled: true  # Enable/disable metrics collection
  placeholders:
    enabled: true  # Enable/disable PlaceholderAPI integration
  website-sync:
    enabled: true  # Enable/disable website sync
```

### Data Fields
Configure which data fields to collect and send:
```yaml
enabled:
  data:
    fields:
      - server_name
      - online_players
      - max_players
      - server_version
      - tps_1m
      - tps_5m
      - tps_15m
      - uptime
      - motd
      - whitelist
      - placeholders
```

### PlaceholderAPI Mappings
Define custom placeholder mappings:
```yaml
placeholderapi:
  mappings:
    example_rank: "%vault_rank%"
    example_balance: "%vault_eco_balance_formatted%"
```

### Debug Mode
```yaml
debug:
  enabled: false  # Enable verbose debug logging
```

## Public API Usage

### Adding the API Dependency

The EzBanners API is available as a separate artifact on GitHub Packages from the [ez-plugins/EzBanners](https://github.com/ez-plugins/EzBanners) repository.

#### Maven
Add the GitHub Packages repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/ez-plugins/EzBanners</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ez-plugins</groupId>
        <artifactId>ezbanners-api</artifactId>
        <version>0.3.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**Note:** You need to authenticate with GitHub Packages. Add your GitHub credentials to `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

#### Gradle
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/ez-plugins/EzBanners")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    compileOnly 'com.github.ez-plugins:ezbanners-api:0.3.0'
}
```

### For Plugin Developers

#### Implementing a Custom Data Provider

```java
import com.skyblockexp.ezbanners.api.provider.DataProvider;
import com.skyblockexp.ezbanners.domain.ServerData;

public class MyDataProvider implements DataProvider {
    @Override
    public ServerData collectData() {
        Map<String, Object> data = new HashMap<>();
        data.put("custom_metric", getMyMetric());
        return new ServerData(data);
    }
    
    @Override
    public String getName() {
        return "MyPlugin";
    }
    
    @Override
    public int getPriority() {
        return 10; // Higher = called earlier
    }
}
```

#### Sending Plugin Stats

```java
import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.api.EzBannersApi;

// Get API instance
EzBannersApi api = EzBannersPlugin.getInstance().getApi();

// Send stats once
api.sendPluginStats(
    "your-plugin-uuid",
    yourPluginInstance,
    1,  // server count
    Bukkit.getOnlinePlayers().size()  // player count
);

// Or start auto-stats push (every 5 minutes)
api.startAutoStatsPush("your-plugin-uuid", yourPluginInstance, 300);
```

## Sync Payload Structure

Each sync sends:
- **Token**: API authentication token
- **Server UUID**: Unique server identifier
- **Timestamp**: Current timestamp in milliseconds
- **HMAC Signature**: SHA256 signature of request body
- **Data Fields**: Configured data fields from `enabled.data.fields`

## Backoff Strategy

EzBanners uses exponential backoff with jitter for failed sync attempts:
- Formula: `interval * 2^failureCount`
- Jitter: Random 0-25% of calculated backoff
- Maximum: Capped at `sync.max-backoff` seconds
- Reset: Failure count resets to 0 on successful sync

## Notes
- TPS and PlaceholderAPI values are only sent when available
- HTTP requests are async and never block the main thread
- All domain models are immutable for thread safety
- Configuration is validated on load with warnings for missing values
