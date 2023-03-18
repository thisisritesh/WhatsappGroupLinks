package com.riteshmaagadh.cms.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.riteshmaagadh.cms.R
import com.riteshmaagadh.cms.databinding.ActivityMainBinding
import com.riteshmaagadh.cms.ui.addlink.AddLinkFragment
import com.riteshmaagadh.cms.ui.categories.CategoriesFragment
import com.riteshmaagadh.cms.ui.home.HomeFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        switchScreen(HomeFragment.newInstance())

        binding.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> switchScreen(HomeFragment.newInstance())
                R.id.nav_categories -> switchScreen(CategoriesFragment.newInstance())
                R.id.nav_add_link -> switchScreen(AddLinkFragment.newInstance())
                else -> { false }
            }
        })


        binding.bottomNavigationView.setOnItemReselectedListener {

        }



    }

    private fun switchScreen(fragment: Fragment?): Boolean {
        return if (fragment == null) {
            false
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss()
            true
        }
    }

}