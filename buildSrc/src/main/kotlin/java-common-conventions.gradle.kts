plugins {
    java
    id("io.spring.dependency-management")
}

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
        dependency("org.dbunit:dbunit:3.5.2")
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

tasks.javadoc {
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
}
