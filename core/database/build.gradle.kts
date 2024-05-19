plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.realm)
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.database.realm.base)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
            }
        }
    }


}
android {
    namespace = "database"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
    }

}