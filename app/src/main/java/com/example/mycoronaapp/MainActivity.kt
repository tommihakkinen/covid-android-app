package com.example.mycoronaapp

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    lateinit var bitmap : Bitmap
    lateinit var map : ImageView
    lateinit var mapBase : ImageView
    lateinit var info : TextView
    private val url = "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        info = findViewById(R.id.allCases)
//        map = findViewById(R.id.map)
////        mapBase = findViewById(R.id.map_base)
////        bitmap = getBitmap(map.drawable)
//        createMapListener()
    }

    override fun onResume() {
        super.onResume()

        thread() {
            val json = getUrl(url)
            val dataset = JSONObject(json).getJSONObject("dataset")
            val allCases = dataset.getJSONObject("value").getString("2331")
            runOnUiThread() {
                info.text = allCases
            }
        }
    }

    private fun getUrl(url: String) : String? {
        val myUrl = URL(url)
        val connection = myUrl.openConnection() as HttpURLConnection
        var result = ""
        val inputstream = connection.inputStream

        inputstream.use {
            var c = it.read()
            while (c != -1) {
                result += c.toChar()
                c = it.read()
            }
        }
        return result
    }

//    private fun createMapListener() {
//
//        map.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                val imageView = v as ImageView
//                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//
//                var x = event.x.toInt()
//                var y = event.y.toInt()
//                var pixel = bitmap.getPixel(x, y)
//
//                var redValue = Color.red(pixel)
//                var blueValue = Color.blue(pixel)
//                var greenValue = Color.green(pixel)
//
//                Log.d("Main", "$redValue, $greenValue, $blueValue")
//
//                return v?.onTouchEvent(event) ?: true
//            }
//        })
//        map.setOnTouchListener {
//            fun onTouch(View v, MotionEvent event) {
//                var x = (Int)event.getX()
//                var y = (Int)event.getY()
//                var pixel = bitmap.getPixel(x, y)
//
//                var redValue = Color.red(pixel)
//                var blueValue = Color.blue(pixel)
//                var greenValue = Color.green(pixel)
//            }
//
}