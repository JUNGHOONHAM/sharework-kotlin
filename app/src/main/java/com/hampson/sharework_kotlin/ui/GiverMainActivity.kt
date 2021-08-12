package com.hampson.sharework_kotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.databinding.ActivityGiverMainBinding

class GiverMainActivity : AppCompatActivity() {

    private lateinit var  mBinding : ActivityGiverMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityGiverMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        // 네비게이션들을 담는 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바텀 네비게이션뷰 와 네비게이션을 묶어준다
        NavigationUI.setupWithNavController(mBinding.bottomNav, navController)
    }
}