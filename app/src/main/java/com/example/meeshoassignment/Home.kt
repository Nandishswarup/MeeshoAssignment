package com.example.meeshoassignment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.interviewassignment.networking.ApiManagger
import com.example.meeshoassignment.databinding.ActivityMainBinding
import com.example.meeshoassignment.model.SessionDetailsModel
import com.example.meeshoassignment.model.SubmitSessionModel
import com.example.meeshoassignment.networking.MyResponse
import com.example.meeshoassignment.service.CountUpService
import com.example.meeshoassignment.utils.Constants
import com.example.meeshoassignment.utils.Utils
import com.example.meeshoassignment.viewmodel.SubmitSessionViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Home : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    var isSessionActive = false
    var endtime: Long = 0
    lateinit var brReceiver: BroadcastReceiver
    lateinit var sessionModel: SessionDetailsModel
    private lateinit var submitSessionViewModel: SubmitSessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        submitSessionViewModel =
            ViewModelProvider(this@Home).get(SubmitSessionViewModel::class.java)
        binding.btnStart.setOnClickListener(this)
        brReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null && !isFinishing) {
                    if (intent.action == Constants.TIME_INFO) {
                        isSessionActive=true
                        var value = intent.getStringExtra(Constants.VALUE).toString()
                        sessionModel =
                            (intent.getSerializableExtra(Constants.EXTRAS_SESSION_MODEL) as? SessionDetailsModel)!!
                        binding.tvTime.setText(value)
                        setTextInView(sessionModel)
                        updateButtons(true)

                    }

                }
            }
        }

    }

    private fun setTextInView(sessionModel: SessionDetailsModel) {

        binding.tvid.setText(getString(R.string.location_id) + " " + sessionModel.location_id)
        binding.tvlocationDetail.setText(getString(R.string.location_detail) + " " + sessionModel.location_details)
        binding.tvprice.setText(getString(R.string.price) + " " + sessionModel.price_per_min + "/" + getString(
                R.string.min
            ))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constants.SCANNER_RESULT) {
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
                    endtime = (System.currentTimeMillis());
                    Utils.showProgressDialog(this)
                    submitSessionViewModel.SendToServer(
                        sessionModel,
                        binding.tvTime.text.toString(),
                        endtime.toString()
                    )
                    setObserver()


                }

            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.invalid_qr), Toast.LENGTH_LONG).show()
                e.printStackTrace()

            }
        }

    }

    private fun setObserver() {

        val observer = Observer<MyResponse<SubmitSessionModel>> { response ->
            when (response.status) {
                MyResponse.Status.SUCCESS -> {


                    Utils.hideProgressDialog()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.data_synced),
                        Toast.LENGTH_LONG
                    ).show()
                    isSessionActive = false
                    stopService(Intent(this, CountUpService::class.java))
                    updateButtons(false)
                    var timeMins =
                        submitSessionViewModel.calculateTimeInMin(binding.tvTime.text.toString())
                    var cost = timeMins * sessionModel.price_per_min
                    binding.tvTime.setText(getString(R.string.total_cost) + cost)
                    binding.tvid.setText(
                        getString(R.string.total_time) + timeMins + " " + getString(
                            R.string.min
                        )+"(Rounded)"
                    )
                    binding.tvlocationDetail.setText(
                        getString(R.string.end_time) + " " + SimpleDateFormat(
                            "HH:mm",
                            Locale.US
                        ).format(endtime)
                    )
                    binding.tvprice.setText("")
                }
                MyResponse.Status.LOADING -> {

                }
                MyResponse.Status.ERROR -> {
                    if (response.message == ApiManagger.LOW_INTERNET_CONNECTION)
                        Toast.makeText(
                            this,
                            resources.getString(R.string.poor_internet),
                            Toast.LENGTH_SHORT
                        )
                            .show()


                    if (response.message == ApiManagger.API_FAILURE)
                        Toast.makeText(
                            this,
                            resources.getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                }
            }

        }
        submitSessionViewModel.getSessionLiveData()!!.observe(this@Home, observer)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun InitiateService(sessionModel: SessionDetailsModel) {

        val intent = Intent(this, CountUpService::class.java)
            .putExtra(Constants.EXTRAS_SESSION_MODEL, sessionModel)
        startForegroundService(intent)
    }

    override fun onClick(id: View?) {
        when (id) {
            binding.btnStart -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    var intent = Intent(this, ScannerActivity::class.java)
                    startActivityForResult(intent, Constants.SCANNER_RESULT)
                } else
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        Constants.REQUEST_CAMERA_PERMISSION
                    )


            }
            binding.btnEnd -> {
                var intent = Intent(this, ScannerActivity::class.java)
                startActivityForResult(intent, Constants.SCANNER_RESULT)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        var intent = IntentFilter(Constants.TIME_INFO)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.btnStart.performClick()
            } else {
                Toast.makeText(
                    this@Home,
                    getString(R.string.camera_permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}