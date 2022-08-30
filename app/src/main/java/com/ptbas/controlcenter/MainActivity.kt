package com.ptbas.controlcenter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ptbas.controlcenter.auth.LoginActivity
import com.ptbas.controlcenter.auth.RegisterActivity

class MainActivity : AppCompatActivity() {

    var authProfile: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Thread.sleep(2000)
        val splashScreen =  installSplashScreen()
        setContentView(R.layout.activity_main)

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = authProfile!!.currentUser

        if (firebaseUser != null) {
            val i = Intent(this, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

    }

    fun login (view: View) {
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    fun register (view: View) {
        val i = Intent(this, RegisterActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

}