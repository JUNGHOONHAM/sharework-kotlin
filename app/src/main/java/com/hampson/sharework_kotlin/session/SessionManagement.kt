package com.hampson.sharework_kotlin.session

import android.content.Context
import android.content.SharedPreferences
import com.hampson.sharework_kotlin.data.vo.User

class SessionManagement (context: Context) {
    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    private val SHARED_PREF_NAME = "session"

    private val IS_LOGIN = "IsLoggedIn"
    private val KEY_ID = "id"
    private val KEY_EMAAIL = "email"
    private val KEY_PHONE_NUMBER = "phone"
    private val KEY_APP_TYPE = "app_type"

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveSession(user: User) {
        // 유저가 로그인 할 때 세션 저장
        editor.putBoolean(IS_LOGIN, true)
        editor.putInt(KEY_ID, user.id!!)
        editor.putString(KEY_PHONE_NUMBER, user.phone)
        editor.putString(KEY_EMAAIL, user.email)
        editor.putString(KEY_APP_TYPE, user.app_type)
        editor.commit()
    }

    fun getSessionID(): Int {
        return sharedPreferences.getInt(KEY_ID, -1)
    }

    fun removeSession() {
        editor.clear()
        editor.commit()
    }
}