package trust

import android.net.Uri

interface Operation {
    fun buildUri(): Uri
}