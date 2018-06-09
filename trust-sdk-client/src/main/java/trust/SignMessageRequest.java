package trust;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import trust.core.entity.Message;

public final class SignMessageRequest implements Request, Parcelable {

    private final Message message;
    private final Uri uri;
    private final Uri callbackUri;

    private SignMessageRequest(Message message, Uri callbackUri) {
        this.message = message;
        this.callbackUri = callbackUri;
        uri = toUri(message, callbackUri);
    }

    private SignMessageRequest(Parcel in) {
        message = in.readParcelable(Message.class.getClassLoader());
        callbackUri = in.readParcelable(Uri.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    private static Uri toUri(Message message, Uri callbackUri) {
        byte[] value = Base64.encode(message.value.getBytes(), Base64.DEFAULT);
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("trust")
                .authority(message.isPersonal ? Trust.ACTION_SIGN_PERSONAL_MESSAGE : Trust.ACTION_SIGN_MESSAGE)
                .appendQueryParameter(Trust.ExtraKey.MESSAGE, new String(value))
                .appendQueryParameter(Trust.ExtraKey.LEAF_POSITION, String.valueOf(message.leafPosition));
        if (callbackUri != null) {
            uriBuilder.appendQueryParameter("callback", callbackUri.toString());
        }
        return uriBuilder.build();
    }

    @Override
    public <T> T body() {
        return (T) message;
    }

    @Override
    public Uri key() {
        return uri;
    }

    @Override
    public String getAction() {
        return message.isPersonal ? Trust.ACTION_SIGN_PERSONAL_MESSAGE : Trust.ACTION_SIGN_MESSAGE;
    }

    @Nullable
    @Override
    public Uri getCallbackUri() {
        return callbackUri;
    }

    public static SignMessageRequest.Builder builder() {
        return new SignMessageRequest.Builder();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(message, flags);
        dest.writeParcelable(callbackUri, flags);
        dest.writeParcelable(uri, flags);
    }

    public static final Creator<SignMessageRequest> CREATOR = new Creator<SignMessageRequest>() {
        @Override
        public SignMessageRequest createFromParcel(Parcel in) {
            return new SignMessageRequest(in);
        }

        @Override
        public SignMessageRequest[] newArray(int size) {
            return new SignMessageRequest[size];
        }
    };

    public static class Builder {
        private String message;
        private boolean isPersonal;
        private long leafPosition;
        private String callbackUri;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder isPersonal(boolean isPersonal) {
            this.isPersonal = isPersonal;
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
            String value = uri.getQueryParameter(Trust.ExtraKey.MESSAGE);
            message = new String(Base64.decode(value, Base64.DEFAULT));
            isPersonal = "sign-personal-message".equals(uri.getAuthority());
            callbackUri = uri.getQueryParameter("callback");
            try {
                leafPosition = Long.valueOf(uri.getQueryParameter(Trust.ExtraKey.LEAF_POSITION));
            } catch (NumberFormatException ex) { /* Quietly */ }
            return this;
        }

        public Builder message(Message message) {
            message(message.value).isPersonal(message.isPersonal).leafPosition(message.leafPosition);
            return this;
        }

        public SignMessageRequest get() {
            Uri callbackUri = null;
            if (!TextUtils.isEmpty(this.callbackUri)) {
                try {
                    callbackUri = Uri.parse(this.callbackUri);
                } catch (Exception ex) { /* Quietly */ }
            }
            Message message = new Message(this.message, isPersonal, leafPosition);
            return new SignMessageRequest(message, callbackUri);
        }

        public Call<SignMessageRequest> call(Activity activity) {
            return Trust.execute(activity, get());
        }
    }
}
