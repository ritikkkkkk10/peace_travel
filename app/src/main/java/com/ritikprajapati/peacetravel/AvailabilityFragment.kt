package com.ritikprajapati.peacetravel

import UserAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ritikprajapati.peacetravel.databinding.FragmentAvailabilityBinding
import com.ritikprajapati.peacetravel.databinding.FragmentRequirementBinding
import java.util.Calendar

class AvailabilityFragment : Fragment() {

    private lateinit var binding: FragmentAvailabilityBinding
    private lateinit var userAdapter: UserAdapter
    private var userList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvailabilityBinding.inflate(inflater, container, false)

        binding.cardViewBuy.setOnClickListener { startActivity(Intent(context, ViewRentActivity::class.java)) }

        binding.cardViewTimeFilter.setOnClickListener { startActivity(Intent(context, TimeFilterActivity::class.java)) }
        binding.cardViewRentSell.setOnClickListener { startActivity(Intent(context, GiveInRentActivity::class.java)) }
        binding.cardViewPlaceFilter.setOnClickListener { startActivity(Intent(context, PeopleTextedListActivity::class.java)) }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentDate2 = "$year-$month-$day"
    }

    private fun openLinkedIn() {
        val linkedinUrl = "https://www.linkedin.com/in/ritik-prajapati-b65b9424b/"

        // Try to open the LinkedIn app
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://in/your-profile-id"))
            startActivity(intent)
        } catch (e: Exception) {
            // If the LinkedIn app is not installed, open the URL in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("AvailabilityFragment", "in the am availability")
    }

}