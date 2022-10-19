

plugins {
    kotlin("jvm")
}

val taboolibVersion: String by rootProject

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}



dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("redis.clients:jedis:4.2.2")
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
}

