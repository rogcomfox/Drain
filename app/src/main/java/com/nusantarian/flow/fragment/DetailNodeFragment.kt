package com.nusantarian.flow.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.flow.R
import java.util.*


class DetailNodeFragment : Fragment(), View.OnClickListener {

    private lateinit var toolbar: Toolbar
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var documentReference: DocumentReference
    private lateinit var heightChart: ScatterChart
    private lateinit var flowChart: ScatterChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_node, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        flowChart = view.findViewById(R.id.flow_chart)
        heightChart = view.findViewById(R.id.height_chart)
        view.findViewById<MaterialButton>(R.id.btn_visit).setOnClickListener(this)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.title = "Node 1"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
        documentReference = firebaseFirestore.collection("Node").document("O4BWCJ71efxwcf5v8812")
        addFlowData()
        addHeightData()
        return view
    }

    private fun addFlowData() {
        val flowData: MutableList<Entry> = mutableListOf()
        flowData.add(Entry(10f, 1f))
        flowData.add(Entry(78f, 2f))
        flowData.add(Entry(28f, 3f))
        flowData.add(Entry(100f, 4f))
        flowData.add(Entry(22f, 5f))
        flowData.add(Entry(2f, 1f))
        flowData.add(Entry(102f, 2f))
        flowData.add(Entry(62f, 3f))
        flowData.add(Entry(82f, 4f))
        flowData.add(Entry(66f, 5f))
        flowData.add(Entry(94f, 1f))
        flowData.add(Entry(39f, 2f))
        flowData.add(Entry(109f, 3f))
        flowData.add(Entry(119f, 4f))
        flowData.add(Entry(9f, 5f))
        val second: ArrayList<Any> = arrayListOf()
        second.add(1)
        second.add(2)
        second.add(3)
        second.add(4)
        second.add(5)
        val dataset = ScatterDataSet(flowData, "Data Flow Sensor (m/detik)")
        flowChart.animateY(3000)
        val data = ScatterData(dataset)
        flowChart.data = data
    }

    private fun addHeightData() {
        val heightData: MutableList<Entry> = mutableListOf()
        heightData.add(Entry(0.8f, 1f))
        heightData.add(Entry(1.4f, 2f))
        heightData.add(Entry(1.9f, 3f))
        heightData.add(Entry(2.2f, 4f))
        heightData.add(Entry(2.5f, 5f))
        heightData.add(Entry(1.1f, 1f))
        heightData.add(Entry(1.7f, 2f))
        heightData.add(Entry(1.9f, 3f))
        heightData.add(Entry(2.2f, 4f))
        heightData.add(Entry(2.5f, 5f))
        heightData.add(Entry(0.8f, 1f))
        heightData.add(Entry(1.4f, 2f))
        heightData.add(Entry(1.7f, 3f))
        heightData.add(Entry(1.9f, 4f))
        heightData.add(Entry(2.2f, 5f))
        val second: ArrayList<Any> = arrayListOf()
        second.add(1)
        second.add(2)
        second.add(3)
        second.add(4)
        second.add(5)
        val dataset = ScatterDataSet(heightData, "Sensor Ketinggian air (cm)")
        heightChart.animateY(3000)
        val data = ScatterData(dataset)
        heightChart.data = data
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.btn_visit){
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, MainFragment())
                .commit()
        }
    }

}
