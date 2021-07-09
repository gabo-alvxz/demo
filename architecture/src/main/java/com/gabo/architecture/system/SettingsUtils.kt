package com.gabo.architecture.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SettingsUtils @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
            Constants.SETTINGS_STORE_NAME,
            Context.MODE_PRIVATE
        )

    fun getString(key: SettingsKey): String? {
        return preferences.getString(key.toString(), null)
    }

    fun setString(key: SettingsKey, value: String?) {
        preferences.edit().putString(key.toString(), value).apply()
    }

    fun getSelectedSite(): String? {
        return getString(SettingsKey.SITE)
    }
}

enum class SettingsKey {
    SITE
}