plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)

}
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    jvm("desktop"){
        jvmToolchain(17)
    }
    sourceSets{
        val commonMain by getting{
            dependencies {
                //For Compose multiplatform UI
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.materialIconsExtended)
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(libs.windowSize)

                //For coroutines
                implementation(libs.kotlinx.coroutines.core)

                //For navigation with compose
                implementation(libs.navigation)
                //For gemini
                implementation("dev.shreyaspatil.generativeai:generativeai-google:0.5.0-1.0.0")
                implementation(libs.lifecycle.viewmodel)

                //NetworkIO
                implementation(libs.kotlinx.coroutines.core)
                //network IO for image loading
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.coil3.network)
                implementation(libs.coil3)
                implementation(libs.coil3.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(project(":core:database"))
                implementation(project(":core:network"))



            }
        }
        val androidMain by getting{
            dependencies {


            }
        }
        val desktopMain by getting{
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
    }



}
android {
    namespace = "gemini"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
    }

}

