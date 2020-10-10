package trust

import android.net.Uri

interface Operation {
    fun buildUri(): Uri

    fun buildAssetId(coin: Int, tokenId: String? = null): String = if (tokenId.isNullOrEmpty()){
        "c$coin"
    } else {
        "c$coin" + "_t$tokenId"
    }
}