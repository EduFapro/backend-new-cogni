plugins {
    kotlin("jvm") version "2.1.20" // Ktor 3 usa Kotlin 2.x
    application
}

kotlin { jvmToolchain(21) }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories { mavenCentral() }

val ktor = "3.2.3"

dependencies {
    // Ktor 3.x — todas as libs na MESMA versão
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-server-core:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    // Exposed — recomendo atualizar pelo menos p/ 0.61.0 (estável em 2025-04)
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")

    implementation("org.postgresql:postgresql:42.7.7")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("ch.qos.logback:logback-classic:1.5.18")
}
