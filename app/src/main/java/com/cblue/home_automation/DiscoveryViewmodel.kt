package com.cblue.home_automation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.net.HttpURLConnection
import java.net.URL
import kotlin.String
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DiscoveryViewModel : ViewModel() {

    private val _ipDetails = MutableLiveData<IpDetails>()
    val ipDetails: LiveData<IpDetails> = _ipDetails

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun onDeviceClicked(device: DiscoveryRecord) {
        fetchDeviceDetails(device.name)
    }

    private fun fetchDeviceDetails(deviceId: String) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {

                val ip = getPublicIp()

                // 2️⃣ Get details using IP
                val details = getIpDetails(ip)

                _ipDetails.postValue(details)

            } catch (e: Exception) {
                _error.postValue(e.message ?: "Something went wrong")
            } finally{

                _loading.postValue(false)

            }
        }
    }

    private suspend fun getPublicIp(): String = withContext(Dispatchers.IO) {
        val url = URL("https://api.ipify.org/?format=json")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Failed to fetch IP")
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)

        json.getString("ip")
    }

    private suspend fun getIpDetails(ip: String): IpDetails =
        withContext(Dispatchers.IO) {

            val url = URL("https://ipinfo.io/$ip/geo")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Failed to fetch IP details")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            IpDetails(
                city = json.optString("city"),
                region = json.optString("region"),
                country = json.optString("country"),
                loc = json.optString("loc"),
                org = json.optString("org"),
                postal = json.optString("postal"),
                timezone = json.optString("timezone"),
                readme = json.optString("readme"),
                ip = json.optString("ip"),
                hostname = json.optString("hostname")
            )
        }
}
