package com.hampson.sharework_kotlin.ui.management_user.sign_up

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hampson.sharework_kotlin.data.api.DBClient
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.User
import com.hampson.sharework_kotlin.databinding.ActivitySignUpBinding
import org.jetbrains.anko.toast

class SignUpActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySignUpBinding

    private lateinit var viewModel: SignUpViewModel
    private lateinit var apiService: DBInterface

    private lateinit var phoneNumber: String

    private lateinit var userName: String
    private lateinit var birth: String
    private lateinit var gender: String
    private lateinit var email: String

    // 주민등록번호 앞자리 정규식
    private val reg1 = Regex("^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|[3][01])")

    // 주민등록번호 뒷자리 정규식
    private val reg2 = Regex("[0-4]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        apiService = DBClient.getClient(this)

        mBinding.toolbar.textViewToolbarTitle.text = "회원가입"

        if (intent.hasExtra("phoneNumber")) {
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
        }

        viewModel = getViewModel()

        viewModel.getAction().observe(this, {
            val intent = Intent(this, it as Class<*>)
            startActivity(intent)
            finish()
        })

        viewModel.getToast().observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        mBinding.buttonSignUp.setOnClickListener {
            userName = mBinding.editTextName.text.toString()
            birth = mBinding.editTextBirth.text.toString()
            gender = mBinding.editTextGender.text.toString()
            email = mBinding.editTextEmail.text.toString()

            if (!userValidationCheck()) {
                return@setOnClickListener
            }

            var user = User(null, phoneNumber, "0", email, null, null, null,
                genderCheck(), userName, null, birth, null)

            viewModel.createUser(user)
        }
    }

    private fun getViewModel(): SignUpViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel(apiService, application) as T
            }
        }).get(SignUpViewModel::class.java)
    }

    private fun userValidationCheck(): Boolean {
        if (userName == "") {
            toast("이름을 입력해 주세요.")
            return false
        }

        if (birth == "" || gender == "") {
            toast("주민등록번호를 입력해 주세요.")
            return false
        }

        if (email == "") {
            toast("이메일을 입력해 주세요.")
            return false
        }

        if (userName == "") {
            toast("이름을 입력해 주세요.")
            return false
        }

        if (!birthValidationCheck()) {
            toast("잘못된 주민등록번호 입니다.")
            return false
        }

        if (!emailValidationCheck()) {
            toast("잘못된 이메일 입니다.")
            return false
        }

        return true
    }

    private fun birthValidationCheck(): Boolean {
        if (!birth.matches(reg1)) {
            return false
        } else {
            return gender.matches(reg2)
        }
    }

    private fun emailValidationCheck(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun genderCheck(): String {
        return if (gender == "1" || gender == "3") {
            "male"
        } else {
            "female"
        }
    }
}