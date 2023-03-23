package com.example.weatherapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherapi.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

    }

    private fun getCurrentLocation() {

        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result

                    if (location == null) {
                        Toast.makeText(this, "null recieved ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

//                        binding.tvLatitude.text = "" + location.latitude
//                        binding.tvLongitude.text = "" + location.longitude
                        fetchCurrentLocationWeather(location.latitude.toString(),location.longitude.toString())

                    }

                }
            } else {
                //open settings here

                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            //request permission here
            requestPermission()
        }
    }
    private fun fetchCurrentLocationWeather(latitude: String, longitude: String){

        ApiUtilities.getApiInterface()?.getCurrentWeather(latitude,longitude,API_KEY)!!
            .enqueue(object : Callback<WeatherResponseModel>{
                override fun onResponse(
                    call: Call<WeatherResponseModel>,
                    response: Response<WeatherResponseModel>
                ) {
                   if (response.isSuccessful){

                       setDataOnView(response.body())

                       Log.d("TAG",response.body().toString())
                       Toast.makeText(this@MainActivity, "API coming", Toast.LENGTH_SHORT).show()
                   }
                }

                override fun onFailure(call: Call<WeatherResponseModel>, t: Throwable) {

                    Toast.makeText(this@MainActivity, "Error something", Toast.LENGTH_SHORT).show()
                }

            })
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setDataOnView(body: WeatherResponseModel?) {

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        binding.tvDateTime.text = currentDate

        binding.tvMaxTemp.text = "Day "+ body!!.main?.tempMax
        binding.tvMinTemp.text = "Day " + body!!.main?.tempMin

        binding.tvFeelsLike.text = body!!.main?.feelsLike.toString()

    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION_ACCESS
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_LOCATION_ACCESS = 100
        const val API_KEY="227432c257b3080f5cf1989ed4df8f73"
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_LOCATION_ACCESS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()

            }
        }

    }


}
