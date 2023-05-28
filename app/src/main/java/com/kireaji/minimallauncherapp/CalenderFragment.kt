package com.kireaji.minimallauncherapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    private lateinit var adapter: DateAdapter
    private val dateTimeChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val dateInfoList = DateInfoUtil().createDateInfoList()
            adapter.submitList(dateInfoList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_calender, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)
        adapter = DateAdapter()
        recyclerView.adapter = adapter

        val dateInfoList = DateInfoUtil().createDateInfoList()

        adapter.submitList(dateInfoList)
        val layoutManager = GridLayoutManager(requireContext(), 7)
        recyclerView.layoutManager = layoutManager

        activity?.registerReceiver(dateTimeChangedReceiver, IntentFilter().also {
            it.addAction(Intent.ACTION_TIME_TICK)
            it.addAction(Intent.ACTION_TIMEZONE_CHANGED)
            it.addAction(Intent.ACTION_TIME_CHANGED)
        })
        return root
    }

    override fun onDestroyView() {
        activity?.unregisterReceiver(dateTimeChangedReceiver)
        super.onDestroyView()
    }
}