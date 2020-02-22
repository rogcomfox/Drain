package com.nusantarian.flow.fragment

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext
import com.nusantarian.flow.Constant.Companion.MAPVIEW_BUNDLE_KEY
import com.nusantarian.flow.R

class MainFragment : Fragment(), OnMapReadyCallback {

    private lateinit var ft: FragmentTransaction
    private lateinit var mToolbar: Toolbar
    private lateinit var mapView: MapView
    private lateinit var progressCircular: ProgressBar
    private var mGeoApiContext: GeoApiContext? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mToolbar = view.findViewById(R.id.toolbar)
        mapView = view.findViewById(R.id.map_view)
        progressCircular = view.findViewById(R.id.progress_circular)

        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.app_name)

        ft = activity!!.supportFragmentManager.beginTransaction()


//        progressCircular.visibility(View.VISIBLE)
        initGoogleMap(savedInstanceState)
        setHasOptionsMenu(true)
        return view
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        if (mGeoApiContext == null) {
            mGeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.maps_api))
                .build()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_trash -> {

            }
            R.id.nav_qr_code -> {
                ft.replace(R.id.frame_container, QrScannerFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_about -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("marker"))
        map?.isMyLocationEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
