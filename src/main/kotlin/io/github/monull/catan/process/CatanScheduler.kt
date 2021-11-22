package io.github.monull.catan.process

import io.github.monull.catan.plugin.CatanPlugin
import io.github.monull.catan.ready.BuildTowns
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound

class CatanScheduler(val process: CatanProcess) : Runnable {

    private var task: Task? = TitleTask()

    override fun run() {
        if (task != null) {
            task = task?.execute()
        } else {
            process.stopProcess()
        }
    }

    interface Task {
        fun execute(): Task?
    }

    inner class TitleTask : Task {
        private var ticks = 0

        override fun execute(): Task {
            if (ticks == 0) {
                Bukkit.getOnlinePlayers().forEach { player ->
                    player.sendTitlePart(TitlePart.TITLE, text("${ChatColor.GOLD}Catan"))
                }
            }
            if (++ticks < 60) return this

            return CountdownTask()
        }
    }

    inner class CountdownTask : Task {
        private var ticks = 0
        override fun execute(): Task {
            if (ticks % 20 == 0) {
                Bukkit.getOnlinePlayers().forEach { player ->
                    player.sendTitlePart(TitlePart.TITLE, text(getNumberByTicks(ticks)))
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F)
                }
            }

            if (++ticks < 100) {
                return this
            }
            return CatanBuildTownTask()
        }

        private fun getNumberByTicks(ticks: Int): String {
            return when (val second = (100 - ticks) / 20) {
                5 -> "${ChatColor.BLUE}$second"
                4 -> "${ChatColor.AQUA}$second"
                3 -> "${ChatColor.YELLOW}$second"
                2 -> "${ChatColor.LIGHT_PURPLE}$second"
                1 -> "${ChatColor.RED}$second"
                else -> "${ChatColor.BOLD}${ChatColor.WHITE}START"
            }
        }
    }

    inner class CatanBuildTownTask : Task {
        val buildTownsProcess: BuildTowns = BuildTowns(process.manager)

        override fun execute(): Task {
            if (!buildTownsProcess.end) return this

            return CatanTask()
        }

    }

    inner class CatanTask : Task {
        private var ticks = 0
        override fun execute(): Task {
            if (ticks == 0) process.start()
            ticks++

            process.update()

            if (!process.gameEnd) return this

            return ResultTask()
        }
    }

    inner class ResultTask : Task {
        override fun execute(): Task? {
            return null
        }
    }

}