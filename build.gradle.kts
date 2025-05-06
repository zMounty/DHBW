plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.netty:netty-all:4.1.121.Final")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
}

tasks.test {
    useJUnitPlatform()
}

