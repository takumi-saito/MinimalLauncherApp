package com.kireaji.minimallauncherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class FullscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        val pager = findViewById<ViewPager>(R.id.pager)
        val adapter =  HomePagerAdapter(pagerFragmentList(), this)
        pager.adapter = adapter
    }

    override fun finish() {
        // アプリ終了しない
    }

    private inner class HomePagerAdapter(val fragmentList: List<Fragment>, fa: FragmentActivity) : FragmentStatePagerAdapter(fa.supportFragmentManager) {
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }
    }

    companion object {
        fun pagerFragmentList(): List<Fragment> {
            return listOf(
                CalenderFragment(),
                AppListFragment()
            )
        }
    }
}