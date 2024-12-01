package cc.modlabs.basicx.modules.anvil

import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.modules.BasicXModule
import dev.fruxz.stacked.text
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

class AnvilListener : Listener {

    @EventHandler
    fun onAnvilRename(event: PrepareAnvilEvent): Unit = with(event) {
        if (result == null) return
        if (!ModuleManager.isModuleEnabled(BasicXModule.ANVIL)) return

        val player = view.player as? Player ?: return
        if (!player.hasPermission("basicx.anvil.rename")) return

        val renameText = view.renameText ?: return
        result = applyMiniMessageToItem(result!!, renameText)
    }

    private fun applyMiniMessageToItem(item: ItemStack, renameText: String): ItemStack {
        val itemMeta = item.itemMeta ?: return item
        itemMeta.displayName(text(renameText))
        item.itemMeta = itemMeta
        return item
    }
}