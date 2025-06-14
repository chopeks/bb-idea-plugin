plugins {
	id("java")
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.intellij)
	alias(libs.plugins.jetbrainsCompose)
	alias(libs.plugins.compose.compiler)
}

group = "com.chopeks"
version = "1.1.0"

configurations.all {
	exclude("org.jetbrains.compose.material")
}

repositories {
	mavenCentral()
	intellijPlatform {
		defaultRepositories()
	}
	gradlePluginPortal()
	google()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	maven("https://packages.jetbrains.team/maven/p/kpm/public")
}

sourceSets["main"].java.srcDirs("src/main/kotlin", "src/main/gen")

dependencies {
	intellijPlatform {
		intellijIdeaCommunity("2025.1")
		bundledPlugin("com.intellij.java")
	}
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

intellijPlatform {
	buildSearchableOptions = true
	pluginConfiguration {
		ideaVersion {
			sinceBuild = "251"
			untilBuild = "999"
		}

		changeNotes = """
            Initial version
        """.trimIndent()
	}
}

tasks {
	withType<JavaCompile> {
		sourceCompatibility = "21"
		targetCompatibility = "21"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "21"
	}
}

kotlin {
	jvmToolchain(21)
	sourceSets {
		all {
			languageSettings {
				optIn("org.jetbrains.jewel.ExperimentalJewelApi")
				optIn("androidx.compose.ui.ExperimentalComposeUiApi")
			}
		}
	}
}