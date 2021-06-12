package com.hampson.sharework_kotlin.ui.home.fab_location_favorites

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import com.hampson.sharework_kotlin.databinding.DialogLocationFavoritesBinding

class DialogLocationFavorites(context: Context): Dialog(context) {

    private lateinit var  mBinding : DialogLocationFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 꼭 DialogFragment 클래스에서 선언하지 않아도 된다.
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        size.x // 디바이스 가로 길이
        size.y // 디바이스 세로 길이

        val params: ViewGroup.LayoutParams? = this.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        this?.window?.attributes = params as WindowManager.LayoutParams

        mBinding = DialogLocationFavoritesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // 배경 투명
        // window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mBinding.buttonAdd.setOnClickListener {

        }
    }
}