package io.github.monull.catan

import io.github.monull.catan.plugin.CatanPlugin
import io.github.monull.catan.process.CatanProcess
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CatanManager(val plugin: CatanPlugin) {
    var regions = arrayListOf<CatanRegion>()
    var dessert: CatanRegion? = null
    var grounds = arrayListOf<Location>()
    var roadGrounds = arrayListOf<Location>()

    val fakeEntityServer = FakeEntityServer.create(plugin).apply {
        plugin.server.pluginManager.registerEvents(object: Listener {
            @EventHandler
            fun onJoin(event: PlayerJoinEvent) {
                addPlayer(event.player)
            }
            @EventHandler
            fun onQuit(event: PlayerQuitEvent) {
                removePlayer(event.player)
            }
        }, plugin)
        plugin.server.scheduler.runTaskTimer(plugin, this::update, 0L, 1L)
    }

    fun resetMap() {
        regions.clear()
    }

    fun registerClickableGround(loc: Location) {
        grounds += loc.clone()
    }

    fun addRoadGround(loc: Location) {
        roadGrounds += loc.clone()
    }

    fun registerRegion(location: Location) {
        regions += CatanRegion(this, location).apply { createStructure() }
    }

    fun registerDessert(location: Location) {
        CatanRegion(this, location, true).apply { createStructure() }.let { region ->
            regions += region
            this.dessert = region
        }
    }

    var process: CatanProcess? = null

    fun startProcess() {
        if (process != null) return

        process = CatanProcess(plugin, this)
    }

    fun stopProcess() {
        if (process == null) return
        process?.unregister()
        process = null
    }
}