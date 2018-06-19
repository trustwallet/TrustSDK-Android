package trust;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;

import trust.core.util.Hex;

public class Call<T extends Request> implements Parcelable {
    private final T request;

    Call(T request) {
        this.request = request;
    }

    private Call(Parcel in) {
        Class<?> type = (Class) in.readSerializable();
        request = in.readParcelable(type.getClassLoader());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, OnCompleteListener<T> onCompleteListener) {
        if (requestCode != Trust.REQUEST_CODE_SIGN || !request.key().equals(data.getData())) {
            return;
        }
        String signHex = null;
        int error;
        if (resultCode == Activity.RESULT_CANCELED) {
            error = Trust.ErrorCode.CANCELED;
        } else if (resultCode == Trust.RESULT_ERROR) {
            error = data.getIntExtra(Trust.ExtraKey.ERROR, Trust.ErrorCode.UNKNOWN);
        } else {
            String base64 = data.getStringExtra(Trust.ExtraKey.SIGN);
            signHex = Hex.byteArrayToHexString(Base64.decode(base64, Base64.DEFAULT));
            error = data.getIntExtra(Trust.ExtraKey.ERROR, Trust.ErrorCode.NONE);
            if (error == Trust.ErrorCode.NONE && TextUtils.isEmpty(signHex)) {
                error = Trust.ErrorCode.INVALID_REQUEST;
            }
        }
        Response<T> response = new Response<>(request, signHex, error);
        onCompleteListener.onComplete(response);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(request.getClass());
        dest.writeParcelable(request, flags);
    }

    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };
}
