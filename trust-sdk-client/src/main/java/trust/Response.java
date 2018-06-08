package trust;

import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class Response<T extends Request> {

    @Nullable
    public final T request;
    @Nullable
    public final String result;
    public final int error;

    public Response(@Nullable T request, @Nullable String result, int error) {
        this.request = request;
        this.result = result;
        this.error = error;
    }

    public boolean isSuccess() {
        return !TextUtils.isEmpty(result) && error == Trust.ErrorCode.NONE;
    }

    public boolean isAvailable() {
        return request != null && (!TextUtils.isEmpty(result) || error > Trust.ErrorCode.NONE);
    }
}
