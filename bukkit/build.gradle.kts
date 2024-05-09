plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.xenondevs.xyz/releases/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(project(":common"))
    compileOnly(project(":loader"))
    compileOnly("com.github.Xiao-MoMi:Sparrow-Heart:${rootProject.properties["sparrow_heart_version"]}")
    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-platform-bukkit:${rootProject.properties["adventure_platform_version"]}")
    compileOnly("org.incendo:cloud-paper:${rootProject.properties["cloud_paper_version"]}")
    compileOnly("com.mojang:brigadier:${rootProject.properties["mojang_brigadier_version"]}")
    compileOnly("de.tr7zw:item-nbt-api:${rootProject.properties["nbt_api_version"]}")
    compileOnly("org.bstats:bstats-bukkit:${rootProject.properties["bstats_version"]}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks {
    shadowJar {
        archiveFileName.set("sparrow-bukkit.jarinjar")
        dependencies {
            include(project(":common"))
        }
        relocate("net.kyori", "net.momirealms.sparrow.libraries")
        relocate("org.incendo", "net.momirealms.sparrow.libraries")
        relocate("dev.dejvokep", "net.momirealms.sparrow.libraries")
        relocate("de.tr7zw.changeme", "net.momirealms.sparrow.libraries")
        relocate("net.bytebuddy", "net.momirealms.sparrow.libraries.bytebuddy")
        relocate ("org.apache.commons.pool2", "net.momirealms.sparrow.libraries.commonspool2")
        relocate ("com.mysql", "net.momirealms.sparrow.libraries.mysql")
        relocate ("org.mariadb", "net.momirealms.sparrow.libraries.mariadb")
        relocate ("com.zaxxer.hikari", "net.momirealms.sparrow.libraries.hikari")
        relocate ("com.mongodb", "net.momirealms.sparrow.libraries.mongodb")
        relocate ("org.bson", "net.momirealms.sparrow.libraries.bson")
        relocate ("org.bstats", "net.momirealms.sparrow.libraries.bstats")
        relocate ("io.lettuce", "net.momirealms.sparrow.libraries.lettuce")
        relocate ("io.leangen.geantyref", "net.momirealms.sparrow.libraries.geantyref")
        relocate ("com.github.benmanes.caffeine", "net.momirealms.sparrow.libraries.caffeine")
    }
}

artifacts {
    archives(tasks.shadowJar)
}
