package com.example.sample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton

class HospitalMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnCallEmergency: MaterialButton
    private lateinit var tvNearestHospital: TextView

    // The number that will be passed to the phone's dialer
    private var emergencyPhoneNumber: String? = null

    // Default coordinates to use if GPS fails or permissions are denied
    private val defaultLocation = LatLng(15.4828, 120.5943)

    // 1. Setup Permission Launcher
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableUserLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission needed to find hospitals.", Toast.LENGTH_LONG).show()
            // Fallback to default map location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hospital_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCallEmergency = view.findViewById(R.id.btnCallEmergency)
        tvNearestHospital = view.findViewById(R.id.tvNearestHospital)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 2. Initialize the Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 3. Setup Dialer Button Click Listener
        btnCallEmergency.setOnClickListener {
            emergencyPhoneNumber?.let { number ->
                // Implicit Intent: Opens the Android Dialer automatically
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$number")
                startActivity(dialIntent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // Check Permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                // Once we have the user's location, we simulate fetching the nearest hospital
                // TODO: Replace this mock function with the actual Google Places API JSON parsing
                fetchNearestHospitalFromPlacesAPI(currentLatLng)
            } else {
                Toast.makeText(requireContext(), "Searching for GPS signal...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * This function simulates a successful Google Places API response.
     * In production, you will pass the LatLng to your Python server or use the Places SDK.
     */
    private fun fetchNearestHospitalFromPlacesAPI(userLocation: LatLng) {
        // MOCK DATA: Simulating a hospital found 800 meters away
        val mockHospitalName = "Provincial General Hospital"
        val mockHospitalPhone = "0459821234"
        val mockHospitalLocation = LatLng(userLocation.latitude + 0.005, userLocation.longitude + 0.005)

        // 1. Drop a red marker on the map for the hospital
        mMap.addMarker(
            MarkerOptions()
                .position(mockHospitalLocation)
                .title(mockHospitalName)
                .snippet("Emergency: $mockHospitalPhone")
        )

        // 2. Update the UI to allow the user to call immediately
        tvNearestHospital.text = "Nearest: $mockHospitalName"
        emergencyPhoneNumber = mockHospitalPhone
        btnCallEmergency.isEnabled = true
    }
}