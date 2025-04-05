package ir.ayantech.hamrahads.example.ui.interstitial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestInterstitialAds
import ir.ayantech.hamrahads.core.ShowInterstitialAds
import ir.ayantech.hamrahads.example.databinding.FragmentInterstitialBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

@Keep
class InterstitialFragment : Fragment() {

    private var _binding: FragmentInterstitialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInterstitialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonInterstitial.setOnClickListener {
            destroy()
            requestInterstitialAds()
        }
        return root
    }

    private fun destroy() {
        showInterstitialAds?.destroyAds()
        requestInterstitial?.cancelRequest()
    }

    private var showInterstitialAds: ShowInterstitialAds? = null
    private var requestInterstitial: RequestInterstitialAds? = null

    private fun requestInterstitialAds() {
        requestInterstitial = HamrahAds.RequestInterstitialAds()
            .setContext(requireContext())
            .initId("094afc16-54fa-4738-b893-01fdf01ff330")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("wqepgojqpofgjegqw", "onSuccess")
                    binding.textStatus.text = "onSuccess RequestInterstitialAds"
                    showInterstitialAds = HamrahAds.ShowInterstitialAds()
                        .setContext(requireActivity() as AppCompatActivity)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                Log.i("wqepgojqpofgjegqw", "onSuccess 111")
                                binding.textStatus.text = "onSuccess ShowInterstitialAds"
                            }

                            override fun onError(error: NetworkError) {
                                binding.textStatus.text =
                                    "onError ShowInterstitialAds " + error.code
                            }

                            override fun onClose() {
                                binding.textStatus.text =
                                    "onClose ShowInterstitialAds "
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "onError ShowInterstitialAds onClick"
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    binding.textStatus.text = "onError RequestInterstitialAds " + error.code
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}