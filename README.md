# TrustSDK-Android
[![](https://jitpack.io/v/TrustWallet/TrustSdk-android.svg)](https://jitpack.io/#TrustWallet/TrustSdk-android)

## Getting started

The TrustSDK lets you sign Ethereum transactions and messages so that you can build a native DApp without having to worry about keys or wallets. Follow these instructions to integrate TrustSDK in your native DApp.

## Demo

## Add dependency

1. Add jitpack to your root gradle file at the end of repositories:
```groovy
allprojects {
    repositories {
	...
        maven { url 'https://jitpack.io'}
    }
}
```

2. Add dependency to your module:
```groovy
dependencies {
    implementation 'com.github.TrustWallet:TrustSDK-Android:$version'
}
```

## Handle Trust callbacks

### Deprecated method used now only for *get accounts* request:

Override `onActivityResult` to obtain the signing result. Handle the response data and pass onSuccessListener and onFailureListener.

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        getAccountsCall?.let {
                    it.onActivityResult(requestCode, resultCode, data, OnCompleteListener<Array<Account>> { response ->
                        val result = response.result?.map { account ->  "${account.address.data} ${account.coin.name}" }?.joinToString("\n")
                        resultText.text = result
                        Log.d("GET_ACCOUNTS", result ?: "")
                    })
                }
    }
```

### For all other requests

Add deep link intent filter to your `AndroidManifest.xml`:
```xml
<activity
...
    <intent-filter android:autoVerify="true">
    ...
        <action android:name="android.intent.action.VIEW" />
        <data android:scheme="trust_sdk" android:host="tx_callback" />
```

Override 'onNewIntent' if your activity is singleTask or 'onCreate' if not, and handle sdk request callback:
```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    val txCallback = Trust.handleTransactionResult(intent)
    txCallback?.signature   // Signed transaction in hex format if you requested sign
    txCallback?.hash        // Hash id of transferred transaction
    txCallback?.isCancelled // User has cancelled your transaction request
}
```

## Build a request

### Get Accounts

To get accounts use this code:

```kotlin
getAccountsCall = Trust.execute(this, AccountsRequest(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH))
```

### Sign a transaction

To sign or send a transaction use this code:

```kotlin
val operation = TransferOperation.Builder().apply {
      // Required params
      action = ActionType.SIGN // ActionType.SEND
      callback = Uri.parse("app_scheme://tx_callback")
      assetId = "c60_t0x6B175474E89094C44Da98b954EedeAC495271d0F"
      to = "0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c"
      amount = BigDecimal("1")
      // Optional params
      feeLimit = 21000L
      feePrice = BigInteger("100000000000")
      nonce = 2
      meta = "0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001"
}.build()
Trust.execute(this, operation)
```

You can find more documentation in [TransferOperation](https://github.com/trustwallet/TrustSDK-Android/blob/master/trust-sdk-client/src/main/java/trust/TransferOperation.kt)

## Example

Trust SDK includes an example project with the above code. To run the example project clone the repo and build the project with Android Studio. Run the app on your emulator or device. Make sure that you have Trust Wallet installed on the device or simulator to test the full callback flow.

License

TrustSDK is available under the MIT license. See the LICENSE file for more info.
