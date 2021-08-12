package com.hampson.sharework_kotlin.ui.home_giver

import android.Manifest.permission
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.FragmentHomeGiverBinding
import com.hampson.sharework_kotlin.databinding.FragmentMypageBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number.AuthenticationPhoneNumberActivity
import com.hampson.sharework_kotlin.ui.mypage.payment_history.PaymentHistoryWorkerActivity
import com.hampson.sharework_kotlin.ui.mypage.profile_update.UserInfoUpdateActivity
import com.hampson.sharework_kotlin.ui.user_profile.ProfileActivity
import ir.androidexception.andexalertdialog.AndExAlertDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.support.v4.toast
import java.io.File


class HomeGiverFragment : Fragment() {

    private var mBinding : FragmentHomeGiverBinding? = null

    private lateinit var sessionManagement: SessionManagement
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeGiverBinding.inflate(inflater, container, false)

        //binding.toolbar.textViewToolbarTitle.text = "마이페이지"

        mBinding = binding

        sessionManagement = SessionManagement(requireActivity())
        userId = sessionManagement.getSessionID()

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}