package com.cblue.home_automation.screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cblue.android_nsd.flow.library.nsd.rx.NsdManagerFlow
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryConfiguration
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryServiceFound
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryServiceLost
import com.cblue.home_automation.R
import com.cblue.home_automation.adapter.DiscoveryAdapter
import com.cblue.home_automation.database.AppDatabase
import com.cblue.home_automation.model.DiscoveryRecord
import com.cblue.home_automation.repository.DeviceRepository
import com.cblue.home_automation.util.DiscoveryVMFactory
import com.cblue.home_automation.util.toDiscoveryRecord
import com.cblue.home_automation.viewmodel.DiscoveryViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DeviceListScreen : AppCompatActivity() {

    private val toggleButton: Button by bind(R.id.toggle)
    private val statusTextView: TextView by bind(R.id.status)
    private val recyclerView: RecyclerView by bind(R.id.recycler_view)

    private val nsdManagerRx: NsdManagerFlow by lazy { NsdManagerFlow(this) }
    private val adapter: DiscoveryAdapter by lazy { DiscoveryAdapter() }
    private val viewModel: DiscoveryViewModel by viewModels {
        DiscoveryVMFactory(
            DeviceRepository(AppDatabase.get(this).deviceDao())
        )
    }
    private var discoveryJob: Job? = null

    private val progressBar: View by bind(R.id.progressBar)

    private fun <T : View> Activity.bind(@IdRes id: Int) = lazy { findViewById<T>(id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list_screen)

        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { device ->
          viewModel.onDeviceClicked(device)
        }

        viewModel.ipDetails.observe(this) { details ->
            val intent = Intent(this, DeviceDetailsScreen::class.java)
            intent.putExtra("DETAILS", details)
            startActivity(intent)
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.devices.observe(this) { cached ->
            adapter.submitList(cached.map {
                DiscoveryRecord(it.name,it.address)
            })
        }

        viewModel.isDiscovering.observe(this) { discovering ->
            statusTextView.setText(
                if (discovering)
                    R.string.activity_main_status_discovery_on
                else
                    R.string.activity_main_status_discovery_off
            )
        }

        toggleButton.setOnClickListener { toggle() }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDiscovery()
    }

    private fun toggle() {
        if (discoveryJob == null) startDiscovery()
        else stopDiscovery()
    }

    private fun startDiscovery() {
        if (discoveryJob != null) return
        viewModel.setDiscovering(true)

        discoveryJob = lifecycleScope.launch {
            nsdManagerRx
                .discoverServices(DiscoveryConfiguration("_services._dns-sd._udp"))
                .catch { _ ->
                    viewModel.setDiscovering(false)}
                .collect { event ->
                    when (event) {
                        is DiscoveryServiceFound -> {
                            val record = event.service.toDiscoveryRecord()
                            adapter.addItem(record)
                            viewModel.onDeviceDiscovered(record) // ✅ VM only handles data
                        }
                        is DiscoveryServiceLost -> {
                            adapter.removeItem(event.service.toDiscoveryRecord())
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun stopDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = null
        viewModel.setDiscovering(false)
        adapter.clear()
    }

}