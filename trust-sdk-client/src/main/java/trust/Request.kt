package trust

import android.content.Intent
import android.net.Uri
import android.os.Parcelable

interface Request<R> : Parcelable {
    fun data(): Uri?
    fun getRequestCode(): Int
    fun buildResponse(intent: Intent?, error: Error): Response<R>
}