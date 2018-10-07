package com.example.savedplaces

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var locationManager :LocationManager?=null
    var locationListener :LocationListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location?) {
                //check location is not null and then pass the changed location to variable
                if(location!=null) {
                    mMap.clear()
                    val userLocation = LatLng(location!!.latitude, location!!.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }
        else{
            intent = intent
            val info = intent.getStringExtra("new")
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            if(info=="new"){
                mMap.clear()
                val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                var userLastLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,17f))
            }else
            {

            }
        }

    }

    val myListener = object :GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            var address = ""
            try{
               val addressList =geocoder.getFromLocation(p0!!.latitude,p0!!.longitude,1)
                if(addressList!=null && addressList.size>0){
                    if(addressList[0].thoroughfare!=null){
                        address+=addressList[0]
                    }
                    if(addressList[0].subThoroughfare!=null){
                        address+=addressList[0]
                    }
                }
                else
                {
                    address = "New Place"
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

            mMap.addMarker(MarkerOptions().position(p0!!).title(address))



            try{
                val database = openOrCreateDatabase("PLACES",Context.MODE_PRIVATE,null)
                val latitude = p0.latitude.toString()
                val longitude = p0.longitude.toString()
                database.execSQL("CREATE TABLE IF NOT EXISTS PLACES (PLACENAME VARCHAR,LATITUDE VARCHAR,LONGITUDE VARCHAR)")
                //first store the statement of insertion in a variable
                val toCompile = "INSERT INTO PLACES(PLACENAME,LATITUDE,LONGITUDE)VALUES(?,?,?)"
                //connect the compile statement to the database and store it in a variable
                val compileStatement = database.compileStatement(toCompile)
                //bind the values to the compile statement
                compileStatement.bindString(1,address)
                compileStatement.bindString(2,latitude)
                compileStatement.bindString(3,longitude)
                //after binding execute the compileStatement
                compileStatement.execute()
            }catch (e:Exception){
                e.printStackTrace()
            }
            Toast.makeText(applicationContext,"New Place Created",Toast.LENGTH_LONG).show()

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(grantResults.size>0){
          if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

              locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

          }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
