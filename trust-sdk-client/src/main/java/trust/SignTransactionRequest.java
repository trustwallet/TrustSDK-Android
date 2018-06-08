package trust;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigInteger;

import trust.core.entity.Address;
import trust.core.entity.Transaction;
import trust.core.util.Hex;

public class SignTransactionRequest implements Request, Parcelable {
    private final Transaction transaction;
    private final Uri uri;

    private SignTransactionRequest(Transaction transaction) {
        this.transaction = transaction;
        this.uri = Trust.toUri(transaction);
    }

    private SignTransactionRequest(Parcel in) {
        transaction = in.readParcelable(Transaction.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public <T> T body() {
        return (T) transaction;
    }

    @Override
    public Uri key() {
        return uri;
    }

    @Override
    public String getAction() {
        return Trust.ACTION_SIGN_TRANSACTION;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(transaction, flags);
        dest.writeParcelable(uri, flags);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final Creator<SignTransactionRequest> CREATOR = new Creator<SignTransactionRequest>() {
        @Override
        public SignTransactionRequest createFromParcel(Parcel in) {
            return new SignTransactionRequest(in);
        }

        @Override
        public SignTransactionRequest[] newArray(int size) {
            return new SignTransactionRequest[size];
        }
    };

    public static class Builder {

        private Address recipient;
        private BigInteger value = BigInteger.ZERO;
        private BigInteger gasPrice = BigInteger.ZERO;
        private long gasLimit;
        private String payload;
        private Address contract;
        private long nonce;

        public Builder recipient(Address recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder value(BigInteger value) {
            if (value == null) {
                value = BigInteger.ZERO;
            }
            this.value = value;
            return this;
        }

        public Builder gasPrice(BigInteger gasPrice) {
            if (gasPrice == null) {
                gasPrice = BigInteger.ZERO;
            }
            this.gasPrice = gasPrice;
            return this;
        }

        public Builder gasLimit(long gasLimit) {
            this.gasLimit = gasLimit;
            return this;
        }

        public Builder payload(byte[] payload) {
            this.payload = Hex.byteArrayToHexString(payload);
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder contractAddress(Address contract) {
            this.contract = contract;
            return this;
        }

        public Builder nonce(long nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder transaction(Transaction transaction) {
            recipient(transaction.recipient)
                    .contractAddress(transaction.contract)
                    .value(transaction.value)
                    .gasLimit(transaction.gasLimit)
                    .gasPrice(transaction.gasPrice)
                    .payload(transaction.payload)
                    .nonce(transaction.nonce);
            return this;
        }

        public Builder uri(Uri uri) {
            String recipient = uri.getQueryParameter(Trust.ExtraKey.RECIPIENT);
            String value = uri.getQueryParameter(Trust.ExtraKey.VALUE);
            String contract = uri.getQueryParameter(Trust.ExtraKey.CONTRACT);
            String gasPrice = uri.getQueryParameter(Trust.ExtraKey.GAS_PRICE);
            String gasLimit = uri.getQueryParameter(Trust.ExtraKey.GAS_LIMIT);
            String nonce = uri.getQueryParameter(Trust.ExtraKey.NONCE);
            recipient(TextUtils.isEmpty(recipient) ? null : new Address(recipient));
            try {
                value(TextUtils.isEmpty(value) ? BigInteger.ZERO : new BigInteger(value));
            } catch (Exception ex) { /* Quietly */ }
            try {
                gasPrice(TextUtils.isEmpty(gasPrice) ? BigInteger.ZERO : new BigInteger(gasPrice));
            } catch (Exception ex) { /* Quietly */ }
            try {
                gasLimit(Long.valueOf(gasLimit));
            } catch (Exception ex) { /* Quietly */ }
            payload(payload);
            contractAddress(TextUtils.isEmpty(contract) ? null : new Address(contract));
            try {
                nonce(Long.valueOf(nonce));
            } catch (Exception ex) { /* Quietly */ }
            return this;
        }

        public SignTransactionRequest get() {
            Transaction transaction = new Transaction(recipient, contract, value, gasPrice, gasLimit, nonce, payload);
            return new SignTransactionRequest(transaction);
        }

        @Nullable
        public Call<SignTransactionRequest> call(Activity activity) {
            return Trust.execute(activity, get());
        }
    }
}
