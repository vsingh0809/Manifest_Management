package com.sko.manifestmanagement.CrewServices

enum class GenderDTO(val value: String) {
    Male("Male"),
    Female("Female"),
    Other("Other");

    companion object {
        fun fromInt(value: Int): GenderDTO {
            return when (value) {
                0 -> Male
                1 -> Female
                2 -> Other
                else -> throw IllegalArgumentException("Invalid Gender value")
            }
        }
    }
}
