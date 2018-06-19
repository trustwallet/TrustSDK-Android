package trust;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import trust.core.entity.Message;
import trust.core.entity.TypedData;

public final class SignTypedMessageRequest extends BaseSignMessageRequest<TypedData[]> implements Request, Parcelable {

    public static SignTypedMessageRequest.Builder builder() {
        return new SignTypedMessageRequest.Builder();
    }

    private SignTypedMessageRequest(Message<TypedData[]> message, Uri callbackUri) {
        super(message, callbackUri);
    }

    private SignTypedMessageRequest(Parcel in) {
        super(in);
    }

    @Override
    byte[] getData() {
        Message<TypedData[]> body = body();
        return new Gson().toJson(body.value).getBytes();
    }

    @Override
    String getAuthority() {
        return Trust.ACTION_SIGN_TYPED_MESSAGE;
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<SignTypedMessageRequest> CREATOR = new Creator<SignTypedMessageRequest>() {
        @Override
        public SignTypedMessageRequest createFromParcel(Parcel in) {
            return new SignTypedMessageRequest(in);
        }

        @Override
        public SignTypedMessageRequest[] newArray(int size) {
            return new SignTypedMessageRequest[size];
        }
    };

    public static class Builder {
        private TypedData[] message;
        private long leafPosition;
        private String callbackUri;
        private String url;

        public Builder message(TypedData... message) {
            this.message = message;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder leafPosition(long leafPosition) {
            this.leafPosition = leafPosition;
            return this;
        }

        public Builder callbackUri(String callbackUri) {
            this.callbackUri = callbackUri;
            return this;
        }

        public Builder uri(Uri uri) {
            if (!Trust.ACTION_SIGN_TYPED_MESSAGE.equals(uri.getAuthority())) {
                throw new IllegalArgumentException("Illegal message");
            }

            String value = uri.getQueryParameter(Trust.ExtraKey.MESSAGE);
            Type type = new TypeToken<TypedData[]>() {}.getType();
            String json = new String(Base64.decode(value, Base64.DEFAULT));
            Log.e("JSON", json);
            message = new Gson().fromJson(json, type);
            callbackUri = uri.getQueryParameter(Trust.ExtraKey.CALLBACK_URI);
            try {
                leafPosition = Long.valueOf(uri.getQueryParameter(Trust.ExtraKey.LEAF_POSITION));
            } catch (NumberFormatException ex) { /* Quietly */ }
            return this;
        }

        public Builder message(Message<TypedData[]> message) {
            message(message.value).leafPosition(message.leafPosition).url(message.url);
            return this;
        }

        public SignTypedMessageRequest get() {
            Uri callbackUri = null;
            if (!TextUtils.isEmpty(this.callbackUri)) {
                try {
                    callbackUri = Uri.parse(this.callbackUri);
                } catch (Exception ex) { /* Quietly */ }
            }
            Message<TypedData[]> message = new Message<>(this.message, url, leafPosition);
            return new SignTypedMessageRequest(message, callbackUri);
        }

        public Call<SignTypedMessageRequest> call(Activity activity) {
            return Trust.execute(activity, get());
        }
    }
}
