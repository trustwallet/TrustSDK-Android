package trust;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import trust.core.entity.Message;

public final class SignMessageRequest implements Request, Parcelable {

    private final Message message;
    private final Uri uri;

    private SignMessageRequest(Message message) {
        this.message = message;
        uri = Trust.toUri(message);
    }

    private SignMessageRequest(Parcel in) {
        message = in.readParcelable(Message.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
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
        return Trust.ACTION_SIGN_MESSAGE;
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

        public Builder uri(Uri uri) {
            message = uri.getQueryParameter(Trust.ExtraKey.VALUE);
            isPersonal = "true".equals(uri.getQueryParameter(Trust.ExtraKey.IS_PERSONAL));
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
            Message message = new Message(this.message, isPersonal, leafPosition);
            return new SignMessageRequest(message);
        }

        public Call<SignMessageRequest> call(Activity activity) {
            return Trust.execute(activity, get());
        }
    }
}
