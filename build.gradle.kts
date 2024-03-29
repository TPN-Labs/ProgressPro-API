import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.gitlab.arturbosch.detekt.Detekt

object LibrariesVersion {
    const val assertj = "3.23.1"
    const val bCrypt = "0.4"
    const val bouncyCastle = "1.70"
    const val exposed = "0.40.1"
    const val googleApi = "2.0.1"
    const val firebase = "9.1.1"
    const val hikariCP = "5.0.1"
    const val jackson = "2.14.2"
    const val junit = "5.9.2"
    const val jwt = "0.11.5"
    const val koin = "3.3.1"
    const val kotlinTest = "1.7.20"
    const val ktor = "2.3.0"
    const val logback = "1.4.6"
    const val newRelic = "8.0.1"
    const val okHttp3 = "4.10.0"
    const val postgresSQL = "42.5.4"
    const val restAssured = "5.3.0"
    const val sentry = "6.16.0"
}

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    application
    jacoco
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/resources/detekt.yml")
    baseline = file("$projectDir/resources/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-serialization-jackson:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-serialization:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-auth-jwt:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-auth:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-call-logging:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-content-negotiation:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-core:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-cors:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-default-headers:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-tomcat:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-status-pages:${LibrariesVersion.ktor}")
    implementation("io.ktor:ktor-server-websockets:${LibrariesVersion.ktor}")

    implementation("org.jetbrains.exposed:exposed-core:${LibrariesVersion.exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${LibrariesVersion.exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${LibrariesVersion.exposed}")
    implementation("org.jetbrains.exposed:exposed-java-time:${LibrariesVersion.exposed}")

    implementation("io.insert-koin:koin-ktor:${LibrariesVersion.koin}")
    implementation("io.sentry:sentry:${LibrariesVersion.sentry}")
    implementation("com.newrelic.agent.java:newrelic-api:${LibrariesVersion.newRelic}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${LibrariesVersion.jackson}")
    implementation("org.mindrot:jbcrypt:${LibrariesVersion.bCrypt}")

    implementation("org.postgresql:postgresql:${LibrariesVersion.postgresSQL}")

    implementation("com.zaxxer:HikariCP:${LibrariesVersion.hikariCP}")
    implementation("ch.qos.logback:logback-classic:${LibrariesVersion.logback}")

    implementation("io.jsonwebtoken:jjwt-api:${LibrariesVersion.jwt}")

    testImplementation("org.assertj:assertj-core:${LibrariesVersion.assertj}")
    testImplementation("io.rest-assured:rest-assured:${LibrariesVersion.restAssured}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${LibrariesVersion.junit}")
    testImplementation("io.ktor:ktor-client-cio:${LibrariesVersion.ktor}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${LibrariesVersion.kotlinTest}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${LibrariesVersion.junit}")
}

application {
    mainClass.set("io.ktor.server.tomcat.EngineMain")
}

kotlin.sourceSets {
    main { kotlin.srcDir("src") }
    test { kotlin.srcDir("test") }
}
sourceSets {
    main { resources.srcDir("resources") }
    test { resources.srcDir("testresources") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ShadowJar>() {

}


tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}
