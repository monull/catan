package io.github.monull.catan.ready

import io.github.monull.catan.CatanManager
import io.github.monull.catan.process.CatanPlayer
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.util.isDamageable
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BuildTowns(val manager: CatanManager) {
    val towns = arrayListOf<FakeEntity>()
    val roads = arrayListOf<FakeEntity>()
    val buildTowns = HashMap<CatanPlayer, Boolean>()
    val onlinePlayers = arrayListOf<CatanPlayer>()
    var currentPlayer: CatanPlayer? = null
    var listener: Listener? = null
    var end = false

    init {
        this.listener = BuildTownListener(this)
        Bukkit.getPluginManager().registerEvents(listener!!, manager.plugin)
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
    }

    fun nextPlayer() {
        if (roads.count() / onlinePlayers.count() == 2 && towns.count() / onlinePlayers.count() == 2) {
            HandlerList.unregisterAll(this.listener!!)
            end = true
        } else {
            this.currentPlayer = currentPlayer?.getNext()
            broadCastBuilder()
        }
    }

    fun broadCastBuilder() {
        Bukkit.getOnlinePlayers().filter { it != this.currentPlayer?.player!! }.forEach {
            it.sendTitlePart(TitlePart.SUBTITLE, text(currentPlayer?.player!!.name))
        }
        this.currentPlayer?.player!!.sendTitlePart(TitlePart.SUBTITLE, if (buildTowns[currentPlayer!!] == null || buildTowns[currentPlayer!!]!!) text("마을을 지으세요!") else text("도로를 지으세요!"))
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
                if (process.buildTowns[process.currentPlayer!!] == null) {
                    val location = getNearestLocation(result.hitBlock?.location!!)
                    var canSpawn = true
                    process.towns.forEach { town ->
                        val distance = town.location.distance(location)
                        if (distance <= 12) canSpawn = false
                    }
                    if (canSpawn) {
                        val town = process.manager.fakeEntityServer.spawnEntity(location, ArmorStand::class.java).apply {
                            updateMetadata<ArmorStand> {
                                isInvisible = true
                                isMarker = true
                            }
                            updateEquipment {
                                helmet = ItemStack(Material.getMaterial("${process.currentPlayer?.color.toString().toUpperCase()}_DYE")!!).apply {
                                    this.itemMeta = itemMeta.apply {
                                        setCustomModelData(2)
                                    }
                                }
                            }
                        }
                        process.towns += town
                        process.currentPlayer!!.townCount += 1
                        process.buildTowns[process.currentPlayer!!] = false
                    }
                } else {
                    if (process.buildTowns[process.currentPlayer!!]!!) {
                        val location = getNearestLocation(result.hitBlock?.location!!)
                        var canSpawn = true
                        process.towns.forEach { town ->
                            val distance = town.location.distance(location)
                            if (distance <= 12) canSpawn = false
                        }
                        if (canSpawn) {
                            val town = process.manager.fakeEntityServer.spawnEntity(location, ArmorStand::class.java).apply {
                                updateMetadata<ArmorStand> {
                                    isInvisible = true
                                    isMarker = true
                                }
                                updateEquipment {
                                    helmet = ItemStack(Material.getMaterial("${process.currentPlayer?.color.toString().toUpperCase()}_DYE")!!).apply {
                                        this.itemMeta = itemMeta.apply {
                                            setCustomModelData(2)
                                        }
                                    }
                                }
                            }
                            process.towns += town
                            process.currentPlayer!!.townCount += 1
                            process.buildTowns[process.currentPlayer!!] = false
                        }
                    } else {
                        val location = getNearestRoadLocation(result.hitBlock?.location!!)
                        var canSpawn = true
                        process.roads.forEach { road ->
                            val distance = road.location.distance(location)
                            if (distance <= 2) canSpawn = false
                        }
                        var nearbyTown = false
                        process.towns.forEach { town ->
                            val distance = town.location.distance(location)
                            if (distance <= 6) nearbyTown = true
                        }
                        canSpawn = canSpawn && nearbyTown
                        if (canSpawn) {
                            val road = process.manager.fakeEntityServer.spawnEntity(location, ArmorStand::class.java).apply {
                                updateMetadata<ArmorStand> {
                                    isInvisible = true
                                    isMarker = true
                                }
                                updateEquipment {
                                    helmet = ItemStack(Material.getMaterial("${process.currentPlayer?.color.toString().toUpperCase()}_DYE")!!).apply {
                                        this.itemMeta = itemMeta.apply {
                                            setCustomModelData(3)
                                        }
                                    }
                                }
                            }
                            process.roads += road
                            process.currentPlayer!!.townCount += 1
                            process.nextPlayer()
                            process.buildTowns[process.currentPlayer!!] = true
                        }
                    }
                }
            }
        }
    }

    fun getNearestLocation(v: Location): Location {

        var distance = 0.0
        var nearest: Location? = null

        process.manager.grounds.forEach { ground ->
            val curDistance = ground.distance(v)

            if (distance == 0.0 || curDistance < distance) {
                distance = curDistance
                nearest = ground
            }
        }

        return nearest!!.clone().apply { y += 1 }
    }

    fun getNearestRoadLocation(v: Location): Location {

        var distance = 0.0
        var nearest: Location? = null

        process.manager.roadGrounds.forEach { ground ->
            val curDistance = ground.distance(v)

            if (distance == 0.0 || curDistance < distance) {
                distance = curDistance
                nearest = ground
            }
        }

        return nearest!!.clone().apply { y += 1 }
    }
}