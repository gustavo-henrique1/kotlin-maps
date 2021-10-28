package com.example.maps

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap

        val latLngOrigin = LatLng(-23.5676915, -46.6621524)
        val latLngDestination = LatLng(-23.5581213, -46.6638027)

        this.googleMap!!.addMarker(
            MarkerOptions()
                .position(latLngOrigin)
                .title("C6 Bank")
                .icon(
                    BitmapHelper.vectorToBitmap(
                        this,
                        R.drawable.ic_baseline_person_pin_circle_24,
                        ContextCompat.getColor(this, R.color.main)
                    )
                )
        )
        this.googleMap!!.addMarker(
            MarkerOptions()
                .position(latLngDestination)
                .title("Bandtec")
                .icon(
                    BitmapHelper.vectorToBitmap(
                        this,
                        R.drawable.ic_baseline_pin_drop_24,
                        ContextCompat.getColor(this, R.color.main)
                    )
                )
        )
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 14.5f))


        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=-23.5676915,-46.6621524&destination=-23.5581213,-46.6638027&key=AIzaSyBXb9iZ4vmYJXOH4ZlzmEkHal706Qj-a44"
        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                this.googleMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.parseColor("#33C262")))
            }
        }, Response.ErrorListener {
                _ ->
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }
}