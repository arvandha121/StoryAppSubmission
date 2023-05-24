package com.dicoding.storyappsubmission.ui.view.activity.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityMapsBinding
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.view.activity.story.StoryActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var listStory: ArrayList<ListStory>

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listStory =
            intent.getParcelableArrayListExtra<ListStory>(StoryActivity.LIST_STORY) as ArrayList<ListStory>

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mapEnable()
        addLocationListStory()
        mapLocation()
        mapPointMarker()
        getMyLocation()
        setMapsStyle()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun mapEnable() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun addLocationListStory() {
        for (story in listStory) {
            if (story.lat != null && story.lon != null) {
                val position = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(story.name)
                        .snippet(story.description)
                )
            }
        }
        val firstLocation = LatLng(listStory.get(0).lat as Double, listStory.get(0).lon as Double)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15f))
    }

    private fun mapLocation() {
        val dicodingSpace = LatLng(-7.9162518, 112.5878411)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("My Location")
                .snippet("Kos Putra Mulyoagung Malang")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))
    }

    private fun mapPointMarker() {
        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapsStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(MAPS, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(MAPS, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val MAPS = "MapsActivity"
    }
}