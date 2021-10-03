package io.github.monull.catan.plugin

import io.github.monull.catan.CatanManager
import io.github.monull.catan.command.CatanCommand
import io.github.monull.dev.command.kommand
import org.bukkit.plugin.java.JavaPlugin

class CatanPlugin : JavaPlugin() {
    lateinit var catanManager: CatanManager

    override fun onEnable() {
        catanManager = CatanManager(this)
        setupKommand()
    }
    private fun setupKommand() {
        kommand {
            CatanCommand.register(this, this@CatanPlugin, catanManager)
        }
    }
}