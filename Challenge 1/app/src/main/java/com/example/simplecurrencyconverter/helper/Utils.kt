@file:Suppress("DEPRECATION")

package com.example.simplecurrencyconverter.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.inputmethod.InputMethodManager

fun String.toFlagEmoji(): String {
    if (this.length != 2) {
        return this
    }

    val countryCodeCaps =
        this.toUpperCase()
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

object Utils {

    fun hideKeyboard(activity: Activity) {
        val inputManager: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        var currentView: View? = activity.currentFocus

        if (currentView == null) {
            currentView = View(activity)
        }
        inputManager.hideSoftInputFromWindow(currentView.windowToken, 0)
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capability =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capability != null) {
            when {
                capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    fun makeStatusBarTransparent(activity: Activity) {
        val decor = activity.window.decorView
        decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val w = activity.window
        w.statusBarColor = Color.TRANSPARENT
    }
}