package com.rajnish.photoprints.nowInkotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object InternetUtils {


        fun isInternetConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return run {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val capabilities =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
        }




}
