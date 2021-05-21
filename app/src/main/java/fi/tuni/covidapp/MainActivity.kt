package fi.tuni.covidapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    lateinit var casesView : TextView
    lateinit var deathsView : TextView
    lateinit var vaccinatedView : TextView
    private lateinit var button : Button
    private val casesUrl = "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json"
    private val deathsUrl = "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=ttr10yage-444309&column=dateweek20200101-509030.&filter=measure-492118"
    private val citiesUrl = "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.dimensions.json"
    private val vaccinatedUrl = "https://sampo.thl.fi/pivot/prod/fi/vaccreg/cov19cov/fact_cov19cov.json?row=hcdmunicipality2020-518362.&column=dateweek20200101-509030&filter=measure-533170"
    private lateinit var locationManager: LocationManager
    private var longitude = 0.0
    private var latitude = 0.0
    private lateinit var areaJsonArray : JSONArray
    private val population = 5537116
    private val conn = ConnectionChecker()
    private var connection = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById((R.id.button))
        button.isClickable = false
        casesView = findViewById(R.id.casesView)
        deathsView = findViewById(R.id.deathsView)
        vaccinatedView = findViewById(R.id.vaccinatedView)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;

        // Initiates data fetching or offline mode based on internet status.
        if (conn.isOnline(this)) {
            fetchData()
        } else {
            button.text = "Ei yhteyttä. Päivitä painamalla tästä."
            button.isClickable = true
            connection = false;
        }
    }

    private fun fetchData() {
        // Fetches all registered covid cases in Finland.
        thread() {
            val casesJson = getUrl(casesUrl)
            if (isJSONValid(casesJson)) {
                val dataSet = JSONObject(casesJson).getJSONObject("dataset")
                val allCases = dataSet.getJSONObject("value").getString("2331")
                runOnUiThread() {
                    casesView.text = allCases
                }
            } else {
                vaccinatedView.text = "Yhteys epäonnistui"
                deathsView.text = ""
                button.text = "Päivitä sovellus"
                connection = false
            }
        }
        // Fetches all covid-related deaths in Finland.
        thread() {
            val deathsJson = getUrl(deathsUrl)
            if (isJSONValid(deathsJson)) {
                val dataSet = JSONObject(deathsJson).getJSONObject("dataset")
                val allDeaths = dataSet.getJSONObject("value").getString("9")
                runOnUiThread() {
                    deathsView.text = "Koronakuolemia: $allDeaths"
                }
            }
        }
        // Fetches number of people who have received their first dose of covid vaccine in Finland.
        thread() {
            val vaccinesJson = getUrl(vaccinatedUrl)
            if (isJSONValid(vaccinesJson)) {
                val dataSet = JSONObject(vaccinesJson).getJSONObject("dataset")
                val allVaccinated = dataSet.getJSONObject("value").getString("0")
                val p: Double = allVaccinated.toDouble() / population * 100
                runOnUiThread() {
                    vaccinatedView.text = "Rokotettu: " + p.toInt() + "%"
                }
            }
        }
        // Fetches json that holds the unique ids for each Finnish city, used for fetching their local covid cases.
        thread() {
            var loop = true
            while(loop) {
                var areasString = getUrl(citiesUrl)
                if (areasString.isNotEmpty()) {
                    val index = areasString.indexOf('[')
                    areasString = areasString.substring(index + 1)
                    val areasJson = areasString.dropLast(3)
                    if (isJSONValid(areasJson)) {
                        areaJsonArray = JSONObject(areasJson).getJSONArray("children").getJSONObject(0).getJSONArray("children")
                        button.isClickable = true
                        loop = false
                    }
                }
            }
        }
    }

    private fun getUrl(url: String) : String {
        try {
            val myUrl = URL(url)
            val connection = myUrl.openConnection() as HttpURLConnection
            val inputStream = connection.inputStream
            return inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            return ""
        }
    }

    private fun isJSONValid(s: String): Boolean {
        try {
            JSONObject(s)
        } catch (e: JSONException) {
            try {
                JSONArray(s)
            } catch (e: JSONException) {
                return false
            }
        }
        return true
    }

    // Requests location permissions and initiates fetch for covid cases in the current city by location.
    fun onLocationButtonPressed(v: View) {
        if (conn.isOnline(this) && connection) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            }
            button.text = "Haetaan..."
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener);
        } else if (conn.isOnline(this) && !connection) {
            fetchData()
            connection = true
            button.text = "Hae oman kuntasi tartunnat"
        }
    }

    private val locationListener : LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            // Gets the name of the current city using device's location coordinates.
            thread() {
                longitude = location.longitude
                latitude = location.latitude
                val geoCoder = Geocoder(this@MainActivity, Locale.getDefault())
                var addresses : List<Address> = listOf()
                try {
                    addresses = geoCoder.getFromLocation(latitude, longitude, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                    button.text = "Haku epäonnistui. Yritä uudestaan."
                }
                val address = addresses[0].getAddressLine(0)
                val postalCode : String = addresses[0].postalCode
                val index = address.indexOf(postalCode)
                val cityAndCountry: String = address.substring(index + 6)
                val city = cityAndCountry.dropLast(7)

                locationManager.removeUpdates(this)

                if (areaJsonArray.length() > 0) {
                    for (i in 0 until areaJsonArray.length()) {
                        // Gets the array of cities inside a province
                        val area = areaJsonArray[i] as JSONObject
                        val citiesJsonArray = area.getJSONArray("children")

                        for (j in 0 until citiesJsonArray.length()) {
                            // Compares the city names to the one received earlier with the device's location and picks up the city's id.
                            val cityObj = citiesJsonArray[j] as JSONObject
                            if (cityObj.getString("label") == city) {
                                val sid = cityObj.getString("sid")
                                getCityData(city, sid)
                            }
                        }
                    }
                }
            }
        }
    }

    // Fetches covid cases for the city that was determined by device's location.
    fun getCityData(city: String, sid: String) {
        thread() {
            val casesJson = getUrl("https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json?row=hcdmunicipality2020-$sid.&column=dateweek20200101-509030&filter=measure-444833")
            val dataSet = JSONObject(casesJson).getJSONObject("dataset")
            val cases = dataSet.getJSONObject("value").getString("105")

            runOnUiThread() {
                if (cases != "..") {
                    button.text = "$city: $cases tartuntaa"
                } else {
                    button.text = "$city: Ei rekisteröityjä tartuntoja"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                        button.text = "Haetaan..."
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener);
                    }
                }
                return
            }
        }
    }

    fun changeActivity(v: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}