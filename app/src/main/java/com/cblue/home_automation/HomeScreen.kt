package com.cblue.home_automation

import android.R.id.toggle
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cblue.android_nsd.flow.library.nsd.rx.NsdManagerFlow
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryConfiguration
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryEvent
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryServiceFound
import com.cblue.android_nsd.flow.library.nsd.rx.discovery.DiscoveryServiceLost
import kotlin.jvm.java
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeScreen : AppCompatActivity() {

    private val toggleButton: Button by bind(R.id.toggle)
    private val statusTextView: TextView by bind(R.id.status)
    private val recyclerView: RecyclerView by bind(R.id.recycler_view)

    private val nsdManagerRx: NsdManagerFlow by lazy { NsdManagerFlow(this) }
    private val adapter: DiscoveryAdapter by lazy { DiscoveryAdapter() }
    private var jobState = MutableStateFlow<Job?>(null)
    private val viewModel: DiscoveryViewModel by viewModels()

    private fun <T : View> Activity.bind(@IdRes id: Int) = lazy { findViewById<T>(id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

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
            val intent = Intent(this, DeviceDetailsActivity::class.java)
            intent.putExtra("DETAILS", details)
            startActivity(intent)
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        toggleButton.setOnClickListener { toggle() }
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDiscovery()
    }

    private fun updateUI() {
        statusTextView.setText(
            if (jobState.value == null) R.string.activity_main_status_discovery_off
            else R.string.activity_main_status_discovery_on
        )
    }

    private fun toggle() {
        if (jobState.value == null) startDiscovery()
        else stopDiscovery()
    }

    private fun startDiscovery() {
        jobState.value = MainScope().launch {
            nsdManagerRx.discoverServices(DiscoveryConfiguration("_services._dns-sd._udp"))
                .catch { e -> Log.e(TAG, "Error starting discovery.", e) }
                .collect(::addServiceFromEvent)
        }
        updateUI()
    }

    private fun addServiceFromEvent(event: DiscoveryEvent) {
        Log.d(TAG, "Event $event")
        when (event) {
            is DiscoveryServiceFound -> adapter.addItem(event.service.toDiscoveryRecord())
            is DiscoveryServiceLost -> adapter.removeItem(event.service.toDiscoveryRecord())
            else -> {}
        }
    }

    private fun stopDiscovery() {
        jobState.value?.cancel()
        jobState.value = null
        updateUI()
        adapter.clear()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}