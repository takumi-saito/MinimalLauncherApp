package com.kireaji.minimallauncherapp.ui

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
import androidx.fragment.app.viewModels
import com.kireaji.minimallauncherapp.R
import com.kireaji.minimallauncherapp.ui.compose.CalendarScreen
import com.kireaji.minimallauncherapp.ui.viewmodel.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()

    private val dateTimeChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshCalendar()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_calender, container, false)
        root.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                CalendarScreen(viewModel)
            }
        }

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
