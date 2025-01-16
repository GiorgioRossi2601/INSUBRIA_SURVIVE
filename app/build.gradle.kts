plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.insubria_survive"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.insubria_survive"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        //dataBinding = true
    }
    packaging {
        resources {
            // Escludi il file duplicato
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.firestore)

    // Per l'autenticazione con gli account Google (OAuth 2.0)
    implementation ("com.google.android.gms:play-services-auth:21.3.0")

    // --- Dipendenze per la Google Calendar API ---
    // Google Calendar API (utilizza una versione disponibile, ad esempio la seguente)
    implementation("com.google.apis:google-api-services-calendar:v3-rev20241101-2.0.0") {
        exclude (group = "org.apache.httpcomponents")
    }

    // HTTP Client e Json Factory
    implementation("com.google.http-client:google-http-client-android:1.45.3")
    implementation("com.google.http-client:google-http-client-gson:1.45.3")
    implementation("com.google.api-client:google-api-client-android:2.7.1")
    implementation("com.google.api-client:google-api-client-gson:2.7.1")


    configurations.all {
        resolutionStrategy {
            force("io.grpc:grpc-okhttp:1.51.0")
            force("io.grpc:grpc-core:1.51.0")
            force("io.grpc:grpc-api:1.51.0")
            force("io.grpc:grpc-context:1.51.0")
        }
    }

    implementation("com.google.android.gms:play-services-maps:19.0.0")


}
