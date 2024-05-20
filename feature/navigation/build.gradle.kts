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
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.materialIconsExtended)
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(libs.windowSize)
//                implementation(compose.components.resources)
                //view-model
                implementation(libs.kotlinx.coroutines.core)
                implementation(project(":feature:product_catalog"))
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha03")
            }
        }
        val androidMain by getting{
            dependencies {


            }
        }
        val desktopMain by getting{
            dependencies {
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
    }



}
android {
    namespace = "navigation"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
    }

}