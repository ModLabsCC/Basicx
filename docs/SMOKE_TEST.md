# Paper 26.2 smoke test

Use a disposable server with the newest Paper 26.2 experimental build and Java 25.

1. Run `./gradlew clean build`.
2. Copy the JAR from `build/libs` into the server's `plugins` directory.
3. Start once without LuckPerms or PlaceholderAPI. Confirm BasicX enables without exceptions and creates its four YAML files.
4. Repeat with current LuckPerms and PlaceholderAPI builds.
5. Test a non-operator and operator account:
   - denied administrative commands do not appear or execute;
   - `/tpa`, `/tpaccept`, `/tpdeny`, and the 60-second expiry work;
   - home, warp, and kit data survive a restart and deleted entries stay deleted;
   - module enable/disable takes effect immediately;
   - chat, join/quit messages, vanish-on-join, tab-list prefixes, and the BasicX anvil behave correctly.
6. Stop the server normally and confirm no configuration-writer or class-loader warnings remain.
7. Inspect `logs/latest.log` for `ERROR`, `Exception`, unsupported API messages, and unresolved runtime artifacts.

Do not promote an experimental Paper build to production without a world and plugin-data backup.
