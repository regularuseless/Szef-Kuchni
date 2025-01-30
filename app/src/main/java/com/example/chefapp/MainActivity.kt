package com.example.chefapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(){
    val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var frameLayout: FrameLayout
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        recipeViewModel.loadRecipesFromFile(this)
        frameLayout = findViewById(R.id.frame_layout)
        tabLayout = findViewById(R.id.tab_layout)

        setupTabIcons()
        switchToTab(2)
        replaceFragment(MainPage(),"MainPage")

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> replaceFragment(SettingsFragment(),"SettingsFragment")
                    1 -> replaceFragment(CartFragment(),"CartFragment")
                    2 -> replaceFragment(MainPage(),"MainPage")
                    3 -> replaceFragment(SearchFragment(),"SearchFragment")
                    4 -> replaceFragment(CustomDishFragment(),"CustomDishFragment")
                    5 -> replaceFragment(ProfileFragment(),"ProfileFragment")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun replaceFragment(fragment: Fragment,tag: String) {
        supportFragmentManager.beginTransaction()
        .replace(R.id.frame_layout, fragment, tag)
            .commit()
    }
    private fun setupTabIcons()
    {
        tabLayout.getTabAt(0)?.setIcon(R.drawable.icons8_settings)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.cart_icon)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.house_icon)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.search_icon)
        tabLayout.getTabAt(4)?.setIcon(R.drawable.fridge_icon)
        tabLayout.getTabAt(5)?.setIcon(R.drawable.profile_icon)
    }
    fun switchToTab(tabIndex: Int) {
        if (tabIndex >= 0 && tabIndex < tabLayout.tabCount) {
            tabLayout.getTabAt(tabIndex)?.select()
        }
    }
}