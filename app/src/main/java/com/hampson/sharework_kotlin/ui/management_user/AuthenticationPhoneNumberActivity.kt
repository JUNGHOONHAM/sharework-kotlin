package com.hampson.sharework_kotlin.ui.management_user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.AuthenticationPhoneNumberNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.databinding.ActivityArthenticationPhoneNumberBinding
import com.hampson.sharework_kotlin.ui.MainActivity
import com.hampson.sharework_kotlin.ui.single_job.JobRepository
import com.hampson.sharework_kotlin.ui.single_job.SingleJobViewModel
import io.reactivex.disposables.CompositeDisposable
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class AuthenticationPhoneNumberActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityArthenticationPhoneNumberBinding

    private lateinit var viewModel: AuthenticationPhoneNumberViewModel
    private lateinit var authenticationPhoneNumberRepository: AuthenticationPhoneNumberRepository
    private val apiService : DBInterface = DBClient.getClient()

    private val maxCount = 300

    private lateinit var phoneNumber: String
    private lateinit var token: String

    private val context = this

    // 휴대폰 번호 정규식
    private val reg = Regex("01[016789][0-9]{3,4}[0-9]{4}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityArthenticationPhoneNumberBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        authenticationPhoneNumberRepository = AuthenticationPhoneNumberRepository(apiService)

        viewModel = getViewModel()

        viewModel.getSmsAuth().observe(this, {
            token = it.token
            Log.d("testtt", token)
        })

        viewModel.getAction().observe(this, {
            val intent = Intent(this, it as Class<*>)
            startActivity(intent)
            finish()
        })

        viewModel.getToast().observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        mBinding.editTextCertification.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBtnLoginCheck()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        mBinding.buttonCertification.setOnClickListener {
            if (!validationCheckPhone()) {
                Toast.makeText(this, "올바른 휴대폰 번호가 아닙니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            phoneNumber = mBinding.editTextPhoneNumber.text.toString()

            setBtnCertificationDisAble()
            startCountDown()

            viewModel.sendPhoneNumber(mBinding.editTextPhoneNumber.text.toString())
        }

        mBinding.buttonLogin.setOnClickListener {
            viewModel.sendVerifiedNumber(phoneNumber, token, mBinding.editTextCertification.text.toString())
        }
    }

    // 로그인버튼 check
    @SuppressLint("ResourceAsColor")
    private fun setBtnLoginCheck() {
        if (mBinding.editTextCertification.text.toString() == "") {
            mBinding.buttonLogin.setTextColor(R.color.gray)
            mBinding.buttonLogin.setBackgroundResource(R.drawable.background_fill_gray)
            mBinding.buttonLogin.isEnabled = false
        } else {
            mBinding.buttonLogin.setTextColor(R.color.white)
            mBinding.buttonLogin.setBackgroundResource(R.drawable.background_fill_mint)
            mBinding.buttonLogin.isEnabled = true
        }
    }

    // 인증버튼 활성화
    private fun setBtnCertificationEnAble() {
        mBinding.buttonCertification.text = "인증"
        mBinding.buttonCertification.isEnabled = true
        mBinding.editTextCertification.clearFocus()
    }

    // 인증버튼 비활성화
    private fun setBtnCertificationDisAble() {
        mBinding.layoutInputCertification.visibility = View.VISIBLE

        mBinding.buttonCertification.text = "전송 완료"
        mBinding.buttonCertification.isEnabled = false
        mBinding.editTextCertification.clearFocus()

        mBinding.editTextPhoneNumber.requestFocus()
    }

    // 휴대폰 번호 유효성 체크
    private fun validationCheckPhone() : Boolean {
        return mBinding.editTextPhoneNumber.text.toString().matches(reg)
    }

    private fun startCountDown() {
        var CDT: CountDownTimer = object : CountDownTimer(10 * 30000, 1000) {
            var count = maxCount

            override fun onTick(millisUntilFinished: Long) {
                mBinding.textViewCount.text = count.toString() + "초"
                count--
            }

            override fun onFinish() {
                mBinding.editTextCertification.setText("")
                mBinding.layoutInputCertification.visibility = View.GONE
                Toast.makeText(context, "시간 초과", Toast.LENGTH_LONG).show()

                setBtnLoginCheck()

                setBtnCertificationEnAble()
            }

        }

        CDT.start()
    }

    private fun getViewModel(): AuthenticationPhoneNumberViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return AuthenticationPhoneNumberViewModel(authenticationPhoneNumberRepository, apiService, application) as T
            }
        }).get(AuthenticationPhoneNumberViewModel::class.java)
    }
}