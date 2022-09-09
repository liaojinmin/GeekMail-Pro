package me.geek.mail.utils


import org.bukkit.entity.Player
import kotlin.math.roundToInt

/**
 * 作者: 老廖
 * 时间: 2022/8/21
 */
object Experience {
    /**
     * 扣除玩家指定的经验值
     * @param player 目标玩家
     * @param exp 要扣除的值
     */
    fun takeTotalExperience(player: Player, exp: Int) {
        val exp2 = getTotalExperience(player)
        player.level = 0
        player.exp = 0.0f
        player.totalExperience = 0
        player.giveExp(exp2 - exp)
    }

    /**
     * 给予玩家指定的经验值
     * @param player 目标玩家
     * @param exp 要给予的值
     */
    fun giveTotalExperience(player: Player, exp: Int) {
        val exp2 = getTotalExperience(player)
        player.level = 0
        player.exp = 0.0f
        player.totalExperience = 0
        player.giveExp(exp2 + exp)
    }
    fun hasTotalExperience(player: Player, exp: Int, take: Boolean = false): Boolean {
        if (getTotalExperience(player) >= exp) {
            if (take) {
                takeTotalExperience(player, exp)
            }
            return true
        }
       return false
    }

    /**
     * 设置玩家经验值为指定值
     * @param player 目标玩家
     * @param exp 要设置的值
     */
    fun setTotalExperience(player: Player, exp: Int) {
        player.level = 0
        player.exp = 0.0f
        player.totalExperience = 0
        player.giveExp(exp)
    }

    /**
     * 获取玩家的经验值
     * @param player 麻痹玩家
     * @return 经验值
     */
    fun getTotalExperience(player: Player): Int {
        var experience = (getExperienceAtLevel(player.level) * player.exp).roundToInt()
        var currentLevel = player.level
        while (currentLevel > 0) {
            currentLevel--
            experience += getExperienceAtLevel(currentLevel)
        }
        if (experience < 0) {
            experience = 0
        }
        return experience
    }

    private fun getExperienceAtLevel(level: Int): Int {
        if (level <= 15) {
            return (level shl 1) + 7
        }
        return if (level <= 30) {
            level * 5 - 38
        } else level * 9 - 158
    }
}