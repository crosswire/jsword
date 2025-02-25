plugins {
    `java-library`
    `maven-publish`
//    id("org.jetbrains.kotlin.jvm") version '1.4.10'
}


tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Test>() {
    // Enforce en-US locale, as many unit tests are locale-dependant.
    systemProperty("user.language", "en")
    systemProperty("user.country", "US")
}

group = "org.crosswire"
version = "2.3-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.apache.commons:commons-compress:1.12")

    implementation("org.jdom:jdom2:2.0.6.1")
    implementation("org.apache.lucene:lucene-analyzers-common:4.7.0")
    implementation("org.apache.lucene:lucene-queryparser:4.7.0")
    implementation("org.apache.lucene:lucene-analyzers-smartcn:4.7.0")

    implementation("org.slf4j:slf4j-api:1.7.6")

    testImplementation("org.slf4j:slf4j-simple:1.7.6")
    testImplementation("junit:junit:4.13")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            }
        }
    }

