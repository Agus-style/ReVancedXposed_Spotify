package io.github.chsbuffer.revancedxposed.spotify.misc

import de.robv.android.xposed.XC_MethodReplacement
import io.github.chsbuffer.revancedxposed.spotify.SpotifyHook

fun SpotifyHook.DisableForceUpdate() {
    runCatching {
        ::forceUpdateDialogFingerprint.hookMethod(
            XC_MethodReplacement.returnConstant(null)
        )
    }
    runCatching {
        ::forceUpdateVersionFingerprint.hookMethod(
            XC_MethodReplacement.returnConstant(false)
        )
    }
}
