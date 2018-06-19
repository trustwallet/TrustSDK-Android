package trust;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.util.HashSet;
import java.util.Set;

import trust.core.entity.Message;
import trust.core.entity.Transaction;
import trust.core.entity.TypedData;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static trust.Trust.ErrorCode.CANCELED;
import static trust.Trust.ErrorCode.NONE;
import static trust.Trust.RESULT_ERROR;

public class SignRequestHelper implements Parcelable {
    private Request request;

    private static final Set<String> authorities = new HashSet<String>() {{
        add(Trust.ACTION_SIGN_TRANSACTION);
        add(Trust.ACTION_SIGN_MESSAGE);
        add(Trust.ACTION_SIGN_PERSONAL_MESSAGE);
        add(Trust.ACTION_SIGN_TYPED_MESSAGE);

    }};

    public SignRequestHelper(Intent intent, Callback callback) {
        final Uri uri = intent.getData();
        if (!isSignUri(uri)) {
            return;
        }
        final String action = uri.getAuthority();

        switch (action) {
            case Trust.ACTION_SIGN_MESSAGE: {
                request = SignMessageRequest.builder().uri(uri).get();
                Message<String> message = request.body();
                callback.signMessage(message);
            } break;
            case Trust.ACTION_SIGN_PERSONAL_MESSAGE: {
                request = SignPersonalMessageRequest.builder().uri(uri).get();
                Message<String> message = request.body();
                callback.signPersonalMessage(message);
            } break;
            case Trust.ACTION_SIGN_TYPED_MESSAGE: {
                request = SignTypedMessageRequest.builder().uri(uri).get();
                Message<TypedData[]> message = request.body();
                callback.signTypedMessage(message);
            } break;
            case Trust.ACTION_SIGN_TRANSACTION: {
                request = SignTransactionRequest.builder().uri(uri).get();
                callback.signTransaction((Transaction) request.body());
            } break;
        }
    }

    protected SignRequestHelper(Parcel in) {
        request = in.readParcelable(Request.class.getClassLoader());
    }

    public void onSignCancel(Activity activity) {
        fail(activity, CANCELED);
    }

    public void onSignError(Activity activity, int error) {
        fail(activity, error);
    }

    public void onMessageSigned(Activity activity, byte[] sign) {
        success(activity, sign);
    }

    public void onTransactionSigned(Activity activity, byte[] sign) {
        success(activity, sign);
    }

    private void success(Activity activity, byte[] sign) {
        result(activity, NONE, sign);
    }

    private void fail(Activity activity, int error) {
        result(activity, error, null);
    }

    private void result(Activity activity, int error, byte[] sign) {
        Intent intent = makeResultIntent(error, sign);
        if (request.getCallbackUri() == null) {
            int code;
            if (error != NONE) {
                code = error == CANCELED ? RESULT_CANCELED : RESULT_ERROR;
            } else {
                code = RESULT_OK;
            }
            activity.setResult(code, intent);
            activity.finish();
        } else if (Trust.canStartActivity(activity, intent)) {
            activity.startActivity(intent);
            activity.finish();
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("No application found")
                    .setMessage("No proper application to handle result")
                    .create()
                    .show();
        }
    }

    private Intent makeResultIntent(int error, byte[] sign) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String signBase64 = null;
        if (sign != null && sign.length > 0) {
            signBase64 = new String(Base64.encode(sign, Base64.DEFAULT));
        } else if (error == NONE) {
            error = Trust.ErrorCode.INVALID_REQUEST;
        }
        Uri data = request.key();
        if (request.getCallbackUri() != null) {
            Uri.Builder dataBuilder = request.getCallbackUri().buildUpon()
                    .appendQueryParameter("src", getSrcUri(request.key()));
            if (error == NONE) {
                dataBuilder.appendQueryParameter("result", signBase64);
            } else {
                dataBuilder.appendQueryParameter("error", String.valueOf(error));
            }
            data = dataBuilder.build();
        }
        intent.setData(data);
        intent.putExtra(Trust.ExtraKey.SIGN, signBase64);
        intent.putExtra(Trust.ExtraKey.ERROR, error);
        return intent;
    }

    private String getSrcUri(Uri key) {
        return new String(Base64.encode(key.toString().getBytes(), Base64.DEFAULT));
    }

    private static boolean isSignUri(Uri uri) {
        return uri != null && "trust".equals(uri.getScheme())
                && authorities.contains(uri.getAuthority());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(request, flags);
    }

    public static final Creator<SignRequestHelper> CREATOR = new Creator<SignRequestHelper>() {
        @Override
        public SignRequestHelper createFromParcel(Parcel in) {
            return new SignRequestHelper(in);
        }

        @Override
        public SignRequestHelper[] newArray(int size) {
            return new SignRequestHelper[size];
        }
    };

    public interface Callback {
        void signMessage(Message<String> message);

        void signPersonalMessage(Message<String> message);

        void signTypedMessage(Message<TypedData[]> message);

        void signTransaction(Transaction transaction);
    }
}
