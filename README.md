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

Add deep link intent filter to your `AndroidManifest.xml`:
```xml
<activity
...
    <intent-filter android:autoVerify="true">
    ...
        <action android:name="android.intent.action.VIEW" />
        <data android:scheme="app_scheme" android:host="tx_callback" />
        <data android:scheme="app_scheme" android:host="accounts_callback" />
```

Override 'onNewIntent' if your activity is singleTask or 'onCreate' if not, and handle sdk request callback:
```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    val txCallback = Trust.handleOperationResult(intent)
    if (txCallback?.error != null) {
        resultText.text = txCallback.error?.name
    } else if (!txCallback?.data.isNullOrEmpty()) {
        onResultData(txCallback?.data)
    }
    val accounts = (operation as? AccountsOperation)?.parseResultData(txCallback?.data ?: return)
}
```

## Build a request

### Get Accounts

To get accounts use this code:

```kotlin
operation = AccountsOperation.Builder()
    .callbackScheme("app_scheme") // callback deep link scheme to app initialized request.
    .callbackHost("accounts_callback") // (Optional) callback deep link host to app initialized request. 'tx_callback' by default
    .coins(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH) // coins you want to request
    .requestId(0) // (Optional) Request id will be returned as callback param. Any incrementing integer. 1 by default
    .build()
Trust.execute(this, operation)
```

You can find more documentation in [AccountsOperation](https://github.com/trustwallet/TrustSDK-Android/blob/master/trust-sdk-client/src/main/java/trust/AccountsOperation.kt)

### Sign a transaction

To sign or send a transaction use this code:

```kotlin
val operation = TransferOperation.Builder()
    .action(ActionType.SIGN) // ActionType - Send or Sign transaction request
    .callbackScheme("app_scheme") // callback deep link scheme to app initialized request.
    .callbackHost("tx_callback") // (Optional) callback deep link host to app initialized request. 'tx_callback' by default
    .coin(60) // Slip44 index
    .tokenId("0x6B175474E89094C44Da98b954EedeAC495271d0F") // token (optional), following standard of unique identifier on the blockhain as smart contract address or asset ID
    .from("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c") // (Optional) "From" address parameter specifies a wallet which contains given account
    .to("0x1b38BC1D3a7B2a370425f70CedaCa8119ac24576") // Recipient address
    .amount(BigDecimal("1")) // Transaction amount in human-readable (unit) format
    .feeLimit(21000L) // (Optional) You can set your custom fee limit in subunit format
    .feePrice(BigInteger("100000000000")) // (Optional) You can set your custom fee price in subunit format
    .nonce(2) // (Optional) You can set your custom nonce or sequence
    .meta("0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001") // (Optional) Transaction data in hex format, Memo or Destination tag
    .requestId(0) // (Optional) Request id will be returned as callback param. Any incrementing integer. 1 by default
    .build()
Trust.execute(this, operation)
```

You can find more documentation in [TransferOperation](https://github.com/trustwallet/TrustSDK-Android/blob/master/trust-sdk-client/src/main/java/trust/TransferOperation.kt)

## Example

Trust SDK includes an example project with the above code. To run the example project clone the repo and build the project with Android Studio. Run the app on your emulator or device. Make sure that you have Trust Wallet installed on the device or simulator to test the full callback flow.

License

TrustSDK is available under the MIT license. See the LICENSE file for more info.
