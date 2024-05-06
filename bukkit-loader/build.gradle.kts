val commitID: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":loader"))
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName = "Sparrow-Bukkit-${rootProject.properties["project_version"]}-${commitID}.jar"
        destinationDirectory.set(file("$rootDir/target"))
        from(project(":bukkit").tasks.shadowJar.get().archiveFile)
    }
}

artifacts {
    archives(tasks.shadowJar)
}