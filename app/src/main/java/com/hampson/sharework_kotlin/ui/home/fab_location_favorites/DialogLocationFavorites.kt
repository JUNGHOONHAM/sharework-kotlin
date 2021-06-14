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

        mBinding = DialogLocationFavoritesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initDisplaySize()

        mBinding.buttonAdd.setOnClickListener {
            dismiss()
        }
    }

    private fun initDisplaySize() {
        val param = this.window?.attributes
        param!!.width = WindowManager.LayoutParams.MATCH_PARENT
        param!!.height = WindowManager.LayoutParams.WRAP_CONTENT

        this.window?.attributes = param

        // 배경 투명
        // window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}