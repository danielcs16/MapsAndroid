package br.com.danielcastro.mapsandroid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import br.com.danielcastro.mapsandroid.utils.PermissaoUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val permissoeslocalizacao = listOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private lateinit var locationManager : LocationManager

    private lateinit var  locationListener : LocationListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        PermissaoUtils.validaPermissao(permissoeslocalizacao.toTypedArray(), this, 1)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(resposta in grantResults) {
            if (resposta == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(applicationContext, "Sem permissão, sem acesso", Toast.LENGTH_LONG).show()
            } else {
                requestLocationUpdates()
            }
        }
    }

    private fun initLocationListener() {
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location?) {
                val minhaPosicao = LatLng(location?.latitude!!, location?.longitude!!)
                addMarcador(minhaPosicao, "Mãe, tô aqui!")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaPosicao, 12f))
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

            }

            override fun onProviderEnabled(p0: String?) {

            }

            override fun onProviderDisabled(p0: String?) {

            }
        }
    }


    private fun addMarcador(latLng: LatLng, titulo: String){
        mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador)))
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        initLocationListener()
        requestLocationUpdates()

        // Add a marker in Sydney and move the camera
        val fiapPaulista = LatLng(-23.563747, -46.652458)
        val fiapVilaOlimpia = LatLng(-23.593496, -46.685315)

        mMap.setOnMapClickListener {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val endereco = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            addMarcador(it, endereco[0].subLocality )
        }

        mMap.addMarker(MarkerOptions()
                .position(fiapPaulista)
                .title("Marker em Fiap Paulista")
                .snippet(getSnippet(fiapPaulista))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        val circulo = CircleOptions()
        circulo.center(fiapPaulista)
        circulo.radius(200.0)
        circulo.fillColor(Color.argb(128,0,51,102))
        circulo.strokeWidth(1f)

        mMap.addCircle(circulo)


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiapPaulista, 12f))


        mMap.addMarker(MarkerOptions()
                .position(fiapVilaOlimpia)
                .title("Marker em Fiap Vl. Olimpia")
                .snippet(getSnippet(fiapVilaOlimpia))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador)))

        mMap.setOnMapLongClickListener {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val endereco = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            addMarcador(it, getSnippet(it) )
        }


    }

    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager = getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener)

        }
    }


    private fun getSnippet(latLng: LatLng): String {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val endereco = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        return "${endereco[0].thoroughfare}, ${endereco[0].subThoroughfare} " +
                "${endereco[0].subLocality}, ${endereco[0].locality} - " +
                "${endereco[0].postalCode}"
    }
}

