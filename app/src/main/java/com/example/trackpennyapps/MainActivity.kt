package com.example.trackpennyapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.records -> replaceFragment(RecordsFragment(), "RecordsFragment")
                R.id.budget -> replaceFragment(BudgetFragment(), "BudgetFragment")
                R.id.category -> replaceFragment(CategoryFragment(), "CategoryFragment")
                R.id.setting -> replaceFragment(SettingFragment(), "SettingFragment")
            }
            true
        }

        // Set the default fragment
        bottomNavigationView.selectedItemId = R.id.records
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag) // Use replace with a tag
            .addToBackStack(null) // This line adds the fragment transaction to the back stack
            .commit()
    }
    private fun navigateToBudgetFragment() {
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag("BudgetFragment")

        if (existingFragment != null) {
            // If the BudgetFragment exists in the back stack, pop it off and replace it
            fragmentManager.popBackStack("BudgetFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            replaceFragment(BudgetFragment(), "BudgetFragment")
        } else {
            // If the BudgetFragment doesn't exist in the back stack, just replace the fragment
            replaceFragment(BudgetFragment(), "BudgetFragment")
        }
    }
}