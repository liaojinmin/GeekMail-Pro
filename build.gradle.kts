val taboolibVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("io.izzel.taboolib") version "1.42"
    id("org.jetbrains.dokka") version "1.7.20"
}

taboolib {
    install(
        "common",
        "common-5",
        "module-metrics",
        "platform-bukkit",
        "module-configuration",
        "module-chat",
        "module-lang",
        "module-kether",
        "module-metrics",
        "module-nms",
        "module-nms-util",
        "expansion-geek-tool"

    )
    description {
        contributors {
            name("HSDLao_liao")
        }
        dependencies {
            bukkitApi("1.13")
            name("PlaceholderAPI").optional(true)
            name("Vault").optional(true)
            name("PlayerPoints").optional(true)
            name("ItemsAdder").optional(true)
            name("TrHologram").optional(true)
        }
    }

    relocate("me.geek.mail", group.toString())
    relocate("com.zaxxer.hikari", "com.zaxxer.hikari_4_0_3_mail")
    relocate("javax.mail", "javax.mail_1_5_0_mail")
    classifier = null
    version = taboolibVersion
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://jitpack.io")
    maven("https://maven.pkg.github.com/LoneDev6/API-ItemsAdder")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

dependencies {

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
    taboo(project(":Scheduler")) { isTransitive = false }

    compileOnly(kotlin("stdlib"))
    // Libraries
    compileOnly(fileTree("libs"))
    // Server Core
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
    compileOnly("ink.ptms.core:v11604:11604")

    // Hook Plugins
    compileOnly("javax.mail:javax.mail-api:1.6.2") { isTransitive = false }
    compileOnly("javax.activation:activation:1.1.1") { isTransitive = false }

    compileOnly("me.clip:placeholderapi:2.10.9") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:-SNAPSHOT") { isTransitive = false }
    compileOnly("org.black_ixx:playerpoints:3.1.1") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.2.3c")

}

