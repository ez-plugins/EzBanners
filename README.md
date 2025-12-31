# 📸 EzBanners

Dynamic Minecraft banner generation API & plugin  
Generate live PNG/WebP banners for your server lists - fast, flexible, and compatible with Spigot, Paper & Bukkit (MC 1.7–1.21+).

SpigotMC resource:  
https://www.spigotmc.org/resources/1-7-1-21-ezbanners-png-webp-banner-generation-public-api.1540/

PaperMC (Hengar) resource:
https://hangar.papermc.io/EzPlugins/EzBanners

---

## 🚀 Features

- 🔥 Live server stats – player count, TPS, MOTD, and more  
- 📊 Real-time banners – generated dynamically via API  
- 🌐 PNG & WebP support – crisp, modern images  
- 🛡 Secure sync – API tokens & signed requests  
- ⚡ Async & lightweight – optimized for performance  
- 🧩 Plugin + API – works with or without installing the plugin  
- 🧠 Wide compatibility – Minecraft 1.7 → 1.21+

---

## 📥 Download

Get the latest plugin build from SpigotMC or Hangar (PaperMC):

https://www.spigotmc.org/resources/1-7-1-21-ezbanners-png-webp-banner-generation-public-api.1540/
https://hangar.papermc.io/EzPlugins/EzBanners

---

## 🧠 Introduction

EzBanners allows you to generate dynamic Minecraft server banners using live data.

You can use EzBanners in two ways:

1. With the plugin – automatic, secure syncing of server stats  
2. Without the plugin – generate banners directly via the public API  

Perfect for:
- Server lists  
- Websites  
- Forums  
- Dashboards  
- Social embeds  

---

## 📦 Installation (Plugin)

1. Download EzBanners.jar  
2. Place it in your server’s plugins/ folder  
3. Start the server (config will be generated)  
4. Configure your API token  
5. Restart or reload the server  

---

## 🔐 Linking Your Server

Link your server to the EzBanners API or dashboard: `/ezbanners link <your_api_token>`

This enables secure data syncing for advanced templates.

---

## 🌐 Public Banner API (No Plugin Required)

You can generate banners directly via HTTP:

```bash
GET https://ezbanners.org/api/banner  
   ?server_name=MyServer  
   &online_players=12  
   &max_players=100  
   &motd_line_1=Welcome  
   &motd_line_2=To%20EzBanners  
   &template_key=minimal-status  
   &width=468  
   &height=60  
   &format=webp  
```

Returns a dynamically generated PNG or WebP image.

---

## 🛠 Plugin API (Advanced)

When using the plugin, EzBanners can:
- Send signed server data  
- Update banners automatically  
- Enable advanced & premium templates  
- Reduce manual configuration  

---

## 🟦 Supported Platforms

- Bukkit  
- Spigot  
- Paper  

Minecraft versions: 1.7 – 1.21+

---

## 📋 Example Use Cases

- Forum server banners  
- Website server status images  
- Discord or community embeds  
- Auto-updating server visuals  

---

## 🔗 Useful Links

SpigotMC resource  
https://www.spigotmc.org/resources/1-7-1-21-ezbanners-png-webp-banner-generation-public-api.1540/

API documentation  
https://ezbanners.org/docs/api

Website & banner designer  
https://ezbanners.org

Support Discord  
https://discord.gg/yWP95XfmBS

---


## 📜 License

MIT License

Copyright (c) 2025 Gyvex (63536625)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

## 🙌 Credits

Developed by EzPlugins  
Modern, powerful tools for Minecraft server owners.
