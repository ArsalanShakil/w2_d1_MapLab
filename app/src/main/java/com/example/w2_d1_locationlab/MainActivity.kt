package com.example.w2_d1_locationlab

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fastestSpeed = 0.0F

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fun getAddress(lat: Double, lng: Double): String {
            val geocoder = Geocoder(this)
            val list = geocoder.getFromLocation(lat, lng, 1)
            return list[0].getAddressLine(0)
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?)
            {

                Log.d("LOCATION", "MAYBE something?")
                locationResult ?: return
                for (location in locationResult.locations) {
                    coordTxt.text = "${location.longitude.toString()} and ${location.latitude.toString()}"
                    addressTxt.text = getAddress(location.latitude,location.longitude)
                    speedTxt.text = location.speed.toString()
                    if (fastestSpeed < location.speed) {
                        fastestSpeed = location.speed
                        fastestSpeedTxt.text = fastestSpeed.toString()
                    } else {
                        fastestSpeedTxt.text = fastestSpeed.toString()
                    }
                    Log.d("GEOLOCATION", "new location latitude:${location.latitude} and longitude:${location.longitude} and speed:${location.speed}" )
                }
            }
        }

        startTrackingBtn.setOnClickListener {
            Log.d("LOCATION", "On click at least, please!")
            val locationRequest = LocationRequest
                .create()
                .setInterval(1000)
                .setPriority(PRIORITY_HIGH_ACCURACY)

            if ((Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    0)
            } else {

                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper())
            }
        }

        stopTrackingBtn.setOnClickListener {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }


    }
}
