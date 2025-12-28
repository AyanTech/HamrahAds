package ir.ayantech.hamrahads.model.enums

enum class LandingType(val value: Int) {
    URL(1),
    CAFE_BAZAAR(2),
    PLAY_STORE(3),
    TEXT_MESSAGE(4),
    CALL(5),
    STORE_INTENT(6),
    CAFE_BAZAAR_MODAL(7);

    companion object {
        fun fromValue(value: Int): LandingType? {
            return values().find { it.value == value }
        }
    }
}
