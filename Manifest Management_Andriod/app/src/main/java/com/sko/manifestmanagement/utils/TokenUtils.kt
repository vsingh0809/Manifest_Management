

package com.sko.manifestmanagement.utils

import android.util.Log
import com.auth0.android.jwt.JWT

object TokenUtils {

    fun extractIdFromToken(token: String, claimName: String = "Id"): Int? {
        return try {
            val jwt = JWT(token)
            jwt.getClaim(claimName).asInt()
        } catch (e: Exception) {
            Log.e("TokenError", "Failed to parse token: ${e.message}")
            null
        }
    }

}

