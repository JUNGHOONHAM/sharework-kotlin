package com.hampson.sharework_kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.hampson.sharework_kotlin.databinding.ActivitySplashBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number.AuthenticationPhoneNumberActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySplashBinding

    private lateinit var sessionManagement: SessionManagement
    private var userID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        sessionManagement = SessionManagement(this)
        userID = sessionManagement.getSessionID()

        Handler().postDelayed({
            checkSession()
            finish()
        }, 2000)
    }

    private fun checkSession() {
        if (userID != -1) {
            moveToMainActivity()
        } else {
            moveToAuthenticationPhoneNumberActivity()
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun moveToAuthenticationPhoneNumberActivity() {
        val intent = Intent(this, AuthenticationPhoneNumberActivity::class.java)
        startActivity(intent)
    }
}