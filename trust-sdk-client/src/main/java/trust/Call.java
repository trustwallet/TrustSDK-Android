package trust;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Call implements Parcelable {
    private Request request;

    public Call(Request request) {
        this.request = request;
    }

    protected Call(Parcel in) {
        request = in.readParcelable(Request.class.getClassLoader());
    }

    @NonNull
    public Response onActivityResult(int requestCode, int resultCode, Intent data) {
        Response response = Trust.onActivityResult(requestCode, resultCode, data);
        if (response.isAvailable() && request.key().equals(response.request.key())) {
            return response;
        }
        return new Response(null, null, Trust.ErrorCode.NONE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
