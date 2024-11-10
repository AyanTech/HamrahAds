package ir.ayantech.hamrahads.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestBannerAds
import ir.ayantech.hamrahads.core.RequestInterstitialAds
import ir.ayantech.hamrahads.core.RequestNativeAds
import ir.ayantech.hamrahads.core.ShowBannerAds
import ir.ayantech.hamrahads.core.ShowInterstitialAds
import ir.ayantech.hamrahads.core.ShowNativeAds
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

class MainActivity : AppCompatActivity() {

    private lateinit var textViewStatus: TextView
    private lateinit var nativeView: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewStatus = findViewById(R.id.textViewStatus)
        nativeView = findViewById(R.id.nativeView)

        HamrahAds.Initializer()
            .setContext(applicationContext)
            .initId("7b4b488a40a0c1dfe0ff73688766d79cab88274d21b16ff3f3af7157fabc692c")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {

                    findViewById<Button>(R.id.buttonBanner).setOnClickListener {
                        destroy()
                        showBannerAds()
                    }
                    findViewById<Button>(R.id.buttonInterstitial).setOnClickListener {
                        destroy()
                        RequestInterstitialAds()
                    }
                    findViewById<Button>(R.id.buttonNative).setOnClickListener {
                        destroy()
                        RequestNativeAds()
                    }

                }

                override fun onError(error: NetworkError) {
                }
            }).build()


    }

    private fun destroy() {
        showAdsBanner?.destroyAds()
        showAdsInterstitial?.destroyAds()
        showNativeAds?.destroyAds()

        showRequestBanner?.cancelRequest()
        showRequestInterstitial?.cancelRequest()
        showRequestNativeAds?.cancelRequest()

        showRequestNativeAds?.cancelRequest()
        nativeView.visibility = View.GONE

    }

    private var showAdsBanner: ShowBannerAds? = null
    private var showRequestBanner: RequestBannerAds? = null

    private var showAdsInterstitial: ShowInterstitialAds? = null
    private var showRequestInterstitial: RequestInterstitialAds? = null

    private var showNativeAds: ShowNativeAds? = null
    private var showRequestNativeAds: RequestNativeAds? = null

    private fun showBannerAds() {
        showRequestBanner = HamrahAds.RequestBannerAds()
            .setContext(applicationContext)
            .initId("e35f5d8d-a952-4bdf-b63e-7aa8d6de9753")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    textViewStatus.text = "onSuccess RequestBannerAds"

                    showAdsBanner = HamrahAds.ShowBannerAds().setContext(this@MainActivity)
                        .setSize(HamrahAdsBannerType.BANNER_320x50)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                textViewStatus.text = "onSuccess ShowBannerAds"
                            }

                            override fun onError(error: NetworkError) {
                                textViewStatus.text = "onError ShowBannerAds " + error.code
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    textViewStatus.text = "onError RequestBannerAds " + error.code
                }
            }).build()
    }

    private fun RequestInterstitialAds() {
        showRequestInterstitial = HamrahAds.RequestInterstitialAds()
            .setContext(applicationContext)
            .initId("20521bed-d9dc-4198-bc22-b026b6e696d3")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    textViewStatus.text = "onSuccess RequestInterstitialAds"
                    showAdsInterstitial = HamrahAds.ShowInterstitialAds()
                        .setContext(this@MainActivity)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                textViewStatus.text = "onSuccess ShowInterstitialAds"
                            }

                            override fun onError(error: NetworkError) {
                                textViewStatus.text = "onError ShowInterstitialAds " + error.code
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    textViewStatus.text = "onError RequestInterstitialAds " + error.code
                }
            }).build()
    }

    private fun RequestNativeAds() {
        showRequestNativeAds = HamrahAds.RequestNativeAds()
            .setContext(applicationContext)
            .initId("00ea8b15-eb29-40f9-80ab-3bd92a631a89")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    textViewStatus.text = "onSuccess RequestNativeAds"

                    showNativeAds = HamrahAds.ShowNativeAds()
                        .setContext(this@MainActivity)
                        .setViewGroup(nativeView)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                nativeView.visibility = View.VISIBLE
                                textViewStatus.text = "onSuccess ShowNativeAds"
                            }

                            override fun onError(error: NetworkError) {
                                textViewStatus.text = "onError ShowNativeAds " + error.code
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    textViewStatus.text = "onError RequestNativeAds " + error.code
                }
            }).build()
    }
}

