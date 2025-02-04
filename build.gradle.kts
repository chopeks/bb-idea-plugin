plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "1.9.24"
	id("org.jetbrains.intellij") version "1.17.3"
	id("org.jetbrains.compose") version "1.7.3"
}

group = "com.chopeks"
version = "1.0.0"

repositories {
	mavenCentral()
	google()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	maven("https://packages.jetbrains.team/maven/p/kpm/public")
}

configurations.all {
	exclude("org.jetbrains.compose.material")
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

dependencies {
	implementation("org.jetbrains.jewel:jewel-ide-laf-bridge-242:0.27.0")
	implementation(compose.desktop.currentOs) {
		exclude(group = "org.jetbrains.compose.material")
		exclude(group = "org.jetbrains.kotlinx")
	}
	implementation("androidx.lifecycle:lifecycle-runtime-desktop:2.8.7") {
		exclude(group = "org.jetbrains.kotlinx")
	}
	implementation("androidx.lifecycle:lifecycle-common-jvm:2.8.7") {
		exclude(group = "org.jetbrains.kotlinx")
	}
}

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

kotlin {
	jvmToolchain(17)
	sourceSets {
		all {
			languageSettings {
				optIn("org.jetbrains.jewel.ExperimentalJewelApi")
				optIn("androidx.compose.ui.ExperimentalComposeUiApi")
			}
		}
	}
}