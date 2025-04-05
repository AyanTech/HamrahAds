package ir.ayantech.hamrahads.listener

import android.view.ViewGroup
import android.widget.FrameLayout
import ir.ayantech.hamrahads.network.model.NetworkError

interface HamrahAdsInitListener {
    fun onSuccess() {}
    fun onError(error: NetworkError) {}
    fun onClose() {}
    fun onClick() {}
    fun onKeyboardVisibility(viewGroup: ViewGroup, isKeyboardVisible: Boolean) {}
}