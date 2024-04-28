package com.example.intmap

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.intmap.ui.theme.IntmapTheme
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class MainActivity : ComponentActivity(), UserLocationObjectListener, Session.SearchListener, CameraListener {
    lateinit var mapview:MapView
    lateinit var lokationmapkit:UserLocationLayer
    lateinit var searchEdit:EditText
    lateinit var searchManager:SearchManager
    lateinit var searchSession:Session
    private fun submitQuery(query:String){
        searchSession=searchManager.submit(query, VisibleRegionUtils.toPolygon(mapview.map.visibleRegion), SearchOptions(), this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("e6dcd208-4550-43a2-a331-56b4f0005201")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapview = findViewById(R.id.mapview)
        var mapKit:MapKit = MapKitFactory.getInstance()
        requstLocationPermission()
        var locationonmapkit = mapKit.createUserLocationLayer(mapview.mapWindow)
        locationonmapkit.isVisible = true
        lokationmapkit=mapKit.createUserLocationLayer(mapview.mapWindow)
        lokationmapkit.isVisible = true
        lokationmapkit.setObjectListener(this)
        SearchFactory.initialize(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapview.map.addCameraListener(this)
        searchEdit = findViewById(R.id.search_edit)
        searchEdit.setOnEditorActionListener{v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                submitQuery(searchEdit.text.toString())
            }
            false
        }

        installSplashScreen()
        setContent {
            setContentView(R.layout.activity_main)
        }
    }
    private fun requstLocationPermission()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        lokationmapkit.setAnchor(
            PointF((mapview.width *0.5).toFloat(),(mapview.height *0.5).toFloat()),
            PointF((mapview.width *0.5).toFloat(),(mapview.height *0.83).toFloat())
        )
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.user_arrow))
        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon("Icon", ImageProvider.fromResource(this, R.drawable.search_result), IconStyle().
        setAnchor(PointF(0f,0f))
            .setRotationType(RotationType.NO_ROTATION).setZIndex(0f).setScale(1f)
        )
        picIcon.setIcon("pin", ImageProvider.fromResource(this, R.drawable.nothing),
            IconStyle().setAnchor(PointF(0.5f,0.5f)).setRotationType(RotationType.NO_ROTATION).setZIndex(1f).setScale(0.5f))
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x6600001
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects:MapObjectCollection = mapview.map.mapObjects
        mapObjects.clear()
        for(searchResult in response.collection.children){
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            if(response!=null){
                mapObjects.addPlacemark(resultLocation, ImageProvider.fromResource(this, R.drawable.search_result))
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMassage = "Низвестная ошибка!"
        if(error is RemoteError){
            errorMassage = "Беспроводная ошибка!"
        } else if (error is NetworkError){
            errorMassage = "Проблема с интернетом!"
        }
        Toast.makeText(this, errorMassage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished){
            submitQuery(searchEdit.text.toString())
        }
    }
}