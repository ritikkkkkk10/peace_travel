package com.ritikprajapati.peacetravel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ritikprajapati.peacetravel.databinding.FragmentAvailabilityBinding
import com.ritikprajapati.peacetravel.databinding.FragmentRequirementBinding

class AvailabilityFragment : Fragment() {

    private lateinit var binding: FragmentAvailabilityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvailabilityBinding.inflate(inflater, container, false)

        //binding.cardViewPlaceFilter.setOnClickListener { startActivity(Intent(context, ChatListActivity::class.java)) }

//        binding.cardViewConnect.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Confirmation!")
//                .setMessage("You will be directed to linkedIn")
//                .setPositiveButton("Confirm") { _, _ ->
//                    openLinkedIn()
//                }
//                .setNegativeButton("Stay") {dialog, _ ->
//                    dialog.dismiss()
//                }
//                .show()
//        }

        binding.cardViewBuy.setOnClickListener { startActivity(Intent(context, ViewRentActivity::class.java)) }

        binding.cardViewTimeFilter.setOnClickListener { startActivity(Intent(context, TimeFilterActivity::class.java)) }
        binding.cardViewRentSell.setOnClickListener { startActivity(Intent(context, GiveInRentActivity::class.java)) }
        binding.cardViewPlaceFilter.setOnClickListener { startActivity(Intent(context, PeopleTextedListActivity::class.java)) }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

}