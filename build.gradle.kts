plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "1.9.24"
	id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.chopeks"
version = "1.0.0"

repositories {
	mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
	version.set("2024.3.2.2")
	type.set("IC") // Target IDE Platform

	plugins.set(listOf("com.intellij.java"))
}

sourceSets["main"].java
	.srcDirs("src/main/kotlin", "src/main/gen")

tasks {
	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}

	buildSearchableOptions {
		enabled = false
	}

	patchPluginXml {
		sinceBuild.set("242")
	}

//  signPlugin {
//    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
//    privateKey.set(System.getenv("PRIVATE_KEY"))
//    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
//  }
//
//  publishPlugin {
//    token.set(System.getenv("PUBLISH_TOKEN"))
//  }
}
