package ir.ayantech.hamrahads.example.ui.native

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ir.ayantech.hamrahads.HamrahAds
import ir.ayantech.hamrahads.core.RequestNativeAds
import ir.ayantech.hamrahads.core.ShowNativeAds
import ir.ayantech.hamrahads.example.databinding.FragmentNativeBinding
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.network.model.NetworkError

@Keep
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
            .initId("d3f8b412-e4a9-4373-861f-4836dab21939")
            .initListener(object : RequestListener {
                override fun onSuccess() {
                    Log.i("wqepgojqpofgjegqw", "onSuccess")
                    binding.textStatus.text = "onSuccess RequestNativeAds"

                    showNativeAds = HamrahAds.ShowNativeAds()
                        .setContext(requireActivity() as AppCompatActivity)
                        .initId("d3f8b412-e4a9-4373-861f-4836dab21939")
                        .setViewGroup(binding.nativeView)
                        .initListener(object : ShowListener {
                            override fun onLoaded() {
                                Log.i("wqepgojqpofgjegqw", "onSuccess 111")
                                binding.nativeView.visibility = View.VISIBLE
                                binding.textStatus.text = "onSuccess ShowNativeAds"
                            }

                            override fun onError(error: NetworkError) {
                                binding.textStatus.text = "onError ShowNativeAds " + error.code + " " + error.type
                            }

                            override fun onClick() {
                                super.onClick()
                                binding.textStatus.text = "ShowNativeAds onClick"
                            }
                            override fun onDisplayed() {
                                super.onDisplayed()
                                binding.textStatus.text = "ShowNativeAds onDisplayed"
                            }
                        }).build()
                }

                override fun onError(error: NetworkError) {
                    binding.textStatus.text = "onError RequestNativeAds " + error.code + " " + error.type
                }
            }).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroy()
        _binding = null
    }
}