plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    implementation(kotlin("stdlib"))

    implementation("io.github.monun:kommand-api:2.6.5")
    implementation("io.github.monun:tap-api:4.1.10")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testImplementation("org.mockito:mockito-core:3.6.28")
}

project.extra.set("packageName", name.replace("-", ""))
project.extra.set("pluginName", name.split('-').joinToString("") { it.capitalize() })

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
            expand(project.extra.properties)
        }
    }

    test {
        useJUnitPlatform()
    }

    create<Jar>("paperJar") {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("") // For bukkit plugin update

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }

    shadowJar {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.extra.properties["pluginName"].toString())
        archiveVersion.set("") // For bukkit plugin update

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}