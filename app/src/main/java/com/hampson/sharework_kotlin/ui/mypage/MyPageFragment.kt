package com.hampson.sharework_kotlin.ui.mypage

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.FragmentMypageBinding
import com.hampson.sharework_kotlin.session.SessionManagement
import com.hampson.sharework_kotlin.ui.home.bottom_sheet_job_list.job_info.JobInfoActivity
import com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number.AuthenticationPhoneNumberActivity
import com.hampson.sharework_kotlin.ui.mypage.payment_history.PaymentHistoryWorkerActivity
import com.hampson.sharework_kotlin.ui.mypage.profile_update.UserInfoUpdateActivity
import com.hampson.sharework_kotlin.ui.user_profile.ProfileActivity
import ir.androidexception.andexalertdialog.AndExAlertDialog
import ir.androidexception.andexalertdialog.AndExAlertDialogListener
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.support.v4.toast
import java.io.File


class MyPageFragment : Fragment() {

    private var mBinding : FragmentMypageBinding? = null

    private lateinit var viewModel: MyPageViewModel
    private lateinit var myPageRepository: MyPageRepository
    private lateinit var apiService: DBInterface

    private lateinit var sessionManagement: SessionManagement
    private var userId: Int = -1

    private val PICK_FROM_ALBUM = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.toolbar.textViewToolbarTitle.text = "마이페이지"

        mBinding = binding

        sessionManagement = SessionManagement(requireActivity())
        userId = sessionManagement.getSessionID()

        apiService = DBClient.getClient(requireActivity())
        myPageRepository = MyPageRepository(apiService)
        viewModel = getViewModel()
        
        viewModel.userInfoLiveData.observe(requireActivity(), {
            bindUI(it)
        })
        
        viewModel.updateProfileLiveData().observe(requireActivity(), {
            if (it) {
                toast("이미지 변경 완료")
            } else {
                toast("이미지 변경 실패")
            }
        })

        viewModel.updateAppTypeLiveData().observe(requireActivity(), {
            var appType = ""
            if (appType == "2") {
                appType = requireActivity().getString(R.string.giver)
            } else {
                appType = requireActivity().getString(R.string.worker)
            }


        })

        viewModel.networkState().observe(requireActivity(), {
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.textViewError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

        binding.imageViewProfile.setOnClickListener {
            if (isStoragePermissionGranted()) {
                //ImagePicker.with(requireActivity())
                //    .crop()
                //    .cropOval()
                //    .createIntentFromDialog { launcher.launch(it) }

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                startActivityForResult(intent, PICK_FROM_ALBUM)
            }

        }

        binding.layoutMyProfile.setOnClickListener {
            Intent(context, ProfileActivity::class.java).apply {
                putExtra("userId", userId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { context?.startActivity(this) }
        }

        binding.textViewProfileUpdate.setOnClickListener {
            val intent = Intent(context, UserInfoUpdateActivity::class.java)
            startActivity(intent)
        }

        binding.textViewAppTypeChange.setOnClickListener {
            updateAppType()
            activity?.finish()
        }

        binding.textViewLogout.setOnClickListener {
            AndExAlertDialog.Builder(context)
                .setMessage("정말로 로그아웃 하시겠습니까?")
                .setPositiveBtnText("로그아웃")
                .setNegativeBtnText("닫기")
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked {
                    logout()
                }
                .OnNegativeClicked {

                }.build()
        }

        binding.textViewPayment.setOnClickListener {
            val intent = Intent(context, PaymentHistoryWorkerActivity::class.java)
            startActivity(intent)
        }

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun getViewModel(): MyPageViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAS T")
                return MyPageViewModel(myPageRepository, userId) as T
            }
        }).get(MyPageViewModel::class.java)
    }

    private fun bindUI(user: User) {
        mBinding?.textViewName?.text = user.name

        Glide.with(this)
            .load(user.profile_img)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(mBinding?.imageViewProfile!!)

        if (sessionManagement.getAppType() == requireActivity().getString(R.string.worker)) {
            mBinding?.textViewAppTypeChange?.text = "일 주는 사람으로 변경"
        } else {
            mBinding?.textViewAppTypeChange?.text = "일 하는 사람으로 변경"
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data!!
        }
    }

    private fun getPathFromUri(uri: Uri?): String? {
        val projection: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            activity?.contentResolver?.query(uri!!, projection, null, null, null)
        cursor?.moveToNext()
        val index = cursor?.getColumnIndex(MediaStore.MediaColumns.DATA)
        val path = cursor?.getString(index!!)

        cursor?.close()

        return path
    }

    private fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && requireActivity().checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE), 1)
                return false
            }
        } else {
            return true
        }
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            ImagePicker.with(requireActivity())
                .crop()
                .cropOval()
                .createIntentFromDialog { launcher.launch(it) }
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            val file = File(getPathFromUri(data?.data))

            val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
            val imageFile = MultipartBody.Part.createFormData("img_file", file.name, requestFile)
            val requestBodyUserId = RequestBody.create(MediaType.parse("text/plain"), userId.toString())

            viewModel.updateProfileImage(imageFile, requestBodyUserId)

            Glide.with(this)
                .load(data?.data)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(mBinding?.imageViewProfile!!)

            //toast("이미지 업로드 성공")
        }
    }

    private fun logout() {
        requireActivity().finishAffinity() // 현재 activity의 모든 fragment를 제거
        sessionManagement.removeSession()

        val intent = Intent(requireActivity(), AuthenticationPhoneNumberActivity::class.java)
        startActivity(intent)
    }

    private fun updateAppType() {
        val userId = sessionManagement.getSessionID()
        var appType = sessionManagement.getAppType()

        if (appType == requireActivity().getString(R.string.worker)) {
            appType = requireActivity().getString(R.string.giver)
        } else {
            appType = requireActivity().getString(R.string.worker)
        }

        val user = User(userId, null, appType, null, null, null, null,
            null, null, null, null, null)

        val sessionManagement = SessionManagement(requireContext())
        sessionManagement.removeSession()
        sessionManagement.saveSession(user)
    }
}