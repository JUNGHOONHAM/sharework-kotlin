package com.hampson.sharework_kotlin.ui.management_user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hampson.sharework_kotlin.databinding.ActivityArthenticationPhoneNumberBinding

class AuthenticationPhoneNumber : AppCompatActivity() {

    private lateinit var mBinding: ActivityArthenticationPhoneNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityArthenticationPhoneNumberBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}