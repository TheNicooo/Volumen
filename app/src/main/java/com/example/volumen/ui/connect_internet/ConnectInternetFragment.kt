package com.example.volumen.ui.connect_internet

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.volumen.R
import com.example.volumen.databinding.FragmentConectionWifiBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.net.*
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ConnectInternetFragment : Fragment(R.layout.fragment_conection_wifi) {

    private var _binding: FragmentConectionWifiBinding? = null
    private val binding get() = _binding!!

    private var wifiName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConectionWifiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        setUpView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        if (isConnectedWifi(requireContext())) {
            findNavController().navigate(R.id.action_connectInternet_to_portico)
            //getList()
            //other()
        } else {
            Toast.makeText(activity, "Connection OFF", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun isConnectedWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager!!.connectionInfo
        wifiName = wifiInfo.ssid
        saveWifiName()
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    private fun saveWifiName() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("WIFI_NAME", wifiName)
            apply()
        }
    }

    private fun setUpView() {
        binding.apply {
            btnConnect.setOnClickListener(View.OnClickListener {
                startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            })
        }
    }

    fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
        }
        return null
    }

    @Throws(SocketException::class)
    fun getBroadcast(): String? {
        System.setProperty("java.net.preferIPv4Stack", "true")
        val niEnum: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (niEnum.hasMoreElements()) {
            val ni: NetworkInterface = niEnum.nextElement()
            if (!ni.isLoopback) {
                for (interfaceAddress in ni.interfaceAddresses) {
                    if (interfaceAddress.broadcast != null) {
                        return interfaceAddress.broadcast.toString()
                    }
                }
            }
        }
        return null
    }

    fun arp() {
        try {
            val br = BufferedReader(FileReader(File("/proc/net/arp")))
            var total = ""
            var line: String
            while (br.readLine().also { line = it } != null) {
                total += """$line """.trimIndent()
            }
            println(total)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * FUNCIONA EN ANDROID 10 u.u
     * */
    private fun getList() {
        val runtime = Runtime.getRuntime()
        val proc = runtime.exec("ip neigh show")
        proc.waitFor()
        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        var list: ArrayList<String> = ArrayList()
        reader.forEachLine {
            list.add(it)
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun other() {
        val macAdress = "5caafd1b0019"
        val dataUrl = "http://api.macvendors.com/$macAdress"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(dataUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doInput = true
            connection.doOutput = true
            val wr = DataOutputStream(connection.outputStream)
            wr.flush()
            wr.close()
            val `is`: InputStream = connection.inputStream
            val rd = BufferedReader(InputStreamReader(`is`))
            val response = StringBuffer()
            var line: String?
            while (rd.readLine().also { line = it } != null) {
                response.append(line)
                response.append('\r')
            }
            rd.close()
            val responseStr = response.toString()
            Log.d("Server response", responseStr)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
    }

    fun getMacAddressForIp(ipAddress: String?): String? {
        try {
            BufferedReader(FileReader("/proc/net/arp")).use { br ->
                var line: String
                while (br.readLine().also { line = it } != null) {
                    if (line.contains(ipAddress!!)) {
                        val macStartIndex = line.indexOf(":") - 2
                        val macEndPos = macStartIndex + 17
                        if (macStartIndex >= 0 && macEndPos < line.length) {
                            return line.substring(macStartIndex, macEndPos)
                        } else {
                            Log.w("MyClass", "Found ip address line, but mac address was invalid.")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MyClass", "Exception reading the arp table.", e)
        }
        return null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}