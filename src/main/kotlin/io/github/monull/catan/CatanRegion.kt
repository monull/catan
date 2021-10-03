package io.github.monull.catan

import org.bukkit.Location
import kotlin.random.Random.Default.nextInt

class CatanRegion(val manager: CatanManager, val location: Location, val dessert: Boolean = false) {
    var regionType: CatanRegionType? = null
    init {
        regionType = if (dessert) {
            CatanRegionType.DESSERT
        } else {
            when (nextInt(5)) {
                0 -> CatanRegionType.BRICKS
                1 -> CatanRegionType.HAY
                2 -> CatanRegionType.FOREST
                3 -> CatanRegionType.SHEEPS
                else -> CatanRegionType.IRON
            }
        }
    }

    fun createStructure() {
        regionType!!.structure().apply {
            createStructure(manager, location)
        }
    }
}