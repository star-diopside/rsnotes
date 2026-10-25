plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.7")
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:4.1.2")
}
