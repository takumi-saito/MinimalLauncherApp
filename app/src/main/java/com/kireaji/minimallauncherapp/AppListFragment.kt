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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppListFragment : Fragment() {

    private lateinit var adapter: AppAdapter
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            adapter.updateList(AppUseCase.getAppInfoList(this@AppListFragment.requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_app_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)
        adapter = AppAdapter { _, appInfo -> AppUseCase.launch(requireContext(), appInfo) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.submitList(AppUseCase.getAppInfoList(requireContext()))

        activity?.registerReceiver(packageReceiver, IntentFilter().also {
            it.addAction(Intent.ACTION_PACKAGE_ADDED)
            it.addAction(Intent.ACTION_PACKAGE_REMOVED)
            it.addAction(Intent.ACTION_PACKAGE_CHANGED)
            it.addAction(Intent.ACTION_PACKAGE_REPLACED)
            it.addDataScheme("package")
        })
        return root
    }

    override fun onDestroyView() {
        activity?.unregisterReceiver(packageReceiver)
        super.onDestroyView()
    }
}