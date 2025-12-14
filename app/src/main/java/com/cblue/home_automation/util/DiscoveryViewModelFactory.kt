package com.cblue.home_automation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cblue.home_automation.repository.DeviceRepository
import com.cblue.home_automation.viewmodel.DiscoveryViewModel

class DiscoveryVMFactory(
    private val repository: DeviceRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)) {
            return DiscoveryViewModel(
                repository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
