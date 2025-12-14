package com.cblue.home_automation.screen

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cblue.home_automation.R
import com.cblue.home_automation.model.IpDetails
import com.cblue.home_automation.databinding.ActivityDeviceDetailsBinding

class DeviceDetailsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val details = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DETAILS", IpDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("DETAILS")
        }

        if (details != null) {
            bindData(details)
        } else {
            finish() // safety fallback
        }
    }

    private fun bindData(details: IpDetails) {
        binding.rowIp.tvLabel.text = "IP Address"
        binding.rowIp.tvValue.text = details.ip

        binding.rowHostname.tvLabel.text = "Hostname"
        binding.rowHostname.tvValue.text = details.hostname

        binding.rowCity.tvLabel.text = "City"
        binding.rowCity.tvValue.text = details.city

        binding.rowRegion.tvLabel.text = "Region"
        binding.rowRegion.tvValue.text = details.region

        binding.rowCountry.tvLabel.text = "Country"
        binding.rowCountry.tvValue.text = details.country

        binding.rowLocation.tvLabel.text = "Location"
        binding.rowLocation.tvValue.text = details.loc

        binding.rowOrg.tvLabel.text = "Organization"
        binding.rowOrg.tvValue.text = details.org

        binding.rowPostal.tvLabel.text = "Postal Code"
        binding.rowPostal.tvValue.text = details.postal

        binding.rowTimezone.tvLabel.text = "Timezone"
        binding.rowTimezone.tvValue.text = details.timezone

    }
}