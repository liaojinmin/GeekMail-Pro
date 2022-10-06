package me.geek.mail.modules.settings


/**
 * 作者: 老廖
 * 时间: 2022/10/5
 *
 **/

val isBundleMeta by lazy {
    try {
        Class.forName("org/bukkit/inventory/meta/BundleMeta")
        true
    } catch (a: NoClassDefFoundError) {
        false
    }
    catch (b: ClassNotFoundException) {
        false
    }
}

