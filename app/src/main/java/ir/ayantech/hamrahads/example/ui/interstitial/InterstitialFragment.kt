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
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.listener.ShowListener
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
            .initListener(object : RequestListener {
                override fun onSuccess() {
                    Log.i("wqepgojqpofgjegqw", "onSuccess")
                    binding.textStatus.text = "onSuccess RequestInterstitialAds"
                    showInterstitialAds = HamrahAds.ShowInterstitialAds()
                        .initId("094afc16-54fa-4738-b893-01fdf01ff330")
                        .setContext(requireActivity() as AppCompatActivity)
                        .initListener(object : ShowListener {
                            override fun onLoaded() {
                                Log.i("wqepgojqpofgjegqw", "onSuccess 111")
                                binding.textStatus.text = "onSuccess ShowInterstitialAds"
                            }

                            override fun onError(error: NetworkError) {
                                binding.textStatus.text =
                                    "onError ShowInterstitialAds " + error.code + " " + error.type
                            }

                            override fun onClose() {
                                binding.textStatus.text =
                                    "onClose ShowInterstitialAds "
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "ShowInterstitialAds onClick"
                            }
                            override fun onDisplayed() {
                                super.onDisplayed()
                                binding.textStatus.text = "ShowInterstitialAds onDisplayed"
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    binding.textStatus.text = "onError RequestInterstitialAds " + error.code + " " + error.type
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}