package trust

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.IllegalArgumentException

@Parcelize
class Call<R, T : Request<R>> internal constructor(private val request: T) : Parcelable {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, onCompleteListener: OnCompleteListener<R>) {
        if (requestCode != request.getRequestCode()) {
            return
        }
        val error = when (resultCode) {
            Activity.RESULT_CANCELED -> Error.CANCELED
            Trust.RESULT_ERROR -> try {
                Error.valueOf(data?.getStringExtra(Trust.ExtraKey.ERROR.key) ?: "")
            } catch (ex: IllegalArgumentException) {
                Error.UNKNOWN
            }
            else -> Error.NONE
        }
        val response = request.buildResponse(data, error)
        onCompleteListener.onComplete(response)
    }
}