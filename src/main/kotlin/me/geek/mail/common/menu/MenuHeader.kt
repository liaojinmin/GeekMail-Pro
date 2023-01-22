package me.geek.mail.common.menu

import me.geek.mail.common.menu.sub.MenuData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

/**
 * 作者: 老廖
 * 时间: 2022/12/12
 *
 **/
interface MenuHeader {
    /**
     * 菜单标题
     */
    var title: String

    /**
     *  菜单大小
     */
    var size: Int

    /**
     * 当前页面
     */
    var page: Int

    /**
     * 玩家对象
     */
    val player: Player

    /**
     * 菜单配置对象
     */
    val menuData: MenuData?
    get() = null


    /**
     * 打开菜单
     */
    fun openMenu()

    /**
     * 接收点击事件
     */
    fun onClick(event: InventoryClickEvent)
    /**
     * 接收关闭事件
     */
    fun onClose(event: InventoryCloseEvent)


}