package com.example.covidapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MapActivity : AppCompatActivity() {

    private lateinit var areaText : TextView
    private lateinit var casesText : TextView
    private lateinit var vaccinesText : TextView
    private val conn = ConnectionChecker()

    private val areaList : List<Area> = listOf(
            Area("445131","518327","Ahvenanmaa",R.id.ahvenanmaa,29789), Area("445197","518349","Varsinais-Suomi", R.id.varsinaissuomi, 481478),
            Area("445170","518366","Satakunta", R.id.satakunta, 218624), Area("445206","518340","Kanta-Häme", R.id.kantahame, 171364),
            Area("445282","518298","Pirkanmaa", R.id.pirkanmaa, 535044), Area("445014","518300","Päijät-Häme", R.id.paijathame, 211215),
            Area("445178","518335","Kymenlaakso", R.id.kymenlaakso, 166623), Area("445043","518294","Etelä-Karjala", R.id.etelakarjala, 128756),
            Area("445155","518306","Etelä-Savo", R.id.etelasavo, 100226), Area("445175","518377","Itä-Savo", R.id.itasavo, 41060),
            Area("445293","518343", "Pohjois-Karjala", R.id.pohjoiskarjala, 165569), Area("445223","518351","Pohjois-Savo", R.id.pohjoissavo, 245602),
            Area("445285","518295","Keski-Suomi", R.id.keskisuomi, 252676), Area("445225","518309","Etelä-Pohjanmaa", R.id.etelapohjanmaa, 194316),
            Area("445079","518323","Vaasa", R.id.vaasa, 169684), Area("445230","518369","Keski-Pohjanmaa", R.id.keskipohjanmaa, 77689),
            Area("444996","518354","Pohjois-Pohjanmaa", R.id.pohjoispohjanmaa, 409418), Area("445101","518303","Kainuu", R.id.kainuu, 73061),
            Area("445190","518353","Länsi-Pohja", R.id.lansipohja, 61172), Area("445224","518322","Lappi", R.id.lappi, 117350),
            Area("445193","518320","Helsinki ja Uusimaa", R.id.uusimaa, 1667203))

    private val viewList : List<Int> = listOf(
            R.id.ahvenanmaa, R.id.varsinaissuomi, R.id.satakunta, R.id.kantahame, R.id.pirkanmaa, R.id.paijathame,
            R.id.kymenlaakso, R.id.etelakarjala, R.id.etelasavo, R.id.itasavo, R.id.pohjoiskarjala, R.id.pohjoissavo,
            R.id.keskisuomi, R.id.etelapohjanmaa, R.id.vaasa, R.id.keskipohjanmaa, R.id.pohjoispohjanmaa, R.id.kainuu,
            R.id.lansipohja, R.id.lappi, R.id.uusimaa)

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        areaText = findViewById(R.id.areaText)
        casesText = findViewById(R.id.casesText)
        vaccinesText = findViewById(R.id.vaccinesText)
        if (conn.isOnline(this)) {
            areaText.text = "Ladataan tietoja"
            fetchDataForAllAreas()
        } else {
            areaText.text = "Ei internet-yhteyttä"
        }
    }

    private fun fetchDataForAllAreas() {
        thread() {
            for (i in areaList.indices) {
                val json = getUrl(getCasesUrlWithSid(areaList[i].sidCases))
                val dataSet = JSONObject(json).getJSONObject("dataset")
                areaList[i].cases = dataSet.getJSONObject("value").getString("105")
            }

            for (i in areaList.indices) {
                val json = getUrl(getVaccineUrlWithSid(areaList[i].sidVaccine))
                val dataSet = JSONObject(json).getJSONObject("dataset")
                areaList[i].vaccines = dataSet.getJSONObject("value").getString("0")
            }
            for (i in viewList.indices) {
                val view = findViewById<View>(viewList[i])
                view.setOnClickListener(MyListener())
            }
            runOnUiThread() {
                areaText.text = "Paina aluetta"
            }
        }
    }

    private fun getUrl(url: String) : String {
        val myUrl = URL(url)
        val connection = myUrl.openConnection() as HttpURLConnection
        val inputStream = connection.inputStream
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun getCasesUrlWithSid(sid: String) : String {
        return "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=hcdmunicipality2020-$sid.&column=dateweek20200101-509030&filter=measure-444833"
    }

    private fun getVaccineUrlWithSid(sid: String) : String {
        return "https://sampo.thl.fi/pivot/prod/fi/vaccreg/cov19cov/fact_cov19cov.json?row=hcdmunicipality2020-$sid.&column=dateweek20200101-509030&filter=measure-533170"
    }

    inner class MyListener : View.OnClickListener {

        override fun onClick(v: View) {
            for (i in areaList.indices) {
                if (v.id == areaList[i].imageId) {
                    areaText.text = areaList[i].name
                    casesText.text = "Tapauksia: " + areaList[i].cases
                    vaccinesText.text = "Rokotettu: " + areaList[i].getVaccinatedPercentage() + "%"
                }
            }
        }
    }
}

