package com.example.lockScreen.battery.charging.animation.utils

import android.content.Context
import android.preference.PreferenceManager

object Preferences {

    fun getUri(context: Context?, str: String?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(str, "null")
    }

    fun setUri(context: Context?, value: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString("uri", value).apply()
    }

    fun setAnimation(context: Context?, value: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putInt("animation", value).apply()
    }


    fun getAnimation(context: Context?, value: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("animation", value)
    }


    fun setAnimationId(context: Context?, value: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putInt("animationId", value).apply()
    }


    fun getAnimationId(context: Context?, value: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("animationId", value)
    }


    fun setClosing(context: Context?, value: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putInt("closing", value).apply()
    }


    fun getClosing(context: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("closing", 0)
    }


    fun setLock(context: Context?, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("isLock", value).apply()
    }


    fun getLock(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isLock", false)
    }


    fun setFirstLaunch(context: Context?, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("setFirstLaunch", value).apply()
    }


    fun getFirstTimeLaunch(context: Context?, value: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("setFirstLaunch", value)
    }


    fun setPerVal(context: Context?, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("perVal", value).apply()
    }


    fun getPerVal(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("perVal", true)
    }


    fun setIsShowing(context: Context?, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("isShowing", value).apply()
    }


    fun getIsShowing(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isShowing", true)
    }

}