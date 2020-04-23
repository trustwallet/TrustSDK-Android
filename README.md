# TrustSDK-Android
[![](https://jitpack.io/v/TrustWallet/TrustSdk-android.svg)](https://jitpack.io/#TrustWallet/TrustSdk-android)

## Getting started

The TrustSDK lets you sign Ethereum transactions and messages so that you can bulid a native DApp without having to worry about keys or wallets. Follow these instructions to integrate TrustSDK in your native DApp.

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

In your signing activity `Trust`.

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

### Sign a transaction

To get accounts use this code:

```kotlin
getAccountsCall = Trust.execute(this, AccountsRequest(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH))
```

## Example

Trust SDK includes an example project with the above code. To run the example project clone the repo and build the project with Android Studio. Run the app on your emulator or device. Make sure that you have Trust Wallet installed on the device or simulator to test the full callback flow.

License

TrustSDK is available under the MIT license. See the LICENSE file for more info.
