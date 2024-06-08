plugins {
    id("io.github.goooler.shadow") version "8.1.7"
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
    implementation("com.github.Xiao-MoMi:Sparrow-Heart:${rootProject.properties["sparrow_heart_version"]}")
    implementation("net.kyori:adventure-platform-bukkit:${rootProject.properties["adventure_platform_version"]}")
    implementation("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    implementation("com.saicone.rtag:rtag:${rootProject.properties["rtag_version"]}")
    implementation("com.saicone.rtag:rtag-item:${rootProject.properties["rtag_version"]}")

    compileOnly(project(":loader"))
    compileOnly("org.incendo:cloud-core:${rootProject.properties["cloud_core_version"]}")
    compileOnly("org.incendo:cloud-minecraft-extras:${rootProject.properties["cloud_minecraft_extras_version"]}")
    compileOnly("org.incendo:cloud-paper:${rootProject.properties["cloud_paper_version"]}")
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:${rootProject.properties["mojang_brigadier_version"]}")
    compileOnly("org.bstats:bstats-bukkit:${rootProject.properties["bstats_version"]}")
}

tasks {
    shadowJar {
        archiveFileName.set("sparrow-bukkit.jarinjar")
        relocate("net.kyori", "net.momirealms.sparrow.libraries")
        relocate("org.incendo", "net.momirealms.sparrow.libraries")
        relocate("dev.dejvokep", "net.momirealms.sparrow.libraries")
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
        relocate ("net.momirealms.sparrow.heart", "net.momirealms.sparrow.bukkit.nms")
        relocate ("com.saicone.rtag", "net.momirealms.sparrow.libraries.rtag")
    }
}

artifacts {
    archives(tasks.shadowJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}