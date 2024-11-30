package ir.ayantech.hamrahads.example.ui.native

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestNativeAds
import ir.ayantech.hamrahads.core.ShowNativeAds
import ir.ayantech.hamrahads.example.databinding.FragmentNativeBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

class NativeFragment : Fragment() {

    private var _binding: FragmentNativeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNativeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonBanner.setOnClickListener {
            destroy()
            requestNativeAds()
        }
        return root
    }

    private fun destroy() {
        showNativeAds?.destroyAds()
        requestNativeAds?.cancelRequest()
    }

    private var showNativeAds: ShowNativeAds? = null
    private var requestNativeAds: RequestNativeAds? = null

    private fun requestNativeAds() {
        requestNativeAds = HamrahAds.RequestNativeAds()
            .setContext(requireActivity())
            .initId("00ea8b15-eb29-40f9-80ab-3bd92a631a89")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    binding.textStatus.text = "onSuccess RequestNativeAds"

                    showNativeAds = HamrahAds.ShowNativeAds()
                        .setContext(requireActivity())
                        .setViewGroup(binding.nativeView)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                binding.nativeView.visibility = View.VISIBLE
                                binding.textStatus.text = "onSuccess ShowNativeAds"
                            }

                            override fun onError(error: NetworkError) {
                                binding.textStatus.text = "onError ShowNativeAds " + error.code
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "onError ShowNativeAds onClick"
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    binding.textStatus.text = "onError RequestNativeAds " + error.code
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}