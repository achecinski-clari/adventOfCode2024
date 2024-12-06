plugins {
    kotlin("jvm") version "2.0.21"
}

group = "checinski.adam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.strikt:strikt-core:0.34.0")
    implementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}