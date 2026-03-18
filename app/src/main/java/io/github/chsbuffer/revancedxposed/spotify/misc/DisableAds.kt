package io.github.chsbuffer.revancedxposed.spotify.misc

import de.robv.android.xposed.XC_MethodReplacement
import io.github.chsbuffer.revancedxposed.spotify.SpotifyHook

fun SpotifyHook.DisableAds() {
    runCatching {
        ::audioAdFingerprint.hookMethod(
            XC_MethodReplacement.returnConstant(false)
        )
    }
    runCatching {
        ::bannerAdFingerprint.hookMethod(
            XC_MethodReplacement.returnConstant(false)
        )
    }
}
