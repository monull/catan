package io.github.monull.catan

import io.github.monull.catan.plugin.CatanPlugin
import io.github.monull.dev.fake.FakeEntityServer
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CatanManager(val plugin: CatanPlugin) {
    var regions = arrayListOf<CatanRegion>()
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

    fun registerRegion(location: Location) {
        regions += CatanRegion(this, location).apply { createStructure() }
    }

    fun registerDessert(location: Location) {
        regions += CatanRegion(this, location, true).apply { createStructure() }
    }
}