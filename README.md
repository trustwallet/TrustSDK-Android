# TrustSDK-Android
[![](https://jitpack.io/v/TrustWallet/TrustSdk-android.svg)](https://jitpack.io/#TrustWallet/TrustSdk-android)

## Getting started

The TrustSDK lets you sign Ethereum transactions and messages so that you can build a native DApp without having to worry about keys or wallets. Follow these instructions to integrate TrustSDK in your native DApp.

## Demo
Get accounts|Sign transaction|Send transaction
-|-|-
![Accounts](docs/accounts.gif)|![Sign](docs/sign.gif)|![Send](docs/send.gif)

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
        <data android:scheme="app_scheme" android:host="tx_callback" />
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
val operation = TransferOperation.Builder()
    .action(ActionType.SIGN) // ActionType - Send or Sign transaction request
    .callback(Uri.parse("app_scheme://tx_callback")) // callback deep link Uri to app initialized request.
    .coin(60) // Slip44 index
    .tokenId("0x6B175474E89094C44Da98b954EedeAC495271d0F") // token (optional), following standard of unique identifier on the blockhain as smart contract address or asset ID
    .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c") // Recipient address
    .amount(BigDecimal("1")) // Transaction amount in human-readable (unit) format
    .feeLimit(21000L) // (Optional) You can set your custom fee limit in subunit format
    .feePrice(BigInteger("100000000000")) // (Optional) You can set your custom fee price in subunit format
    .nonce(2) // (Optional) You can set your custom nonce or sequence
    .meta("0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001") // (Optional) Transaction data in hex format, Memo or Destination tag
    .build()
Trust.execute(this, operation)
```

You can find more documentation in [TransferOperation](https://github.com/trustwallet/TrustSDK-Android/blob/master/trust-sdk-client/src/main/java/trust/TransferOperation.kt)

## Example

Trust SDK includes an example project with the above code. To run the example project clone the repo and build the project with Android Studio. Run the app on your emulator or device. Make sure that you have Trust Wallet installed on the device or simulator to test the full callback flow.

License

TrustSDK is available under the MIT license. See the LICENSE file for more info.
