package ir.ayantech.hamrahads.model.error

/** Stable SDK error catalog. Legacy numeric IDs remain supported by HamrahAdsError.getError. */
internal enum class AdError(val legacyId: Int, val code: String, val description: String) {
    INVALID_REQUEST(0, "G00010", "The information entered is not complete"),
    EMPTY_RESPONSE(1, "G00011", "Response body is null"),
    EMPTY_ERROR_RESPONSE(2, "G00012", "Error body is null"),
    INVALID_ERROR_RESPONSE(3, "G00013", "Failed to deserialize error response"),
    REQUEST_FAILED(4, "G00014", "Network request failed"),
    IMAGE_FAILED(5, "G00015", "The ad image has not been downloaded"),
    AD_UNAVAILABLE(6, "G00017", "There is no advertising information"),
    WEB_DISPLAY_FAILED(7, "G00018", "The web display encountered a problem"),
    MISSING_APP_KEY(8, "G00019", "AppKey is empty");

    fun toError(type: ErrorType = ErrorType.Local) = HamrahAdsError(code, description, type)
}
