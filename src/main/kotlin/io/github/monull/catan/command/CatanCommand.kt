package io.github.monull.catan.command

import io.github.monull.catan.plugin.CatanPlugin
import io.github.monull.dev.command.PluginKommand
import io.github.monull.dev.math.toRadians
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.sqrt

object CatanCommand {
    fun register(kommand: PluginKommand, plugin: CatanPlugin) {
        kommand.apply {
            register("catan") {
                val colors = dynamic { _, input ->
                    listOf("red", "yellow", "blue", "green").firstOrNull { it == input }
                }.apply {
                    suggests {
                        suggest(listOf("red", "yellow", "blue", "green"))
                    }
                }
                then("map") {
                    requires { playerOrNull != null }
                    executes {
                        val count = 10
                        val loc = player.location.apply {
                            z -= count * sqrt(3.0) / 2 * 3
                            x -= count / 2
                        }

                        plugin.server.scheduler.runTaskTimer(plugin, HexagonScheduler(loc, count), 0L, 1L)
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

class HexagonScheduler(val loc: Location, val count: Int) : Runnable {
    val vec = Vector(1.0, 0.0, 0.0)
    private var ticks = 0

    override fun run() {
        ticks++
        when (ticks) {
            1 -> {
                repeat(6) {
                    repeat(count) {
                        loc.block.type = Material.STONE
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
                    repeat(count) {
                        loc.block.type = Material.STONE
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
                    repeat(count) {
                        loc.block.type = Material.STONE
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
                    repeat(count) {
                        loc.block.type = Material.STONE
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
        }
    }
}