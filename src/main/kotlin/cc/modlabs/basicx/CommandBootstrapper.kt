package cc.modlabs.basicx

import cc.modlabs.basicx.commands.*
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

class CommandBootstrapper : PluginBootstrap {

    override fun bootstrap(context: BootstrapContext) {
        val manager = context.lifecycleManager

        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands = event.registrar()

            commands.register(
                createBasicXCommand(),
                "Plugin management"
            )

            commands.register(
                createTPCommand(),
                "Teleport to a player or coordinates"
            )

            commands.register(
                createTPACommand(),
                "Request to teleport to a player"
            )

            commands.register(
                InvseeCommand().register(),
                "View another player's inventory"
            )

            commands.register(
                VanishCommand().createVanishCommand(),
                "Toggle vanish mode"
            )

            commands.register(
                createGMCommand(),
                "Change game mode"
            )

            commands.register(
                ItemEditCommand().register().build(),
                "Edit items (sign, enchant, rename)"
            )

            commands.register(
                TrashCommand().createCommand(),
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

            commands.register(
                WarpCommand.createWarpCommand(),
                "Warp to a location"
            )

            commands.register(
                CreateWarpCommand.createWarpCommand(),
                "Create a warp"
            )

            commands.register(
                DeleteWarpCommand.createDeleteWarpCommand(),
                "Delete a warp"
            )

            commands.register(
                HomesCommand.createHomesCommand(),
                "Manage homes"
            )

            commands.register(
                createEconomyCommand(),
                "Economy commands"
            )

            commands.register(
                KitCommand.createKitCommand(),
                "Receive a kit"
            )

            commands.register(
                createTimeCommand(),
                "Set or add time"
            )

            commands.register(
                WeatherCommand().createWeatherCommand(),
                "Set weather"
            )
        }
    }
}
