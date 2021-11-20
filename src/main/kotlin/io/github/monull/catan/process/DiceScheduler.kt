package io.github.monull.catan.process

import io.github.monull.catan.CatanManager
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.math.toRadians
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.EulerAngle
import kotlin.random.Random

class DiceScheduler(val manager: CatanManager) {

    var total = 0
    lateinit var dice1: FakeEntity
    lateinit var dice2: FakeEntity
    var task: BukkitTask? = null

    fun initialize() {
        val loc = manager.dessert?.location!!
        val loc1 = loc.clone().add(2.0, 1.0, 2.0)
        val loc2 = loc.clone().add(-2.0, 1.0, -2.0)
        dice1 = manager.fakeEntityServer.spawnEntity(loc1, ArmorStand::class.java).apply {
            updateMetadata<ArmorStand> {
                isInvisible = true
                isMarker = true
                setGravity(false)
            }
            updateEquipment {
                helmet = ItemStack(Material.PAPER).apply {
                    this.itemMeta = itemMeta.apply {
                        setCustomModelData(1)
                    }
                }
            }
        }
        dice2 = manager.fakeEntityServer.spawnEntity(loc2, ArmorStand::class.java).apply {
            updateMetadata<ArmorStand> {
                isInvisible = true
                isMarker = true
                setGravity(false)
            }
            updateEquipment {
                helmet = ItemStack(Material.PAPER).apply {
                    this.itemMeta = itemMeta.apply {
                        setCustomModelData(1)
                    }
                }
            }
        }
    }

    public fun rotate() {
        val task = DiceRotateScheduler(this, dice1, dice2)
        Bukkit.getScheduler().runTaskTimer(manager.plugin, task, 0L, 1L).let { b ->
            task.task = b
            this.task = b
        }
    }

    public fun terminate() {
        this.task?.cancel()
    }
}

class DiceRotateScheduler(val dice: DiceScheduler, val dice1: FakeEntity, val dice2: FakeEntity) : Runnable {
    lateinit var task: BukkitTask
    private var ticks = 0

    private var speed = 1.0

    val rspeedx1 = Random.nextInt().toDouble().toRadians()
    val rspeedy1 = Random.nextInt().toDouble().toRadians()
    val rspeedz1 = Random.nextInt().toDouble().toRadians()

    val rspeedx2 = Random.nextInt().toDouble().toRadians()
    val rspeedy2 = Random.nextInt().toDouble().toRadians()
    val rspeedz2 = Random.nextInt().toDouble().toRadians()

    override fun run() {
        if (ticks <= 20) {
            speed -= 0.05
            dice1.move(0.0, speed, 0.0)
            dice2.move(0.0, speed, 0.0)
        }
        if (ticks in 21..38) {
            speed -= 0.05
            dice1.move(0.0, speed, 0.0)
            dice2.move(0.0, speed, 0.0)
        }
        if (++ticks <= 38) {
            dice1.updateMetadata<ArmorStand> {
                headPose = EulerAngle(rspeedx1, rspeedy1, rspeedz1)
            }
            dice2.updateMetadata<ArmorStand> {
                headPose = EulerAngle(rspeedx2, rspeedy2, rspeedz2)
            }
        } else {
            val numbers = arrayListOf(1, 2, 3, 4, 5, 6)
            val number1 = numbers.random()
            dice1.updateMetadata<ArmorStand> {
                when (number1) {
                    1 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 180.0.toRadians())
                    2 -> headPose = EulerAngle(0.0.toRadians(), 0.0, 90.0.toRadians())
                    3 -> headPose = EulerAngle(180.0.toRadians(), 0.0, 180.0.toRadians())
                    4 -> headPose = EulerAngle(0.0.toRadians(), 0.0, 180.0.toRadians())
                    5 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 270.0.toRadians())
                    6 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 0.0.toRadians())
                }
            }
            val number2 = numbers.random()
            dice2.updateMetadata<ArmorStand> {
                when (number2) {
                    1 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 180.0.toRadians())
                    2 -> headPose = EulerAngle(0.0.toRadians(), 0.0, 90.0.toRadians())
                    3 -> headPose = EulerAngle(180.0.toRadians(), 0.0, 180.0.toRadians())
                    4 -> headPose = EulerAngle(0.0.toRadians(), 0.0, 180.0.toRadians())
                    5 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 270.0.toRadians())
                    6 -> headPose = EulerAngle(90.0.toRadians(), 0.0, 0.0.toRadians())
                }
            }
            dice.total = number1 + number2
            task.cancel()
        }
    }
}