package ir.ayantech.hamrahads.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.ShowBannerAds
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            showAds?.destroyAds()
        }
        HamrahAds.Initializer()
            .setContext(applicationContext)
            .initId("7b4b488a40a0c1dfe0ff73688766d79cab88274d21b16ff3f3af7157fabc692c")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("uiotyrjykouyo", "onSuccess Initializer")
//                    showBannerAds()
                    RequestInterstitialAds()
                }

                override fun onError(error: NetworkError) {
                    Log.i("uiotyrjykouyo", "onError Initializer " + error.code)
                }
            }).build()
    }
    var showAds : ShowBannerAds? = null

    private fun showBannerAds() {
        val showRequest = HamrahAds.RequestBannerAds()
            .setContext(applicationContext)
            .initId("e35f5d8d-a952-4bdf-b63e-7aa8d6de9753")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("uiotyrjykouyo", "onSuccess RequestBannerAds")

                      showAds = HamrahAds.ShowBannerAds().setContext(this@MainActivity)
                        .setSize(HamrahAdsBannerType.BANNER_320x50)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                Log.i("uiotyrjykouyo", "onSuccess ShowBannerAds")
                            }

                            override fun onError(error: NetworkError) {
                                Log.i("uiotyrjykouyo", "onError ShowBannerAds " + error.code)
                            }
                        }).build()

//                    showAds?.destroyAds()
                }

                override fun onError(error: NetworkError) {
                    Log.i("uiotyrjykouyo", "onError RequestBannerAds " + error.code)
                }
            }).build()

//        request?.cancelRequest()
    }

    private fun RequestInterstitialAds() {
        HamrahAds.RequestInterstitialAds()
            .setContext(applicationContext)
            .initId("20521bed-d9dc-4198-bc22-b026b6e696d3")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("uiotyrjykouyo", "onSuccess RequestInterstitialAds")

                    HamrahAds.ShowInterstitialAds()
                        .setContext(this@MainActivity)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                Log.i("uiotyrjykouyo", "onSuccess RequestInterstitialAds")
                            }

                            override fun onError(error: NetworkError) {
                                Log.i("uiotyrjykouyo", "onError RequestInterstitialAds")
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    Log.i("uiotyrjykouyo", "onError RequestInterstitialAds")
                }
            }).build()
    }

    private fun RequestNativeAds() {
        HamrahAds.RequestNativeAds()
            .setContext(applicationContext)
            .initId("edad705c-c045-48f6-9b8c-9f5b5705f2f6")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("uiotyrjykouyo", "onSuccess RequestBannerAds")

//                    HamrahAds.ShowNativeAds()
//                        .setContext(this@MainActivity)
//                        .setViewGroup()
//                        .initListener(object : HamrahAdsInitListener {
//                            override fun onSuccess() {
//                                Log.i("uiotyrjykouyo", "onSuccess ShowBannerAds")
//                            }
//
//                            override fun onError(error: NetworkError) {
//                                Log.i("uiotyrjykouyo", error.code!!)
//                            }
//                        }).build()
                }

                override fun onError(error: NetworkError) {
                    Log.i("uiotyrjykouyo", error.code!!)
                }
            }).build()
    }
}

