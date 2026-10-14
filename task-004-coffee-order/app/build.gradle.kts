plugins { id("com.android.application") }

android {
    namespace = "cz.matejretych.coffeeorder"
    compileSdk = 37
    defaultConfig {
        applicationId = "cz.matejretych.coffeeorder"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { viewBinding = true }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.core:core-ktx:1.16.0")
    testImplementation("junit:junit:4.13.2")
}
