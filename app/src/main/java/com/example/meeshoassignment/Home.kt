package com.example.meeshoassignment

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meeshoassignment.databinding.ActivityMainBinding
import com.example.meeshoassignment.model.SessionDetailsModel
import org.json.JSONArray
import org.json.JSONObject


class Home : AppCompatActivity(), View.OnClickListener {
    lateinit var binding:ActivityMainBinding
    val SCANNER_RESULT = 202
    var isSessionActive=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==SCANNER_RESULT)
        {
            var result=data!!.getStringExtra("scan_result")
            try {
                 var jsonobject=JSONObject(JSONArray("["+result.toString()+"]").getString(0))
                var sessionModel= SessionDetailsModel(jsonobject.optString("location_id","")
                    ,jsonobject.optString("location_details","")
                    ,jsonobject.optString("price_per_min").toFloat())


                //start service for timer

                if(!isSessionActive)
                {
                    isSessionActive=true
                    binding.btnStart.isEnabled=false
                    binding.btnEnd.isEnabled=true
                    binding.btnEnd.setOnClickListener(this)

                }
                else{
                    isSessionActive=false
                    binding.btnStart.isEnabled=true
                    binding.btnEnd.isEnabled=false
                    binding.btnStart.setOnClickListener(this)


                }

            }
            catch (e:Exception)
            {
                Toast.makeText(this,getString(R.string.invalid_qr),Toast.LENGTH_LONG).show()
                e.printStackTrace()

            }
        }

    }

    override fun onClick(id: View?) {
        when(id)
        {
            binding.btnStart->{
                var intent=Intent(this,SimpleScannerActivity::class.java)
                startActivityForResult(intent,SCANNER_RESULT)
            }
            binding.btnEnd->{
                var intent=Intent(this,SimpleScannerActivity::class.java)
                startActivityForResult(intent,SCANNER_RESULT)
            }

        }
    }
}