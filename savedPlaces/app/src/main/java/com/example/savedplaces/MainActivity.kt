package com.example.savedplaces

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater =menuInflater
        menuInflater.inflate(R.menu.add_place,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item!!.itemId==R.menu.add_place)
        {
            val intent =Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onCreate(savedInstanceState: Bundle?) {


        var placesList = ArrayList<String>()
        var locationList = ArrayList<LatLng>()

        //DataBase for storing  retrieving names and data along with linking the already present items to the

        try{

            val database =openOrCreateDatabase("Places", Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS PLACES (PLACENAME VARCHAR,LATITUDE VARCHAR,LONGITUDE VARCHAR)")
            val cursor = database.rawQuery("SELECT * FROM PLACES",null)

            val placeIndex = cursor.getColumnIndex("PLACENAME")
            val latitudeIndex  = cursor.getColumnIndex("LATITUDE")
            val longitudeIndex = cursor.getColumnIndex("LONGITUDE")

            cursor.moveToFirst()
            locationList.clear()
            placesList.clear()

            while (cursor!=null){
                placesList.add(cursor.getString(placeIndex))

                val latitude = getString(cursor.getColumnIndex("LATITUDE"))
                val longitude = getString(cursor.getColumnIndex("LONGITUDE"))

                val latitudeTODouble = latitude.toDouble()
                val longitudeToDouble = longitude.toDouble()

                val locationInLatLng = LatLng(latitudeTODouble,longitudeToDouble)
                locationList.add(locationInLatLng)

                cursor.moveToNext()
                //arrayAdapter.notifyDataSetChanged()
            }

        }catch(e:Exception){
            e.printStackTrace()
        }

        val arrayAdapter  = ArrayAdapter(this,android.R.layout.simple_list_item_1,placesList)
        listView.adapter = arrayAdapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

           val intent = Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("placeName",placesList[position])
            intent.putExtra("latitude",locationList[position])
            intent.putExtra("info","old")
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}

