package com.sko.manifestmanagement.model

enum class Salutation(val value: Int) {
    Mr(0),
    Mrs(1),
    Dr(2),
    Prof(3);

    companion object {
        fun fromValue(value: Int): Salutation? {
            return values().find { it.value == value }
        }
    }
}
