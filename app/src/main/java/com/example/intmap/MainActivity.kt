@file:Suppress("DEPRECATION")

package com.example.intmap

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.intmap.R.id.mapview

import com.example.intmap.ui.theme.IntmapTheme
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import android.Manifest;

class MainActivity : ComponentActivity() {

    private lateinit var mapview: MapView

    lateinit var btnTraffic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("e6dcd208-4550-43a2-a331-56b4f0005201")
        MapKitFactory.initialize(this)
        val mapKit:MapKit = MapKitFactory.getInstance()
        requstLocationPermission()
        setContentView(R.layout.activity_main)

        mapview = findViewById(R.id.mapview)
        btnTraffic = findViewById(R.id.btntraffic)

        mapview.map.move(CameraPosition(Point(55.755864, 37.617698),10.0f,0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2f), null)

        var traffic = mapKit.createTrafficLayer(mapview.mapWindow)
        var trafficIsOn = false

        btnTraffic.setOnClickListener {
            if (trafficIsOn)
                trafficIsOn = false
            else
                trafficIsOn = true
            traffic.isTrafficVisible = trafficIsOn
        }

        var locationUser = mapKit.createUserLocationLayer(mapview.mapWindow)
        locationUser.isVisible = true

    }

    private fun requstLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }
    override fun onStart() {
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}