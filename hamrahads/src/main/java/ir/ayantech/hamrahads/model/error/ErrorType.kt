package ir.ayantech.hamrahads.model.error

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorType {
    Remote, Local
}
