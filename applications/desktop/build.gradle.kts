import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
}
kotlin {
    jvm{
        jvmToolchain(17)
        withJava()
    }
    sourceSets{
        val jvmMain by getting{
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(project(":feature:auth"))
                implementation(project(":common:ui"))
                implementation(project(":feature:navigation"))
                implementation(project(":feature:product_catalog"))
            }
        }
    }


}
compose.desktop{
    application{
        mainClass="Application"
        nativeDistributions{
            targetFormats(TargetFormat.Exe)
            packageName="desktop"
            version="1.0.0"
        }
    }
}