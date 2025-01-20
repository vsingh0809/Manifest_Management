

package com.sko.manifestmanagement.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false  // Disable manifest parsing for Glide (optional, based on your needs)
    }
}
