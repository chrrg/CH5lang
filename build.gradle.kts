plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
    }
}

apply(plugin="org.jetbrains.dokka")

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    moduleName.set("CH Compiler")
    outputDirectory.set(File(rootDir, "doc"))

    dokkaSourceSets {
        configureEach {
//            includes.from(File(rootDir, "CH语言.md"))
        }
    }
}