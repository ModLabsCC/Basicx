package cc.modlabs.basicx.extensions

import cc.modlabs.basicx.cache.MessageCache
import dev.fruxz.stacked.text
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun Player.send(key: String, placeholders: Map<String, Any> = emptyMap<String, String>(), default: String = key) {
    this.sendMessage(text(MessageCache.getMessage(key, this, placeholders, default)))
}

fun CommandSender.send(key: String, placeholders: Map<String, Any> = emptyMap<String, String>(), default: String = key) = sendMessage(text(MessageCache.getMessage(key, this, placeholders, default)))

fun broadcast(key: String, placeholders: Map<String, Any> = emptyMap<String, String>(), default: String =key) = Bukkit.broadcast(text(MessageCache.getMessage(key, Bukkit.getConsoleSender(), placeholders, default)))

fun CommandSender.sendEmtpyLine() = sendMessage(text(" "))

fun Player.soundExecution() {
    playSound(location, Sound.ENTITY_ITEM_PICKUP, .75F, 2F)
    playSound(location, Sound.ITEM_ARMOR_EQUIP_LEATHER, .25F, 2F)
    playSound(location, Sound.ITEM_ARMOR_EQUIP_CHAIN, .1F, 2F)
}


fun Player.sendDeniedSound() = playSound(location, "minecraft:block.note_block.bass", 1f, 1f)

fun Player.sendSuccessSound() = playSound(location, "minecraft:block.note_block.pling", 1f, 1f)

fun Player.sendTeleportSound() = playSound(location, "minecraft:block.note_block.harp", 1f, 1f)

fun Player.sendOpenSound() = playSound(location, "minecraft:block.note_block.chime", 1f, 1f)