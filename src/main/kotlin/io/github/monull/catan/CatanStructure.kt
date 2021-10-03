package io.github.monull.catan

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class CatanStructure {
    internal open fun createStructure(manager: CatanManager, location: Location) {}

    fun fillBlocks(manager: CatanManager, loc: Location, type: Material) {
        val task = FillScheduler(loc, type)
        task.task = Bukkit.getServer().scheduler.runTaskTimer(manager.plugin, task, 0L, 2L)
    }

    inner class FillScheduler(val loc: Location, val type: Material): Runnable {
        val blockQueue = LinkedList<Block>()
        var ticks = 0
        lateinit var task: BukkitTask

        override fun run() {
            ticks++
            when (ticks) {
                1 -> {
                    blockQueue.offer(loc.block)
                    loc.block.type = type
                }
            }
            blockQueue.poll().run {
                listOf(
                    getRelative(BlockFace.NORTH),
                    getRelative(BlockFace.EAST),
                    getRelative(BlockFace.SOUTH),
                    getRelative(BlockFace.WEST)
                )
            }.forEach {
                if (it.type == Material.AIR) {
                    blockQueue.offer(it)
                    it.type = type
                }
            }
            if (blockQueue.isEmpty()) {
                task.cancel()
            }
        }

    }

    class HayStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.DIRT_PATH)
        }
    }

    class SheepStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.GRASS_BLOCK)
        }
    }

    class ForestStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.GRASS_BLOCK)
        }
    }

    class DessertStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.SANDSTONE)
        }
    }

    class BricksStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.POLISHED_GRANITE)
        }
    }

    class IronStructure : CatanStructure() {
        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.CALCITE)
        }
    }
}