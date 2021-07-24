apply(plugin = "org.springframework.boot")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.thymeleaf.extras:thymeleaf-extras-java8time")
    runtimeOnly("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    runtimeOnly("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.test {
    useJUnitPlatform()
}
