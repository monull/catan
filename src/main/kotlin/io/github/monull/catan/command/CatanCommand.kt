package io.github.monull.catan.command

import io.github.monull.catan.CatanManager
import io.github.monull.catan.plugin.CatanPlugin
import io.github.monun.kommand.PluginKommand
import io.github.monun.tap.math.toRadians
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.sqrt

object CatanCommand {
    fun register(kommand: PluginKommand, plugin: CatanPlugin, catanManager: CatanManager) {
        kommand.apply {
            register("catan", "ct") {
                val colors = dynamic { _, input ->
                    listOf("red", "yellow", "blue", "green").firstOrNull { it == input }
                }.apply {
                    suggests {
                        suggest(listOf("red", "yellow", "blue", "green"))
                    }
                }
                then("start") {
                    executes {
                        catanManager.startProcess()
                    }
                }
                then("stop") {
                    executes {
                        catanManager.stopProcess()
                    }
                }
                then("map") {
                    requires { playerOrNull != null }
                    executes {
                        catanManager.resetMap()

                        val count = 10
                        val loc = player.location.apply {
                            z -= count * sqrt(3.0) / 2 * 3
                            x -= count / 2
                        }

                        val playerLoc = player.location

                        plugin.server.scheduler.runTaskTimer(plugin, HexagonScheduler(catanManager, loc, count, playerLoc), 0L, 1L)
                    }
                }
                then("supply") {
                    requires { playerOrNull != null }
                    then("color" to colors) {
                        then("city") {
                            executes {
                                val color: String = it["color"]
                                val item = ItemStack(Material.getMaterial("${color.uppercase()}_DYE")!!).apply {
                                    itemMeta = itemMeta.apply {
                                        setCustomModelData(1)
                                    }
                                }
                                player.inventory.addItem(item)
                            }
                        }
                        then("house") {
                            executes {
                                val color: String = it["color"]
                                val item = ItemStack(Material.getMaterial("${color.uppercase()}_DYE")!!).apply {
                                    itemMeta = itemMeta.apply {
                                        setCustomModelData(2)
                                    }
                                }
                                player.inventory.addItem(item)
                            }
                        }
                        then("road") {
                            executes {
                                val color: String = it["color"]
                                val item = ItemStack(Material.getMaterial("${color.uppercase()}_DYE")!!).apply {
                                    itemMeta = itemMeta.apply {
                                        setCustomModelData(3)
                                    }
                                }
                                player.inventory.addItem(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

class HexagonScheduler(val manager: CatanManager, val loc: Location, val count: Int, val centerLoc: Location) : Runnable {
    val vec = Vector(1.0, 0.0, 0.0)
    private var ticks = 0
    private var centerLocs = arrayListOf<Location>()

    override fun run() {
        ticks++
        when (ticks) {
            1 -> {
                val centerLoc = centerLoc.clone()
                centerLoc.apply {
                    z += count * sqrt(3.0) * 2
                }
                repeat(5) {
                    centerLocs += centerLoc.clone()
                    if (it <= 3) centerLoc.z -= count * sqrt(3.0)
                }

                val left = centerLoc.clone().apply {
                    z += count * sqrt(3.0) / 2
                    x -= count / 2 * 3
                }
                val right = centerLoc.clone().apply {
                    z += count * sqrt(3.0) / 2
                    x += count / 2 * 3
                }

                repeat(4) {
                    centerLocs += left.clone()
                    centerLocs += right.clone()
                    if (it <= 2) {
                        left.z += count * sqrt(3.0)
                        right.z += count * sqrt(3.0)
                    }
                }

                left.apply {
                    z -= count * sqrt(3.0) / 2
                    x -= count / 2 * 3
                }
                right.apply {
                    z -= count * sqrt(3.0) / 2
                    x += count / 2 * 3
                }

                repeat(3) {
                    centerLocs += left.clone()
                    centerLocs += right.clone()
                    if (it <= 1) {
                        left.z -= count * sqrt(3.0)
                        right.z -= count * sqrt(3.0)
                    }
                }

                repeat(6) {
                    manager.registerClickableGround(loc)
                    loc.block.type = Material.IRON_BLOCK
                    repeat(count) {
                        if (loc.block.type != Material.IRON_BLOCK) loc.block.type = Material.STONE
                        loc.add(vec)
                    }
                    vec.rotateAroundY((60.0).toRadians())
                }
            }
            in 2..5 -> {
                loc.apply {
                    z += count * sqrt(3.0)
                }

                repeat(6) {
                    manager.registerClickableGround(loc)
                    loc.block.type = Material.IRON_BLOCK
                    repeat(count) {
                        if (loc.block.type != Material.IRON_BLOCK) loc.block.type = Material.STONE
                        loc.add(vec)
                    }
                    vec.rotateAroundY((60.0).toRadians())
                }
            }
            6 -> {
                loc.apply {
                    z += count * sqrt(3.0) / 2
                    x -= count / 2 * 3
                }
            }
            in 7..10, in 16..19 -> {
                loc.apply {
                    z -= count * sqrt(3.0)
                }

                repeat(6) {
                    manager.registerClickableGround(loc)
                    loc.block.type = Material.IRON_BLOCK
                    repeat(count) {
                        if (loc.block.type != Material.IRON_BLOCK) loc.block.type = Material.STONE
                        loc.add(vec)
                    }
                    vec.rotateAroundY((60.0).toRadians())
                }
            }
            11 -> {
                loc.apply {
                    x -= count / 2 * 3
                    z -= count * sqrt(3.0) / 2
                }
            }
            in 12..14, in 21..23 -> {
                loc.apply {
                    z += count * sqrt(3.0)
                }

                repeat(6) {
                    manager.registerClickableGround(loc)
                    loc.block.type = Material.IRON_BLOCK
                    repeat(count) {
                        if (loc.block.type != Material.IRON_BLOCK) loc.block.type = Material.STONE
                        loc.add(vec)
                    }
                    vec.rotateAroundY((60.0).toRadians())
                }
            }
            15 -> {
                loc.apply {
                    x += count / 2 * 9
                    z += count * sqrt(3.0) * 3 / 2
                }
            }
            20 -> {
                loc.apply {
                    x += count / 2 * 3
                    z -= count * sqrt(3.0) / 2
                }
            }
            24 -> randomMap()
        }
    }

    private fun randomMap() {
        centerLocs.forEach {
            if (it.distance(centerLoc) > 8) {
                manager.registerRegion(it)
            }
        }
        manager.registerDessert(centerLoc)
    }
}