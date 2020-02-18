package com.nusantarian.flow.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.nusantarian.flow.Constant.Companion.ERROR_DIALOG_REQUEST
import com.nusantarian.flow.Constant.Companion.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.nusantarian.flow.Constant.Companion.PERMISSIONS_REQUEST_ENABLE_GPS
import com.nusantarian.flow.Constant.Companion.TAG
import com.nusantarian.flow.R
import com.nusantarian.flow.fragment.MainFragment

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private var mLocationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set frame layout for fragment
        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.frame_container
        setContentView(
            frameLayout, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        if (savedInstanceState == null) {
            getMainFragment()
        }

        //listen for changes in back stack
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                val enableGps = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGps, PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        builder.create().show()
    }

    private fun checkMapServices(): Boolean {
        if (isServicesOk() && isMapsEnabled()) {
            return true
        }
        return false
    }

    private fun isServicesOk(): Boolean {
        Log.d(TAG, "isServicesOk: checking google services version ")
        val available: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(TAG, "isServicesOK: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d(TAG, "isServicesOk: an error occurred")
                val dialog: Dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun isMapsEnabled(): Boolean {
        val manager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            return false
        }
        return true
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
            getMainFragment()
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(
                this, permissions,
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun getMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, MainFragment())
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mLocationPermissionGranted = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: Called")
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                getMainFragment()
            } else {
                getLocationPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getMainFragment()
            } else {
                getLocationPermission()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack() else super.onBackPressed()
    }

    override fun onBackStackChanged() {

    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
}
