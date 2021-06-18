package com.hampson.sharework_kotlin.ui.management_user

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hampson.sharework_kotlin.R
import com.hampson.sharework_kotlin.databinding.ActivityArthenticationPhoneNumberBinding

class AuthenticationPhoneNumber : AppCompatActivity() {

    private lateinit var mBinding: ActivityArthenticationPhoneNumberBinding

    private val maxCount = 300

    private val context = this

    // 휴대폰 번호 정규식
    private val reg = Regex("01[016789][0-9]{3,4}[0-9]{4}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityArthenticationPhoneNumberBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

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

            setBtnCertificationDisAble()
            startCountDown()
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
}