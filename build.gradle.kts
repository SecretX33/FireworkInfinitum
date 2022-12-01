plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.secretx33"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

val javaVersion = 17

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.checkerframework:checker-qual:3.25.0")
    implementation("com.github.cryptomorin:XSeries:9.1.0")
}

// Disables the normal jar task
tasks.jar { enabled = false }

// And enables shadowJar task
artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set("${rootProject.name}.jar")
    val dependencyPackage = "${rootProject.group}.dependencies.${rootProject.name.toLowerCase()}"
    relocate("com.cryptomorin.xseries", "$dependencyPackage.xseries")
    exclude("META-INF/**")
}

tasks.withType<JavaCompile> {
    options.apply {
        release.set(javaVersion)
        encoding = "UTF-8"
    }
}

tasks.processResources {
    outputs.upToDateWhen { false }
    val main_class = "${project.group}.${project.name.toLowerCase()}.${project.name}"
    expand("name" to project.name, "version" to project.version, "mainClass" to main_class)
}
