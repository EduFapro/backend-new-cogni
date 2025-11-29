import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"           // Kotlin 2.x for Ktor 3
    kotlin("plugin.serialization") version "2.1.20"
    application
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktor = "3.2.3"

dependencies {
    // Use the Ktor BOM to keep versions in sync
    implementation(platform("io.ktor:ktor-bom:$ktor"))

    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")

    implementation("org.postgresql:postgresql:42.7.7")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    implementation("org.postgresql:postgresql:42.7.3")
}
