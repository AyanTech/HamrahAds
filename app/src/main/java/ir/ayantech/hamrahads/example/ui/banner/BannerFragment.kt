package ir.ayantech.hamrahads.example.ui.banner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.ads.banner.BannerAdLoader
import ir.ayantech.hamrahads.ads.banner.BannerAdView
import ir.ayantech.hamrahads.example.databinding.FragmentBannerBinding
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.model.enums.BannerSize
import ir.ayantech.hamrahads.model.error.HamrahAdsError

@Keep
class BannerFragment : Fragment() {

    private var _binding: FragmentBannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonBanner.setOnClickListener {
            destroy()
            showBannerAds()
        }
        return root
    }

    private fun destroy() {
        bannerAdView?.destroyAds()
        bannerAdLoader?.cancelRequest()
    }

    private var bannerAdView: BannerAdView? = null
    private var bannerAdLoader: BannerAdLoader? = null

    private fun showBannerAds() {
        bannerAdLoader = HamrahAds.RequestBannerAds()
            .setContext(requireContext())
            .initId("e17beb85-1b7e-40f1-a312-ab947840590e")
            .initListener(object : RequestListener {
                override fun onSuccess() {
                    Log.i("wqepgojqpofgjegqw", "onSuccess")
                    binding.textStatus.text = "onSuccess RequestBannerAds"

                    bannerAdView = HamrahAds.ShowBannerAds()
                        .setContext(requireActivity() as AppCompatActivity)
                        .setSize(BannerSize.BANNER_320x50)
                        .initId("e17beb85-1b7e-40f1-a312-ab947840590e")
                        .setViewGroup(binding.banner)
                        .initListener(object : ShowListener {
                            override fun onLoaded() {
                                binding.textStatus.text = "onSuccess ShowBannerAds"
                            }

                            override fun onError(error: HamrahAdsError) {
                                binding.textStatus.text =
                                    "onError ShowBannerAds " + error.code + " " + error.type
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "ShowBannerAds onClick"
                            }

                            override fun onDisplayed() {
                                super.onDisplayed()
                                binding.textStatus.text = "ShowBannerAds onDisplayed"
                            }
                        }).build()
                }

                override fun onError(error: HamrahAdsError) {
                    binding.textStatus.text = "onError RequestBannerAds " + error.code + " " + error.type
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}
