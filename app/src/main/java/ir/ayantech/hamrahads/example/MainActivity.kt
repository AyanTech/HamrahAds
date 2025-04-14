package ir.ayantech.hamrahads.example

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.example.databinding.ActivityMainBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

@Keep
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        HamrahAds.Initializer()
            .setContext(applicationContext)
            .initId("8765224ae941c1ad721d35777f90eec4160c2193f726d566df46dbab8aa0008e")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {

                }

                override fun onError(error: NetworkError) {

                }
            }).build()

    }
}