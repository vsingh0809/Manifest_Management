package com.sko.manifestmanagement.CrewServices

enum class SalutationDTO(val value: String) {
    Mr("Mr"),
    Mrs("Mrs"),
    Miss("Miss");

    companion object {
        fun fromInt(value: Int): SalutationDTO {
            return when (value) {
                0 -> Mr
                1 -> Mrs
                2 -> Miss
                else -> throw IllegalArgumentException("Invalid Salutation value")
            }
        }
    }
}
