package io.github.monull.catan

import io.github.monull.catan.plugin.CatanPlugin
import org.bukkit.Location

class CatanManager(val plugin: CatanPlugin) {
    var regions = arrayListOf<CatanRegion>()

    fun registerRegion(location: Location) {
        regions += CatanRegion(this, location).apply { createStructure() }
    }

    fun registerDessert(location: Location) {
        regions += CatanRegion(this, location, true).apply { createStructure() }
    }
}