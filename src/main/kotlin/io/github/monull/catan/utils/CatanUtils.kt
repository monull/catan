package io.github.monull.catan.utils

import net.kyori.adventure.text.Component.text
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object CatanUtils {
    val diceItem = ItemStack(Material.STICK).apply {
        this.itemMeta = itemMeta.apply {
            displayName(text("${ChatColor.YELLOW}주사위 굴리기"))
        }
    }

    val nextTurnItem = ItemStack(Material.NETHER_STAR).apply {
        this.itemMeta.apply {
            displayName(text("${ChatColor.DARK_BLUE}차례 넘기기"))
        }
    }
}