package com.eaglenews.app

import android.app.Application
import com.google.firebase.FirebaseApp

class EagleNewsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}