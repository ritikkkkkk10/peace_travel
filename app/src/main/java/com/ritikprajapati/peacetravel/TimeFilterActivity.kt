package com.ritikprajapati.peacetravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ritikprajapati.peacetravel.databinding.ActivityTimeFilterBinding

class TimeFilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeFilterBinding.inflate(layoutInflater)

        if (binding != null) {
            setupClickListeners()
            setContentView(binding.root)
        } else {
            // Handle null binding
            // For example, show an error message or finish the activity
            finish()
        }
    }

    private fun setupClickListeners(){
        binding.time740.setOnClickListener { launchPeopleThisTimeActivity("07.40-08.00") }
        binding.time840.setOnClickListener { launchPeopleThisTimeActivity("08.40-09.00") }
        binding.time940.setOnClickListener { launchPeopleThisTimeActivity("09.40-10.00") }
        binding.time1040.setOnClickListener { launchPeopleThisTimeActivity("10.40-11.00") }
        binding.time1140.setOnClickListener { launchPeopleThisTimeActivity("11.40-12.00") }
        binding.time1240.setOnClickListener { launchPeopleThisTimeActivity("12.40-13.00") }
        binding.time1340.setOnClickListener { launchPeopleThisTimeActivity("13.40-14.00") }
        binding.time1440.setOnClickListener { launchPeopleThisTimeActivity("14.40-15.00") }
        binding.time1540.setOnClickListener { launchPeopleThisTimeActivity("15.40-16.00") }
        binding.time1640.setOnClickListener { launchPeopleThisTimeActivity("16.40-17.00") }
        binding.time1740.setOnClickListener { launchPeopleThisTimeActivity("17.40-18.00") }
        binding.time1840.setOnClickListener { launchPeopleThisTimeActivity("18.40-19.00") }
        binding.time1940.setOnClickListener { launchPeopleThisTimeActivity("19.40-20.00") }
        binding.time2040.setOnClickListener { launchPeopleThisTimeActivity("20.40-21.00") }
        binding.time2140.setOnClickListener { launchPeopleThisTimeActivity("21.40-22.00") }
        binding.time2240.setOnClickListener { launchPeopleThisTimeActivity("22.40-23.00") }
        binding.time2340.setOnClickListener { launchPeopleThisTimeActivity("23.40-24.00") }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchPeopleThisTimeActivity(timeRange : String) {
        val intent = Intent( this, PeopleThisTimeActivity::class.java)
        intent.putExtra("TimeRange", timeRange)
        startActivity(intent)
    }

}