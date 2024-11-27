package cc.modlabs.basicx

import cc.modlabs.basicx.commands.*
import cc.modlabs.basicx.commands.FeedCommand.Companion.createFeedCommand
import cc.modlabs.basicx.commands.HealCommand.Companion.createHealCommand
import cc.modlabs.basicx.commands.KitCommand.Companion.createKitCommand
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.modules.BasicXModule
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

class CommandBootstrapper : PluginBootstrap {

    private val moduleFunctions = mutableMapOf<BasicXModule, (Commands) -> Unit>(
        BasicXModule.TELEPORT to ::registerTeleportationCommands,
        BasicXModule.TPA to ::registerTeleportationRequestCommands,
        BasicXModule.INVSEE to ::registerInventoryCommands,
        BasicXModule.GM to ::registerGameModeCommands,
        BasicXModule.ITEMEDIT to ::registerItemEditCommand,
        BasicXModule.WARP to ::registerWarpCommands,
        BasicXModule.HOMES to ::registerHomeCommands,
        BasicXModule.ECONOMY to ::registerEconomyCommands,
        BasicXModule.TRASH to ::registerTrashCommand,
        BasicXModule.TIME to ::registerTimeCommands,
        BasicXModule.WEATHER to ::registerWeatherCommands,
        BasicXModule.FLY to ::registerFlyCommand,
        BasicXModule.KITS to ::registerKitCommands,
        BasicXModule.FEED to ::registerFeedCommand,
        BasicXModule.HEAL to ::registerHealCommand,
        BasicXModule.VANISH to ::registerVanishCommand,
        BasicXModule.ANVIL to ::registerAnvilCommand,
    )

    override fun bootstrap(context: BootstrapContext) {
        val manager = context.lifecycleManager

        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()

            for ((module, register) in moduleFunctions) {
                if (ModuleManager.isModuleEnabled(module)) {
                    register(commands)
                }
            }

            registerPluginManagementCommands(commands)
        }
    }

    private fun registerPluginManagementCommands(commands: Commands) {
        commands.register(
            createBasicXCommand(),
            "Plugin management"
        )
    }

    private fun registerTeleportationCommands(commands: Commands) {
        commands.register(
            createTPCommand(),
            "Teleport to a player or coordinates"
        )
    }

    private fun registerTeleportationRequestCommands(commands: Commands) {
        commands.register(
            createTPACommand(),
            "Request to teleport to a player"
        )
    }

    private fun registerInventoryCommands(commands: Commands) {
        commands.register(
            registerInvSeeCommand(),
            "View another player's inventory"
        )
    }

    private fun registerGameModeCommands(commands: Commands) {
        commands.register(
            createGMCommand(),
            "Change game mode"
        )
    }

    private fun registerItemEditCommand(commands: Commands) {
        commands.register(
            ItemEditCommand().register().build(),
            "Edit items (sign, enchant, rename)"
        )
    }

    private fun registerKitCommands(commands: Commands) {
        commands.register(
            createKitCommand(),
            "Receive a kit"
        )
    }

    private fun registerFlyCommand(commands: Commands) {
        commands.register(
            FlyCommand.createFlyCommand(),
            "Toggle fly mode"
        )
    }

    private fun registerTrashCommand(commands: Commands) {
        commands.register(
            createTrashCommand(),
            "Open trash GUI"
        )
    }

    private fun registerFeedCommand(commands: Commands) {
        commands.register(
            createFeedCommand(),
            "Feed a player"
        )
    }

    private fun registerHealCommand(commands: Commands) {
        commands.register(
            createHealCommand(),
            "Heal a player"
        )
    }

    private fun registerVanishCommand(commands: Commands) {
        commands.register(
            VanishCommand().createVanishCommand(),
            "Toggle vanish mode"
        )
    }

    private fun registerAnvilCommand(commands: Commands) {
        commands.register(
            AnvilCommand.register(),
            "Open anvil GUI"
        )
    }

    private fun registerWarpCommands(commands: Commands) {
        commands.register(
            WarpCommand.createWarpCommand(),
            "Warp to a location",
            listOf("warps")
        )

        commands.register(
            CreateWarpCommand.createWarpCommand(),
            "Create a warp"
        )

        commands.register(
            DeleteWarpCommand.createDeleteWarpCommand(),
            "Delete a warp",
            listOf("delwarp")
        )
    }

    private fun registerHomeCommands(commands: Commands) {
        commands.register(
            HomesCommand.createHomesCommand(),
            "Manage homes",
            listOf("home")
        )
    }

    private fun registerEconomyCommands(commands: Commands) {
        commands.register(
            createEconomyCommand(),
            "Economy commands"
        )
    }

    private fun registerTimeCommands(commands: Commands) {
        commands.register(
            createTimeCommand(),
            "Set or add time"
        )
    }

    private fun registerWeatherCommands(commands: Commands) {
        commands.register(
            WeatherCommand().createWeatherCommand(),
            "Set weather"
        )
    }
}
