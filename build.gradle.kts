plugins {
    java
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "jp.gr.java_conf.stardiopside.rsnotes"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        dependencies {
            dependency("com.github.springtestdbunit:spring-test-dbunit:1.3.0")
            dependency("org.dbunit:dbunit:2.8.0")
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }
}
