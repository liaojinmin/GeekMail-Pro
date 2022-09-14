package me.geek.mail.common.menu.sub

import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
enum class IconType {
    TEXT, DELETE, BIND, LAST_PAGE, NEXT_PAGE, NORMAL;


    open fun getIconType(type: String?): IconType {
        if (type != null) {
            when (type.uppercase(Locale.ROOT)) {
                "TEXT" -> return TEXT
                "DELETE" -> return DELETE
                "BIND" -> return BIND
                "LAST_PAGE" -> return LAST_PAGE
                "NEXT_PAGE" -> return NEXT_PAGE
            }
        }
        return NORMAL
    }
}