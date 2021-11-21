package io.github.monull.catan.process

import io.github.monull.catan.utils.CatanUtils
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CatanPlayer(val player: Player) {

    private var next: CatanPlayer? = null
    var diceCount = 1

    val color = Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(player.name)?.color()

    fun ready() {
        diceCount = 1
        player.inventory.setItem(0, CatanUtils.diceItem)
        player.inventory.setItem(9, CatanUtils.nextTurnItem)
    }

    fun setNext(player: CatanPlayer) {
        next = player
    }

    fun getNext(): CatanPlayer {
        return next!!
    }
}