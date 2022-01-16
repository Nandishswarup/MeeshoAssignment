package com.example.meeshoassignment.utils

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Looper

class Utils {
    companion object{
        var dialog: ProgressDialog? = null

        fun isNetworkConnected(a: Context?): Boolean {
            return if (a != null) {
                try {
                    val cm = a.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val ni = cm.activeNetworkInfo
                    ni != null
                } catch (e: Exception) {
                    true
                }
            } else true
        }


        fun hideProgressDialog() {
             try {
                if (dialog != null && dialog!!.isShowing()) {
                    dialog!!.dismiss()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun showProgressDialog(context: Context) {
            try {
                if (Looper.myLooper() == null) Looper.prepare()
                if (dialog != null && dialog!!.isShowing()) {
                } else {
                    dialog = ProgressDialog(context)
                    dialog!!.setMessage("Loading...")
                    dialog!!.show()
                    dialog!!.setCancelable(true)
                    dialog!!.setCanceledOnTouchOutside(false)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

}