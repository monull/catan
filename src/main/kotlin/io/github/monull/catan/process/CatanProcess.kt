package io.github.monull.catan.process

import io.github.monull.catan.CatanManager
import io.github.monull.catan.plugin.CatanPlugin
import io.github.monun.tap.util.isDamageable
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

class CatanProcess(val plugin: CatanPlugin, val manager: CatanManager) {

    var task: BukkitTask? = null
    var listener: Listener? = null
    val onlinePlayers = arrayListOf<CatanPlayer>()
    var dice: DiceScheduler? = null
    val gameEnd = false

    var currentPlayer: CatanPlayer? = null

    init {
        Bukkit.getOnlinePlayers().filter { it.gameMode.isDamageable }.forEach { player ->
            val catanPlayer = CatanPlayer(player)
            onlinePlayers.add(catanPlayer)
        }

        val order = arrayListOf<CatanPlayer>()
        onlinePlayers.forEach(order::add)
        order.shuffle()
        val size = order.size

        for (i in 0 until size) {
            val player = order[i]
            val next = order[(i + 1) % size]
            player.setNext(next)

            plugin.logger.info("${i + 1}. ${player.player.name}")
        }

        task = plugin.server.scheduler.runTaskTimer(plugin, CatanScheduler(this), 0, 1)
        this.listener = CatanListener(this)
    }

    fun start() {
        val players = ArrayList<CatanPlayer>(onlinePlayers)
        this.currentPlayer = players[Random.nextInt(players.size)]
        currentPlayer?.ready()
        broadcastGambler()

        plugin.server.pluginManager.registerEvents(this.listener!!, plugin)
        val numberList = arrayListOf<Int>()
        for (i in 1..19) numberList += i

        manager.regions.forEach { region ->
            val int = numberList.random()
            numberList.remove(int)
            val loc = region.location.world.getHighestBlockAt(region.location).location
            manager.fakeEntityServer.spawnEntity(loc.apply { y += 2 }, ArmorStand::class.java).apply {
                updateMetadata<ArmorStand> {
                    isInvisible = true
                    isMarker = true
                    isCustomNameVisible = true
                    customName = "$int"
                }
            }
        }
        dice = DiceScheduler(manager).apply { initialize() }
    }

    fun update() {

    }

    fun stopProcess() {
        manager.stopProcess()
        dice?.terminate()
    }

    fun unregister() {
        task?.cancel()
        HandlerList.unregisterAll(this.listener!!)
    }

    fun broadcastGambler() {
        Bukkit.getOnlinePlayers().filter { it != currentPlayer?.player }.forEach { player ->
            player.sendTitlePart(TitlePart.SUBTITLE, text(currentPlayer?.player!!.name))
        }
    }

    fun nextPlayer() {
        this.currentPlayer = currentPlayer?.getNext()
        broadcastGambler()
        currentPlayer?.ready()
    }
}