package io.github.monull.catan

import com.google.common.collect.ImmutableList
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class CatanStructure {

    open var task: BukkitTask? = null

    internal open fun createStructure(manager: CatanManager, location: Location) {}

    internal open fun buildStructure(manager: CatanManager, location: Location) {}

    fun CatanStructure.fillBlocks(manager: CatanManager, loc: Location, type: Material) {
        val task = FillScheduler(manager, loc, type)
        task.structure = this
        task.task = Bukkit.getServer().scheduler.runTaskTimer(manager.plugin, task, 0L, 2L)
    }

    inner class FillScheduler(val manager: CatanManager, val loc: Location, val type: Material): Runnable {
        val blockQueue = LinkedList<Block>()
        var ticks = 0
        lateinit var structure: CatanStructure
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
                val task = BuildStructure(manager, loc)
                task.structure = structure
                structure.task = Bukkit.getScheduler().runTaskTimer(manager.plugin, task, 0L, 2L)
            }
        }
    }

    inner class BuildStructure(val manager: CatanManager, val loc: Location) : Runnable {
        lateinit var structure: CatanStructure

        override fun run() {
            structure.buildStructure(manager, loc)
        }
    }

    class HayStructure : CatanStructure() {
        override var task: BukkitTask? = null
        private var ticks = 0

        override fun createStructure(manager: CatanManager, location: Location) {
            fillBlocks(manager, location, Material.DIRT_PATH)
        }

        override fun buildStructure(manager: CatanManager, location: Location) {
            ticks++
            when (ticks) {
                1 -> {
                    location.apply { y += 1 }
                    location.block.getRelative(-2, 0, 3).apply {
                        type = Material.HAY_BLOCK
                        val list = arrayListOf<Block>()
                        list += this
                        this.getRelative(0, 0, 1).apply {
                            type = Material.HAY_BLOCK
                            list += this
                        }
                        this.getRelative(0, 0, 2).apply {
                            type = Material.HAY_BLOCK
                            list += this
                        }
                        list.forEach { block ->
                            block.getRelative(1, 0, 0).type = Material.HAY_BLOCK
                            block.getRelative(2, 0, 0).type = Material.HAY_BLOCK
                            block.getRelative(3, 0, 0).type = Material.HAY_BLOCK
                            block.getRelative(4, 0, 0).type = Material.HAY_BLOCK
                            block.getRelative(5, 0, 0).type = Material.HAY_BLOCK
                        }
                    }
                    location.block.getRelative(0, 0, -3).apply {
                        val listX = arrayListOf(0, -1, -2)
                        val listZ = arrayListOf(0, -1, -2, -3, -4)
                        type = Material.YELLOW_WOOL
                        listX.forEach { dx ->
                            listZ.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.YELLOW_WOOL
                            }
                        }
                        getRelative(1, 0, -1).type = Material.YELLOW_WOOL
                        getRelative(1, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(1, 0, -3).type = Material.YELLOW_WOOL
                        getRelative(-3, 0, -1).type = Material.YELLOW_WOOL
                        getRelative(-3, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(-3, 0, -3).type = Material.YELLOW_WOOL
                    }
                }
                2 -> {
                    location.block.getRelative(0, 1, -4).apply {
                        val dx = arrayListOf(0, -1, -2)
                        val dz = ImmutableList.copyOf(dx)
                        dx.forEach { dx ->
                            dz.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.YELLOW_WOOL
                            }
                        }
                    }
                    location.block.getRelative(-1, 1, 3).apply {
                        val listX = arrayListOf(0, 1, 2, 3)
                        val listZ = arrayListOf(0, 1, 2)
                        listX.forEach { dx ->
                            listZ.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.HAY_BLOCK
                            }
                        }
                    }
                }
                3 -> {
                    location.block.getRelative(0, 2, -4).apply {
                        val dx = arrayListOf(0, -1, -2)
                        val dz = ImmutableList.copyOf(dx)
                        dx.forEach { dx ->
                            dz.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.RED_WOOL
                            }
                        }
                    }
                    location.block.getRelative(0, 2, 3).apply {
                        val listX = arrayListOf(0, 1)
                        val listZ = arrayListOf(0, 1, 2)
                        listX.forEach { dx ->
                            listZ.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.HAY_BLOCK
                            }
                        }
                    }
                }
                4 -> {
                    location.block.getRelative(0, 3, -4).apply {
                        val dx = arrayListOf(0, -1, -2)
                        val dz = ImmutableList.copyOf(dx)
                        dx.forEach { dx ->
                            dz.forEach { dz ->
                                getRelative(dx, 0, dz).type = Material.YELLOW_WOOL
                            }
                        }
                    }
                }
                5 -> {
                    location.block.getRelative(0, 4, -4).apply {
                        type = Material.YELLOW_WOOL
                        getRelative(0, 0, -1).type = Material.YELLOW_WOOL
                        getRelative(0, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(-1, 0, 0).type = Material.YELLOW_WOOL
                        getRelative(-1, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(-2, 0, 0).type = Material.YELLOW_WOOL
                        getRelative(-2, 0, -1).type = Material.YELLOW_WOOL
                        getRelative(-2, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(-1, 0, 1).type = Material.YELLOW_WOOL
                        getRelative(1, 0, 0).type = Material.YELLOW_WOOL
                        getRelative(1, 0, -1).type = Material.YELLOW_WOOL
                        getRelative(1, 0, -3).type = Material.YELLOW_WOOL
                        getRelative(-1, 0, -3).type = Material.YELLOW_WOOL
                        getRelative(-3, 0, -3).type = Material.YELLOW_WOOL
                        getRelative(-3, 0, -1).type = Material.YELLOW_WOOL
                    }
                }
                6 -> {
                    location.block.getRelative(1, 5, -3).apply {
                        type = Material.YELLOW_WOOL
                        getRelative(-2, 0, 0).type = Material.YELLOW_WOOL
                        getRelative(1, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(0, 0, -5).type = Material.YELLOW_WOOL
                        getRelative(-2, 0, -5).type = Material.YELLOW_WOOL
                        getRelative(-5, 0, -2).type = Material.YELLOW_WOOL
                        getRelative(-5, 0, -4).type = Material.YELLOW_WOOL
                    }
                }
            }
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