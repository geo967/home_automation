package com.cblue.home_automation

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cblue.home_automation.databinding.ActivityDeviceDetailsBinding

class DeviceDetailsActivity : AppCompatActivity() {

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
        binding.tvIp.text = details.ip
        binding.tvHostname.text = details.hostname
        binding.tvCity.text = details.city
        binding.tvRegion.text = details.region
        binding.tvCountry.text = details.country
        binding.tvLocation.text = details.loc
        binding.tvOrg.text = details.org
        binding.tvPostal.text = details.postal
        binding.tvTimezone.text = details.timezone
    }
}
