package com.locationapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import android.widget.Toast
import androidx.work.Worker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class LocationWorker : Worker() {
    companion object {
        const val TAG = "Location Worker"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun doWork(): Result {

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(applicationContext, applicationContext.getString(R.string.location_required), Toast.LENGTH_LONG).show()
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Toast.makeText(applicationContext, "Lat: ${location!!.latitude}, Long ${location!!.longitude}", Toast.LENGTH_LONG).show()

                    //ToDO
                    /**
                     *1. SQL Lite Database store history of the location in table and use a content provider
                     * if you need to share the data
                     *2. File Storage to store history in a file
                     */
                }


        return Result.SUCCESS
    }
}