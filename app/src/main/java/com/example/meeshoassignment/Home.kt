package com.example.meeshoassignment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.meeshoassignment.AppConstants.Constants
import com.example.meeshoassignment.databinding.ActivityMainBinding
import com.example.meeshoassignment.model.SessionDetailsModel
import org.json.JSONArray
import org.json.JSONObject


class Home : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    val SCANNER_RESULT = 202
    var isSessionActive = false
    private var TIME_INFO = "time_info"
    lateinit var brReceiver: BroadcastReceiver
    lateinit var  sessionModel:SessionDetailsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener(this)


        brReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null && !isFinishing) {
                    if (intent.action == TIME_INFO) {
                        var value = intent.getStringExtra(Constants.VALUE).toString()
                        sessionModel= (intent.getSerializableExtra(Constants.EXTRAS_SESSION_MODEL) as? SessionDetailsModel)!!
                        binding.tvTime.setText(value)
                        setTextInView(sessionModel)
                        updateButtons(true)

                    }

                }
            }
        }

    }

    private fun setTextInView(sessionModel: SessionDetailsModel) {

        binding.tvid.setText(getString(R.string.location_id)+" "+sessionModel.location_id)
        binding.tvlocationDetail.setText(getString(R.string.location_detail)+" "+sessionModel.location_details)
        binding.tvprice.setText(getString(R.string.price)+" "+sessionModel.price_per_min+ "/"+getString(R.string.min))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == SCANNER_RESULT) {
            var result = data!!.getStringExtra(Constants.scan_result)
            try {
                var jsonobject = JSONObject(JSONArray("[" + result.toString() + "]").getString(0))
                 sessionModel = SessionDetailsModel(
                    jsonobject.optString("location_id", ""),
                    jsonobject.optString("location_details", ""),
                    jsonobject.optString("price_per_min").toFloat()
                )


                //start service for timer
                if (!isSessionActive) {
                    isSessionActive = true
                    updateButtons(false)
                    setTextInView(sessionModel)
                    InitiateService(sessionModel)


                } else {
                    isSessionActive = false
                    stopService(Intent(this, CountUpService::class.java))
                    //send data to server
                    updateButtons(false)


                }

            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.invalid_qr), Toast.LENGTH_LONG).show()
                e.printStackTrace()

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun InitiateService(sessionModel: SessionDetailsModel) {

        val intent = Intent(this, CountUpService::class.java)
            .putExtra(Constants.EXTRAS_SESSION_MODEL,sessionModel)
        startForegroundService(intent)
    }

    override fun onClick(id: View?) {
        when (id) {
            binding.btnStart -> {
                var intent = Intent(this, SimpleScannerActivity::class.java)
                startActivityForResult(intent, SCANNER_RESULT)
            }
            binding.btnEnd -> {
                var intent = Intent(this, SimpleScannerActivity::class.java)
                startActivityForResult(intent, SCANNER_RESULT)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        var intent = IntentFilter(TIME_INFO)
        registerReceiver(brReceiver, intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(brReceiver)
    }

    fun updateButtons(isSessionActive: Boolean) {
        binding.btnStart.isEnabled = !isSessionActive
        binding.btnEnd.isEnabled = isSessionActive
        if (isSessionActive) {
            binding.btnEnd.setOnClickListener(this)
        } else
            binding.btnStart.setOnClickListener(this)

    }
}