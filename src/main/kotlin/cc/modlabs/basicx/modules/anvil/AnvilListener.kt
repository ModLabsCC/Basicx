package cc.modlabs.basicx.modules.anvil

import dev.fruxz.stacked.extension.asPlainString
import dev.fruxz.stacked.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

class AnvilListener : Listener {

    @EventHandler
    fun onAnvilRename(event: PrepareAnvilEvent): Unit = with(event) {
        if (result == null) return
        result = replaceItemName(result!!)
    }

    private fun replaceItemName(item: ItemStack): ItemStack {
        item.itemMeta?.displayName(
            text(
                item.itemMeta?.displayName()?.asPlainString ?: item.type.name
            )
        )
        return item
    }


}