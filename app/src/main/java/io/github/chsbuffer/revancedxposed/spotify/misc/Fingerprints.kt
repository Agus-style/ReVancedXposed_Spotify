package io.github.chsbuffer.revancedxposed.spotify.misc

import io.github.chsbuffer.revancedxposed.AccessFlags
import io.github.chsbuffer.revancedxposed.Opcode
import io.github.chsbuffer.revancedxposed.SkipTest
import io.github.chsbuffer.revancedxposed.findClassDirect
import io.github.chsbuffer.revancedxposed.findFieldDirect
import io.github.chsbuffer.revancedxposed.findMethodDirect
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.strings
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.query.enums.UsingType

// === EXISTING FINGERPRINTS ===

val productStateProtoFingerprint = fingerprint {
    returns("Ljava/util/Map;")
    classMatcher { descriptor = "Lcom/spotify/remoteconfig/internal/ProductStateProto;" }
}

val attributesMapField =
    findFieldDirect { productStateProtoFingerprint().usingFields.single().field }

val buildQueryParametersFingerprint = findMethodDirect {
    findMethod {
        matcher {
            strings("trackRows", "device_type:tablet")
        }
    }.single()
}

val contextFromJsonFingerprint = fingerprint {
    opcodes(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC
    )
    methodMatcher {
        name("fromJson")
        declaredClass(
            "voiceassistants.playermodels.ContextJsonAdapter", StringMatchType.EndsWith
        )
    }
}

val contextMenuViewModelClass = findClassDirect {
    return@findClassDirect runCatching {
        fingerprint {
            strings("ContextMenuViewModel(header=")
        }
    }.getOrElse {
        fingerprint {
            accessFlags(AccessFlags.CONSTRUCTOR)
            strings("ContextMenuViewModel cannot contain items with duplicate itemResId. id=")
            parameters("L", "Ljava/util/List;", "Z")
        }
    }.declaredClass!!
}

val viewModelClazz = findClassDirect {
    findMethod {
        findFirst = true
        matcher { name("getViewModel") }
    }.single().returnType!!
}

val isPremiumUpsellField = findFieldDirect {
    viewModelClazz().fields.filter { it.typeName == "boolean" }[1]
}

@SkipTest
fun structureGetSectionsFingerprint(className: String) = fingerprint {
    classMatcher { className(className, StringMatchType.EndsWith) }
    methodMatcher {
        addUsingField {
            usingType = UsingType.Read
            name = "sections_"
        }
    }
}

val homeStructureGetSectionsFingerprint =
    structureGetSectionsFingerprint("homeapi.proto.HomeStructure")
val browseStructureGetSectionsFingerprint =
    structureGetSectionsFingerprint("browsita.v1.resolved.BrowseStructure")

val pendragonJsonFetchMessageRequestFingerprint = findMethodDirect {
    findMethod {
        matcher {
            name("apply")
            addInvoke {
                name("<init>")
                declaredClass("FetchMessageRequest", StringMatchType.EndsWith)
            }
        }
    }.single()
}

val pendragonJsonFetchMessageListRequestFingerprint = findMethodDirect {
    findMethod {
        matcher {
            name("apply")
            addInvoke {
                name("<init>")
                declaredClass("FetchMessageListRequest", StringMatchType.EndsWith)
            }
        }
    }.single()
}

// === NEW FINGERPRINTS ===

// Force update
val forceUpdateDialogFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("force_update", "update_required")
        }
    }.getOrElse {
        fingerprint {
            strings("forceUpdate", "mandatory_update")
        }
    }
}

val forceUpdateVersionFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("min_supported_version", "minimum_version")
        }
    }.getOrElse {
        fingerprint {
            strings("minVersion", "min_version")
        }
    }
}

// Ads
val audioAdFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("audio_ad", "audioAd", "interstitial")
        }
    }.getOrElse {
        fingerprint {
            strings("ad_type", "adType")
        }
    }
}

val bannerAdFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("banner_ad", "bannerAd")
        }
    }.getOrElse {
        fingerprint {
            strings("ad_banner", "adBanner")
        }
    }
}

// Skips
val skipLimitFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("skip_limit", "skipLimit", "skips_remaining")
        }
    }.getOrElse {
        fingerprint {
            strings("remaining_skips", "maxSkips")
        }
    }
}

// Shuffle
val shuffleFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("shufflingContext", "forced_shuffle")
        }
    }.getOrElse {
        fingerprint {
            strings("shuffle_forced", "isShuffle")
        }
    }
}

// On demand
val onDemandFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("on_demand", "onDemand")
        }
    }.getOrElse {
        fingerprint {
            strings("is_on_demand", "playOnDemand")
        }
    }
}

// Audio quality
val audioQualityFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("audio_quality", "audioQuality", "VERY_HIGH")
        }
    }.getOrElse {
        fingerprint {
            strings("quality_level", "HIGH_QUALITY")
        }
    }
}

// Upsell UI
val premiumUpsellUIFingerprint = findMethodDirect {
    runCatching {
        fingerprint {
            strings("premium_upsell", "upsell_trigger")
        }
    }.getOrElse {
        fingerprint {
            strings("show_upsell", "upsellView")
        }
    }
}
