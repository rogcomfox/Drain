package com.nusantarian.flow.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.nusantarian.flow.R

class TrashListFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trash_list, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progress_circular)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "List Sampah"
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        progressBar.visibility = View.VISIBLE
        initRecycler()
        return view
    }

    private fun initRecycler() {

    }

}
