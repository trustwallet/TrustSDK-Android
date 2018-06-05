package trust;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class Response {

    @Nullable
    final Request request;
    @Nullable
    final String signHex;
    final int error;

    public Response(@Nullable Request request, @Nullable String signHex, int error) {
        this.request = request;
        this.signHex = signHex;
        this.error = error;
    }

    public Response subscribe(@NonNull OnSuccessListener onSuccessListener) {
        return subscribe(onSuccessListener, null);
    }

    public Response subscribe(@NonNull OnSuccessListener onSuccessListener, @Nullable OnFailureListener onFailureListener) {
        if (isAvailable()) {
            if (error > Trust.ErrorCode.NONE || TextUtils.isEmpty(signHex)) {
                if (onFailureListener != null) {
                    onFailureListener.onFail(request, error);
                }
            } else {
                onSuccessListener.onSuccess(request, signHex);
            }
        }
        return this;
    }

    public boolean isAvailable() {
        return request != null && (!TextUtils.isEmpty(signHex) || error > Trust.ErrorCode.NONE);
    }
}
