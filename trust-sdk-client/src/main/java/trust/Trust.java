package trust;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import trust.core.entity.Message;
import trust.core.entity.Transaction;

public abstract class Trust {

    public static final String ACTION_SIGN_TRANSACTION = "eth.sign.transaction";
    public static final String ACTION_SIGN_MESSAGE = "eth.sign.message";

    public static final int RESULT_ERROR = 1;

    public static final int REQUEST_CODE_SIGN = 8001;

    private static String packageName = "com.wallet.crypto.trustapp";

    private Trust() {}

    public static void setWalletAppPackageName(String packageName) {
        Trust.packageName = packageName;
    }

    public static Uri toUri(Message message) {
        return new Uri.Builder()
                .scheme("eth")
                .path("sign")
                .path("message")
                .appendQueryParameter(ExtraKey.VALUE, message.value)
                .appendQueryParameter(ExtraKey.IS_PERSONAL, String.valueOf(message.isPersonal))
                .appendQueryParameter(ExtraKey.LEAF_POSITION, String.valueOf(message.leafPosition))
                .build();
    }

    public static Uri toUri(Transaction transaction) {
        return new Uri.Builder()
                .scheme("eth")
                .path("sign")
                .path("transaction")
                .appendQueryParameter(ExtraKey.RECIPIENT,
                        transaction.recipient == null ? "" : transaction.recipient.toString())
                .appendQueryParameter(ExtraKey.CONTRACT,
                        transaction.contract == null ? "" : transaction.contract.toString())
                .appendQueryParameter(ExtraKey.VALUE,
                        transaction.value == null ? "0" : transaction.value.toString())
                .appendQueryParameter(ExtraKey.GAS_PRICE,
                        transaction.gasPrice == null ? "0" : transaction.gasPrice.toString())
                .appendQueryParameter(ExtraKey.GAS_LIMIT, String.valueOf(transaction.gasLimit))
                .appendQueryParameter(ExtraKey.NONCE, String.valueOf(transaction.nonce))
                .appendQueryParameter(ExtraKey.INPUT, transaction.payload)
                .appendQueryParameter(ExtraKey.LEAF_POSITION, String.valueOf(transaction.leafPosition))
                .build();
    }

    public static SignTransactionRequest.Builder signTransaction() {
        return SignTransactionRequest.builder();
    }

    public static SignMessageRequest.Builder signMessage() {
        return SignMessageRequest.builder();
    }

    public static SignMessageRequest.Builder signPersonalMessage() {
        return SignMessageRequest.builder().isPersonal(true);
    }

    @Nullable
    public static Call execute(Activity activity, Request request) {
        Intent intent = new Intent(request.getAction());
        intent.setData(request.key());
        if (canStartActivity(activity, intent)) {
            activity.startActivityForResult(intent, REQUEST_CODE_SIGN);
            return new Call(request);
        } else {
            try {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return null;
        }
    }

    private static boolean canStartActivity(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(intent, 0).size() > 0;
    }

    @NonNull
    public static Response onActivityResult(int requestCode, int resultCode, Intent data) {
        String signHex = null;
        Request request = null;
        int error = ErrorCode.NONE;

        if (requestCode == REQUEST_CODE_SIGN) {
            if (resultCode == Activity.RESULT_CANCELED) {
                error = ErrorCode.CANCELED;
            } else {
                signHex = data.getStringExtra(ExtraKey.SIGN);
                error = data.getIntExtra(ExtraKey.ERROR, ErrorCode.UNKNOWN_ERROR);
                if (error == ErrorCode.NONE && TextUtils.isEmpty(signHex)) {
                    error = ErrorCode.SIGN_NOT_AVAILABLE;
                }

                String action = data.getAction();
                if (Trust.ACTION_SIGN_MESSAGE.equals(action)) {
                    request = SignTransactionRequest.builder().uri(data.getData()).get();
                } else if (Trust.ACTION_SIGN_TRANSACTION.equals(action)) {
                    request = SignTransactionRequest.builder().uri(data.getData()).get();
                }
            }
        }
        return new Response(request, signHex, error);
    }

    interface ExtraKey {
        String SIGN = "sign";
        String ERROR = "error";
        String RECIPIENT = "recipient";
        String VALUE = "value";
        String GAS_PRICE = "gas_price";
        String GAS_LIMIT = "gas_limit";
        String CONTRACT = "contract";
        String INPUT = "input";
        String NONCE = "nonce";
        String IS_PERSONAL = "is_personal";
        String LEAF_POSITION = "leaf_position";
    }

    public interface ErrorCode {
        int NONE = -1;
        int CANCELED = 0;
        int SIGN_NOT_AVAILABLE = 1;
        int UNKNOWN_ERROR = Integer.MAX_VALUE;
    }
}
