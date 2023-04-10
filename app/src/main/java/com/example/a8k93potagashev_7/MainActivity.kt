package com.example.a8k93potagashev_7

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private var locationManager: LocationManager? = null
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            showInfo(location)
        }

        override fun onProviderDisabled(provider: String) {
            showInfo()
        }

        override fun onProviderEnabled(provider: String) {
            showInfo()
        }

        override fun onStatusChanged(
            provider: String, status: Int,
            extras: Bundle
        ) {
            showInfo()
        }
    }

    // текущие данные по широте и долготе
    private var userLongitude: Double = 0.0;
    private var userLatitude: Double = 0.0;
    private var currentLongitudeGPS = 0.0;
    private var currentLatitudeGPS = 0.0;
    private var currentLongitudeNetwork = 0.0;
    private var currentLatitudeNetwork = 0.0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Проверяем есть ли разрешение
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешения нет. Нужно ли показать пользователю пояснения?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Показываем пояснения
            } else {
                // Пояснений не требуется, запрашиваем разрешение
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        } else {
            //
        }

    }

    override fun onResume() {
        super.onResume()
        startTracking()
    }

    override fun onPause() {
        super.onPause()
        stopTracking()
    }

    fun startTracking() {
        // Проверяем есть ли разрешение
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // Здесь код работы с разрешениями...
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 10f, locationListener
            )
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 10f, locationListener
            )
            showInfo()
        }
    }

    fun stopTracking() {
        locationManager!!.removeUpdates(locationListener)
    }

    fun buttonOpenSettings(view: View) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun showInfo(location: Location? = null) {
        val isGpsOn = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkOn = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        findViewById<TextView>(R.id.gps_status).text =
            if (isGpsOn) "GPS ON" else "GPS OFF"
        findViewById<TextView>(R.id.network_status).text =
            if (isNetworkOn) "Network ON" else "Network OFF"

        findViewById<TextView>(R.id.selectedPointLongitude).text =
            "Долгота выбранной точки - ${this.userLongitude}"
        findViewById<TextView>(R.id.selectedPointLatitude).text =
            "Широта выбранной точки - ${this.userLatitude}"

        if (location != null) {
            if (location.provider == LocationManager.GPS_PROVIDER) {
                findViewById<TextView>(R.id.gps_coords).text =
                    "GPS: широта = " + location.latitude.toString() +
                            ", долгота = " + location.longitude.toString()
                this.currentLongitudeGPS = location.longitude
                this.currentLatitudeGPS = location.latitude
                if (countDistance(
                        location.longitude,
                        location.latitude,
                        this.userLongitude,
                        this.userLatitude
                    ) <= 100
                ) {
                    findViewById<TextView>(R.id.statusMessageGPS).text =
                        "GPS: Расстояние до точки МЕНЬШЕ 100 метров"
                } else {
                    findViewById<TextView>(R.id.statusMessageGPS).text =
                        "GPS: Расстояние до точки БОЛЬШЕ 100 метров"
                }
            }
            if (location.provider == LocationManager.NETWORK_PROVIDER) {
                findViewById<TextView>(R.id.network_coords).text =
                    "Network: широта = " + location.latitude.toString() +
                            ", долгота = " + location.longitude.toString()
                this.currentLatitudeNetwork = location.latitude
                this.currentLongitudeNetwork = location.longitude
                if (countDistance(
                        location.longitude,
                        location.latitude,
                        this.userLongitude,
                        this.userLatitude
                    ) <= 100
                ) {
                    findViewById<TextView>(R.id.statusMessageNetwork).text =
                        "Network: Расстояние до точки меньше 100 метров"
                } else{
                    findViewById<TextView>(R.id.statusMessageNetwork).text =
                        "Network: Расстояние до точки БОЛЬШЕ 100 метров"
                }
            }
        }
    }

    fun countDistance(
        startLongitude: Double,
        startLatitude: Double,
        endLongitude: Double,
        endLatitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results
        )
        return results[0]
    }

    fun setPointButtonClick(view: View) {
        this.userLatitude = findViewById<EditText>(R.id.latitude).text.toString().toDouble()
        this.userLongitude = findViewById<EditText>(R.id.longitude).text.toString().toDouble()
    }
}