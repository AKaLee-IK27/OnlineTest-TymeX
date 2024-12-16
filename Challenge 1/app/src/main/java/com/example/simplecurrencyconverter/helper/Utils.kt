@file:Suppress("DEPRECATION")

package com.example.simplecurrencyconverter.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.os.Build

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