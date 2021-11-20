package io.github.monull.catan.process

import io.github.monull.catan.utils.CatanUtils
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.TitlePart
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class CatanListener(val process: CatanProcess) : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val current = process.currentPlayer
        if (event.player == current?.player) {
            if (event.item?.isSimilar(CatanUtils.diceItem)!! && current.diceCount > 0) {
                process.dice?.rotate()
                current.diceCount--
            }

            if (event.item?.isSimilar(CatanUtils.nextTurnItem)!!) {
                process.nextPlayer()
            }
        }
    }
}