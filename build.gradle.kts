        plugins {
            kotlin("jvm") version "2.0.0"
            kotlin("plugin.serialization") version "2.0.0"
            id("io.ktor.plugin") version "3.0.0"
        }

application {
    mainClass.set("com.delivery.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-server-websockets:3.0.0")
    implementation("io.ktor:ktor-server-auth:3.0.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.0.0")
    implementation("io.ktor:ktor-server-status-pages:3.0.0")
    implementation("io.ktor:ktor-server-call-logging:3.0.0")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.auth0:java-jwt:4.4.0")

    // Сериализация
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Корутины и Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // База данных (Exposed + PostgreSQL)
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.55.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.5.8")

    // Тесты
    testImplementation("io.ktor:ktor-server-test-host:3.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}