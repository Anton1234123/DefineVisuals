# Define Visuals

**Define Visuals 1.1 Alpha** is an open-source, client-side visual enhancement and HUD utility mod for Minecraft 1.21.4 using Fabric.

## Features

- Customizable Liquid Glass, Glass, Minimalism, and Modern Minimalism interface styles
- Configurable HUD elements, Target HUD, effects, keybinds, and Dynamic Island
- Custom title screen, themes, shaders, animations, and cosmetic models
- HolyWorld event and auction information panels
- OptiFine-style zoom and inventory quality-of-life utilities

## Requirements

- Minecraft 1.21.4
- Fabric Loader 0.16.14 or newer
- Fabric API
- Java 21

## Building from source

```bash
git clone https://github.com/Anton1234123/DefineVisuals.git
cd DefineVisuals
./gradlew build
```

The compiled mod is created at:

```text
build/libs/definevisuals-1.1.0-alpha.jar
```

## Network access

Define Visuals does not include telemetry and does not upload user data. Optional features make read-only requests to:

- `https://api.holyworld.me` for HolyWorld event and auction information
- `https://api.thecatapi.com` only when the user explicitly runs the cat command

## Links

- Source: https://github.com/Anton1234123/DefineVisuals
- Issues: https://github.com/Anton1234123/DefineVisuals/issues

## License

Define Visuals is licensed under the GNU General Public License v3.0 (`GPL-3.0`).
