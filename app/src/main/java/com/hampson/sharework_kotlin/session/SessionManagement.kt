package com.hampson.sharework_kotlin.session

import android.content.Context
import android.content.SharedPreferences
import com.hampson.sharework_kotlin.data.vo.User

class SessionManagement {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val SHARED_PREF_NAME = "session"
    private val SESSION_KEY = "session_user"


    fun SessionManagement(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveSession(user: User) {
        // 유저가 로그인 할 때 세션 저장

    }

    fun getSession(): Int {
        return -1
    }
}