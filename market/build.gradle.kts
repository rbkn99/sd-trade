import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.21"
    id("com.bmuschko.docker-remote-api") version "6.7.0"
}

group = "se.rbkn99"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    jar {
        enabled = true
        manifest {
            attributes["Main-Class"] = "se.rbkn99.ApplicationKt"
        }
    }
}

tasks.create("copyJar", Copy::class) {
    dependsOn("jar")
    from("build/libs/trade-market-${project.version}.jar")
    into("build/docker")
    rename { it.replace("-${project.version}", "") }
}

tasks.create("createDockerfile", Dockerfile::class) {
    dependsOn("copyJar")
    from("openjdk:8")
    addFile("trade-market.jar", "/app/trade-market.jar")
    exposePort(8080, 49152)
    defaultCommand("java", "-jar", "/app/trade-market.jar")
}

tasks.create("removeImage", DockerRemoveImage::class) {
    targetImageId("rbkn99/trade-market:latest")
}

tasks.create("buildImage", DockerBuildImage::class) {
    dependsOn("removeImage", "createDockerfile")
    images.add("rbkn99/trade-market:latest")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
