package ir.ayantech.hamrahads.example.ui.interstitial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestInterstitialAds
import ir.ayantech.hamrahads.core.ShowInterstitialAds
import ir.ayantech.hamrahads.example.databinding.FragmentInterstitialBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

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
            .initId("20521bed-d9dc-4198-bc22-b026b6e696d3")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    binding.textStatus.text = "onSuccess RequestInterstitialAds"
                    showInterstitialAds = HamrahAds.ShowInterstitialAds()
                        .setContext(requireActivity() as AppCompatActivity)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
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