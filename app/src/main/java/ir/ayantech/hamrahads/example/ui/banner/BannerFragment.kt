package ir.ayantech.hamrahads.example.ui.banner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestBannerAds
import ir.ayantech.hamrahads.core.ShowBannerAds
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.example.databinding.FragmentBannerBinding
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError

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
        showBannerAds?.destroyAds()
        requestBanner?.cancelRequest()
    }

    private var showBannerAds: ShowBannerAds? = null
    private var requestBanner: RequestBannerAds? = null

    private fun showBannerAds() {
        requestBanner = HamrahAds.RequestBannerAds()
            .setContext(requireContext())
            .initId("e35f5d8d-a952-4bdf-b63e-7aa8d6de9753")
            .initListener(object : HamrahAdsInitListener {
                override fun onSuccess() {
                    Log.i("wqepgojqpofgjegqw", "onSuccess")
                    binding.textStatus.text = "onSuccess RequestBannerAds"

                    showBannerAds = HamrahAds.ShowBannerAds()
                        .setContext(requireActivity())
                        .setSize(HamrahAdsBannerType.BANNER_1136x640)
                        .setViewGroup(binding.banner)
                        .initListener(object : HamrahAdsInitListener {
                            override fun onSuccess() {
                                Log.i("wqepgojqpofgjegqw", "onSuccess 111")
                                binding.textStatus.text = "onSuccess ShowBannerAds"
                            }

                            override fun onError(error: NetworkError) {
                                binding.textStatus.text = "onError ShowBannerAds " + error.code
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "onError ShowBannerAds onClick"
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    binding.textStatus.text = "onError RequestBannerAds " + error.code
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}