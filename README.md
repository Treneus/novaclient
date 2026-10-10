# Nova Client (Fabric 1.21.11)

Client-side utility mod with a tabbed ClickGUI.

* **Right Shift** - open / close the menu (in a world)
* **Left click** - toggle a module  |  **Right click** - expand its settings
* Sliders are draggable, modes cycle on click, every module has a rebindable key
* Settings are saved to `config/novaclient.json`

## Build
Requirements: JDK 21 and Gradle 9.2+ (or a Fabric template's `gradlew`).

    gradle wrapper --gradle-version 9.2.0    # once, only if you don't have gradlew yet
    ./gradlew build                          # gradlew.bat build on Windows

The finished jar is `build/libs/novaclient-1.0.0.jar`. Put it in `.minecraft/mods`
together with Fabric API 0.141.3+1.21.11 and Fabric Loader 0.18.4+.

## No local setup? Build it on GitHub
1. Create a new GitHub repo and upload everything in this folder (keep the `.github` folder).
2. Open the **Actions** tab, wait for the `build` run to finish (a few minutes).
3. Open the run, download the **novaclient-jar** artifact and unzip it. That jar goes in `mods`.

`fabric.mod.json` lives in `src/main/resources/` and ends up inside the jar. `config/novaclient.json`
is created by the mod itself the first time you close the menu or quit the game.
