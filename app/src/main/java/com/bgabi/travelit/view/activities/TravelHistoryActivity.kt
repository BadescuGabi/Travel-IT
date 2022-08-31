package com.bgabi.travelit.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bgabi.travelit.R
import com.bgabi.travelit.helpers.GeoCodingLocation
import com.bgabi.travelit.helpers.MapData
import com.bgabi.travelit.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

var lat: String? = null
var long: String? = null

class TravelHistoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var currentUser: User
    private  var userLocations: ArrayList<String> =  ArrayList<String>()
    private val apiKey = "AIzaSyDenNpWsH58Xq0ouEM_7bocrvvUNpp_EpM"
    private lateinit var mapFragment: SupportMapFragment
    private var infoWindow: ViewGroup? = null
    public lateinit var mMap: GoogleMap
    private lateinit var currentLocation: String
    private val defaultLocation: String = "Strada Pechea 8, București"
    private val LOCATION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currentUser = intent.getSerializableExtra("currentUser") as User
        currentUser.userPosts.forEach(){
            userLocations.add(it.postLocation.toString())
        }

        // show map
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        infoWindow = layoutInflater.inflate(R.layout.location_marker, null) as ViewGroup
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        userLocations.forEach(){
            val locationAddress = GeoCodingLocation()
            locationAddress.getAddressFromLocation(it, applicationContext, GeoCoderHandler(this))
        }


        if (mMap != null) {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                mMap?.isMyLocationEnabled = true

                val mapSettings = mMap?.uiSettings
                mapSettings?.isZoomControlsEnabled = true
                mapSettings?.isScrollGesturesEnabled = true
                mapSettings?.isRotateGesturesEnabled = true
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)
            }
        }



    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Unable to show location - permission required", Toast.LENGTH_LONG).show()
            } else {
                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun getCoordsByAddress(address: String): String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocationName(address, 1)
        if (addressList != null && addressList.size > 0) {
            val address = addressList.get(0) as Address
            lat = address.latitude.toString()
            long = address.longitude.toString()
        }
        return "$lat+$long"
    }

    companion object {
        private class GeoCoderHandler(private val activity: TravelHistoryActivity) : Handler() {
            override fun handleMessage(message: Message) {
                lat = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("latitude")
                    }
                    else -> null
                }
                long = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("longitude")
                    }
                    else -> null
                }
                val sydney = LatLng(-34.0, 151.0)
                if (lat != null && long != null){
                    val location = LatLng(lat!!.toDouble(), long!!.toDouble())
                    activity.mMap.addMarker(MarkerOptions().position(location).title("Clinica Medicover Pipera"))
                    activity.mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                }
                else {
                    activity.mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                    activity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        // or call onBackPressed()
        return true
    }
}
