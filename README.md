# Wyvern editor
Wyvern is a long running project, created to provide an open source alternative for Kadokawa-s RPG Maker software.
The editor is capable of creating 2D maps, editing database for a game, managing resources, creating events, playtesting, and more.
All features are created in a way, that can be extended to support multiple game engines.
The editor supports an experimental GUI editor to create dynamic GUI interfaces for managing game data.

![alt text](core/showcase/1.png)

![alt text](core/showcase/2.png)

![alt text](core/showcase/3.png)

![alt text](core/showcase/4.png)

![alt text](core/showcase/5.png)

## Building requirements
- JDK 8

## Building
>./gradlew.bat desktop:dist

- Copy the "desktop-1.0.jar" file from "desktop/build/libs/" to "core/"
- You can launch the editor with double clicking "desktop-1.0.jar" from the "core/" folder.

## NOTE: The windows_launcher.bat file is for the portable version (if a JRE is included with the editor).
