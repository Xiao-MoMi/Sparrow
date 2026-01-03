val commitID: String by project

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    implementation(project(":loader"))
    implementation(project(":common"))
    implementation(project(":bukkit"))

    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName = "Sparrow-Bukkit-${rootProject.properties["project_version"]}-${commitID}.jar"
        destinationDirectory.set(file("$rootDir/target"))
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    dependsOn(tasks.clean)
}