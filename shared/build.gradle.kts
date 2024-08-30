
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.6.0"
}

group = "link.socket"
version = "0.0.2"

publishing {
    publications {
        create<MavenPublication>("mavenLocal") {
            from(components["kotlin"])

            groupId = group.toString()
            artifactId = "kore-ai"
            version = "0.0.2"

            pom {
                name.set("KoreAI")
                description.set("A Kotlin Multiplatform library for ...")
                url.set("https://github.com/yourcompany/mylibrary")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/yourcompany/mylibrary.git")
                    developerConnection.set("scm:git:ssh://git@github.com:yourcompany/mylibrary.git")
                    url.set("https://github.com/yourcompany/mylibrary")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "ossrh"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: ""
                password = project.findProperty("ossrhPassword") as String? ?: ""
            }
        }
    }
}

tasks {
    register("generateJavadocs", DokkaTask::class) {
        dokkaSourceSets {
            named("main") {
                noAndroidSdkLink.set(true)
            }
        }
    }
}

kotlin {
    androidTarget()
    jvm()

    val xcf = XCFramework()
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.aallam.openai:openai-client:3.8.2")
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("com.squareup.okio:okio:3.9.0")
                implementation("com.mikepenz:multiplatform-markdown-renderer:0.12.0")
                implementation("co.touchlab:kermit:2.0.4")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(kotlin("reflect"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.9.1")
                api("androidx.appcompat:appcompat:1.7.0")
                api("androidx.core:core-ktx:1.13.1")
                implementation("com.lordcodes.turtle:turtle:0.10.0")
                implementation("io.ktor:ktor-client-okhttp:2.3.6")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("com.lordcodes.turtle:turtle:0.10.0")
                implementation("io.ktor:ktor-client-okhttp:2.3.6")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.6")
            }
        }
    }

    androidTarget {
        publishLibraryVariants("release", "debug")
        publishLibraryVariantsGroupedByFlavor = true
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.get("main").compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "link.socket.kore"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

tasks.register("kotlinConfiguration") {
    val generatedSources = File(buildDir, "generated/kotlin/config")
    generatedSources.mkdirs()
    kotlin.sourceSets.commonMain.get().kotlin.srcDirs(generatedSources)

    val localProperties = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }

    val properties = localProperties.entries
        .filter { (key, _) -> (key as? String)?.contains(".") == false }
        .joinToString("\n") { (key, value) -> "\tconst val $key = \"$value\"" }

    val kotlinConfig = File(generatedSources, "KotlinConfig.kt")
    kotlinConfig.writeText(
        "package link.kore.shared.config\n\n" +
            "object KotlinConfig {\n" +
            "$properties\n" +
            "}\n"
    )
}

tasks.findByName("build")?.dependsOn(
    tasks.findByName("kotlinConfiguration")
)
