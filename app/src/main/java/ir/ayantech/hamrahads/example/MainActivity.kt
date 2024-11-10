package ir.ayantech.hamrahads.example

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.example.databinding.ActivityMainBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        HamrahAds.Initializer()
            .setContext(applicationContext)
            .initId("7b4b488a40a0c1dfe0ff73688766d79cab88274d21b16ff3f3af7157fabc692c")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {

                }

                override fun onError(error: NetworkError) {
                }
            }).build()
    }

}

