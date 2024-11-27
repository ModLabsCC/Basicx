package cc.modlabs.basicx

import cc.modlabs.basicx.commands.*
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

class CommandBootstrapper : PluginBootstrap {

    override fun bootstrap(context: BootstrapContext) {
        val manager = context.lifecycleManager

        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()

            registerPluginManagementCommands(commands)
            registerTeleportationCommands(commands)
            registerInventoryCommands(commands)
            registerGameModeCommands(commands)
            registerUtilityCommands(commands)
            registerWarpCommands(commands)
            registerHomeCommands(commands)
            registerEconomyCommands(commands)
            registerKitCommands(commands)
            registerTimeCommands(commands)
            registerWeatherCommands(commands)
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

    private fun registerUtilityCommands(commands: Commands) {
        commands.register(
            VanishCommand().createVanishCommand(),
            "Toggle vanish mode"
        )

        commands.register(
            ItemEditCommand().register().build(),
            "Edit items (sign, enchant, rename)"
        )

        commands.register(
            createTrashCommand(),
            "Open trash GUI"
        )

        commands.register(
            FeedCommand.createFeedCommand(),
            "Feed a player"
        )

        commands.register(
            HealCommand.createHealCommand(),
            "Heal a player"
        )

        commands.register(
            FlyCommand.createFlyCommand(),
            "Toggle fly mode"
        )

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

    private fun registerKitCommands(commands: Commands) {
        commands.register(
            KitCommand.createKitCommand(),
            "Receive a kit"
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
