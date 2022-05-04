package com.example.purrrfectpoi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.purrrfectpoi.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var btnEnviar : Button? = null
    var coordSelec : LatLng? = null;

    var latitud : String? = null;
    var longitud : String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val bundle : Bundle?= intent.extras
        if (bundle != null) {
            latitud = bundle.getString("Latitud")
            longitud = bundle.getString("Longitud")
        }

        this.btnEnviar = findViewById<Button>(R.id.btnEnviarUbicacion)
        this.btnEnviar?.setOnClickListener {
            enviarUbicacion()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        setResult(RESULT_CANCELED)
    }

    private fun enviarUbicacion() {
        val intent = Intent()
        intent.putExtra("Latitud", coordSelec?.latitude.toString())
        intent.putExtra("Longitud", coordSelec?.longitude.toString())

        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        activarMiUbicacion()

        if (latitud != null && longitud != null) {
            this.btnEnviar?.visibility = View.GONE

            mMap.setOnMapClickListener { coordenadas ->
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(coordenadas))
            }

            val ubi = LatLng(latitud!!.toDouble(), longitud!!.toDouble())
            mMap.addMarker(MarkerOptions().position(ubi))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi, 10F))
        }
        else {
            mMap.setOnMapClickListener { coordenadas ->
                coordSelec = coordenadas
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(coordenadas))
                this.btnEnviar?.isEnabled = true
            }

            val mty = LatLng(25.67, -100.31)
            mMap.addMarker(MarkerOptions().position(mty))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mty, 10F))
        }
    }

    private fun activarMiUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
    }

}