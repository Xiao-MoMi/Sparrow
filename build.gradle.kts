plugins {
    id("java")
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    tasks.processResources {
        filteringCharset = "UTF-8"
        filesMatching(arrayListOf("plugin.yml", "library-version.properties")) {
            expand(rootProject.properties)
        }
    }

    tasks.compileJava {
        dependsOn(tasks.clean)
    }
}
