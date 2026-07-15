# BasicX

BasicX is a modular Paper plugin providing homes, warps, teleport requests, vanish, kits, moderation utilities, item editing, chat formatting, and time/weather controls.

## Requirements

- Java 25
- Paper 26.2 experimental
- Optional: LuckPerms 5.x and PlaceholderAPI 2.12.3+

Paper 26.2 is still an experimental Paper target. Test every BasicX update on a staging server and back up `plugins/BasicX` before upgrading production data.

## Building

The Gradle wrapper provisions the compile toolchain automatically:

```shell
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The plugin JAR is written to `build/libs`. Runtime libraries are resolved from the public ModLabs and Maven Central repositories by Paper's isolated plugin library loader.

## Modules and commands

Modules can be toggled in `plugins/BasicX/config.yml` or with `/basicx module enable|disable <module>`. Changes apply at runtime.

- Teleportation: `/tp`, `/tpa`, `/tpaccept`, `/tpdeny`
- Homes and warps: `/homes`, `/home`, `/warp`, `/createwarp`, `/deletewarp`
- Player tools: `/kit`, `/trash`, `/feed`, `/heal`, `/fly`, `/anvil`
- Administration: `/invsee`, `/gm`, `/vanish`, `/itemedit`, `/time`, `/weather`
- Management: `/basicx`, `/basicx reload`, `/basicx module ...`

The former economy command was removed because it did not persist balances or perform transfers.

## Permissions

Permissions and their safe defaults are declared in `paper-plugin.yml`. Player-facing defaults include `basicx.tpa`, `basicx.homes`, `basicx.warp`, `basicx.kit`, `basicx.kit.starter`, and `basicx.trash`; administrative permissions default to operators.

## Verification

Automated checks run through `./gradlew build`. For release validation, follow [the Paper 26.2 smoke test](docs/SMOKE_TEST.md).

## License

BasicX is available under the [MIT License](LICENSE).
