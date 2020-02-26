package com.nusantarian.flow.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.nusantarian.flow.Constant.Companion.TAG
import com.nusantarian.flow.R
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.frame.Frame
import kotlinx.android.synthetic.main.fragment_qr_scanner.*

class QrScannerFragment : Fragment(), View.OnClickListener {

    private lateinit var mToolbar: Toolbar
    private lateinit var cameraView: CameraView
    private lateinit var detector: FirebaseVisionBarcodeDetector
    private var isDetected: Boolean = false
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var scanQR: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)
        isFlashAvailable()
        mToolbar = view.findViewById(R.id.toolbar)
        cameraView = view.findViewById(R.id.camera_scan)
        scanQR = view.findViewById(R.id.scan)
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.qr_code_scanner)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cameraManager =
            (activity as AppCompatActivity).getSystemService(Context.CAMERA_SERVICE) as CameraManager
        setupCamera()
        scanQR.setOnClickListener(this)
        return view
    }

    private fun isFlashAvailable(){
        val isAvailable: Boolean = context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!isAvailable)
            showNoFlashError()
    }

    private fun showNoFlashError(){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this.context!!)
        alertDialog.setTitle("Oops")
        alertDialog.setMessage("FlashLight Not Available In This Device....")
        alertDialog.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            activity!!.finish()
        }
        alertDialog.create().show()
    }

    private fun setupCamera() {
        cameraView.setLifecycleOwner(this)
        cameraView.addFrameProcessor { frame -> processImage(getVisionImageFromFrame(frame)) }
        val options =
            FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build()
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
    }

    private fun processImage(image: FirebaseVisionImage?) {
        if (!isDetected) {
            detector.detectInImage(image!!).addOnSuccessListener { firebaseVisionBarcode ->
                processResult(firebaseVisionBarcode)
            }.addOnFailureListener { e: Exception ->
                Log.e(TAG, e.toString())
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processResult(firebaseVisionBarcode: List<FirebaseVisionBarcode>) {
        if (firebaseVisionBarcode.isNotEmpty()) {
            isDetected = true
            for (item in firebaseVisionBarcode) {
                when (item.valueType) {
                    FirebaseVisionBarcode.TYPE_TEXT -> item.rawValue?.let { createDialog(it) }
                    FirebaseVisionBarcode.TYPE_URL -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.rawValue))
                        startActivity(intent)
                        activity?.finish()
                    }
                    FirebaseVisionBarcode.TYPE_CONTACT_INFO -> {
                        val info = "Name: " +
                                item.contactInfo?.name?.formattedName.toString() +
                                "\n" +
                                "Address: " +
                                item.contactInfo!!.addresses[0].addressLines.contentToString() +
                                "\n" +
                                "Email: " +
                                item.contactInfo!!.emails[0].address
                        createDialog(info)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun createDialog(rawValue: String) {
        val builder = AlertDialog.Builder(this.activity!!)
        builder.setMessage(rawValue)
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getVisionImageFromFrame(frame: Frame): FirebaseVisionImage? {
        val data: ByteArray = frame.getData()
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width).build()
        return FirebaseVisionImage.fromByteArray(data, metadata)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.scan) {
                cameraId = cameraManager.cameraIdList[0]
                if (tv_flash.text.toString().contains("On")){
                    tv_flash.text = "Tap To Turn Off Flash"
                    try {
                        cameraManager.setTorchMode(cameraId, false)
                    } catch (e: CameraAccessException){
                        e.printStackTrace()
                    }
                } else {
                    tv_flash.text = "Tap To Turn On Flash"
                    try {
                        cameraManager.setTorchMode(cameraId, true)
                    } catch (e: CameraAccessException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
