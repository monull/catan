package io.github.monull.catan.ready

import io.github.monull.catan.CatanManager
import io.github.monull.catan.process.CatanPlayer
import io.github.monun.tap.util.isDamageable
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BuildTowns(val manager: CatanManager) {
    val onlinePlayers = arrayListOf<CatanPlayer>()
    var currentPlayer: CatanPlayer? = null
    var listener: Listener? = null
    val end = false

    init {
        Bukkit.getOnlinePlayers().forEach(manager.fakeEntityServer::addPlayer)
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
        }

        val players = ArrayList<CatanPlayer>(onlinePlayers)
        this.currentPlayer = players[Random.nextInt(players.size)]
        this.listener = BuildTownListener(this)
        Bukkit.getPluginManager().registerEvents(listener!!, manager.plugin)
    }

    fun nextPlayer() {
        this.currentPlayer = currentPlayer?.getNext()
        currentPlayer?.ready()
    }
}

class BuildTownListener(val process: BuildTowns) : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player == process.currentPlayer?.player) {
            val player = event.player
            val loc = player.eyeLocation
            val vector = loc.direction
            val result = player.world.rayTraceBlocks(loc, vector, 8.0, FluidCollisionMode.NEVER, true)

            if (result != null) {
                val location = getNearestLocation(result.hitBlock?.location!!)
                player.teleport(location)
                process.manager.fakeEntityServer.spawnEntity(location, ArmorStand::class.java).apply {
                    player.teleport(location)
                    updateMetadata<ArmorStand> {
                        isInvisible = true
                        isMarker = true
                    }
                    updateEquipment {
                        helmet = ItemStack(Material.getMaterial("${process.currentPlayer?.color.toString().toUpperCase()}_DYE")!!).apply {
                            this.itemMeta = itemMeta.apply {
                                setCustomModelData(1)
                            }
                        }
                        println("${process.currentPlayer?.color.toString().toUpperCase()}_DYE")
                    }
                }
            }
        }
    }

    fun getNearestLocation(v: Location): Location {
        val grounds = process.manager.grounds

        var distance = 0.0
        var nearest: Location? = null

        grounds.forEach { ground ->
            val curDistance = ground.distance(v)

            if (distance == 0.0 || curDistance < distance) {
                distance = curDistance
                nearest = ground
            }
        }

        return nearest!!.clone().apply { y += 1 }
    }
}