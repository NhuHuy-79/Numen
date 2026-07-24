plugins {
    id("numen.jitpack")
    id("maven-publish")
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.nhuhuy.numen.android"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.github.nhuhuy"
                artifactId = "numen-android"
                version = "1.0.0"

                pom {
                    name.set(project.name)
                    description.set("Numen Library - ${project.name}")
                }
            }
        }
    }
}

dependencies {
    api(project(":numen:core"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
