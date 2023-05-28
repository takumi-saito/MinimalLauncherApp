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
import androidx.compose.ui.platform.ComposeView

class AppListFragment : Fragment() {

    private val viewModel: AppListViewModel by lazy {
        AppListViewModel()
    }
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return
            viewModel.notifyGetAppInfoList(context)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_app_list, container, false)

        root.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                AppListScreen(viewModel, onAppClick = { appInfo ->
                    AppUseCase.launch(requireContext(), appInfo)
                })
            }
        }
        viewModel.notifyGetAppInfoList(requireContext())

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