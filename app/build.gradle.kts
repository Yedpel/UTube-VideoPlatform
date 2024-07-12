plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.utube"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.utube"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Picasso for image loading
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.android.exoplayer:exoplayer:2.16.0")

    // Room dependencies
    val roomVersion = "2.4.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") //try-swip

    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.3.1")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
}