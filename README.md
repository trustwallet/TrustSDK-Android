## Getting Started
The TrustSDK for Android lets you sign Ethereum transactions and messages so that you can bulid a native DApp
 without having to worry about keys or wallets. Follow these instructions to integrate TrustSDK in your native DApp.
 
## Trust SDK

### Add Dependency
First, add a dependency for `Jitpack`
    
    allprojects {
        repositories {
            maven { url "https://jitpack.io" }
        }
    }
    
Then, in your submodule's `build.gradle` add a dependency to this repo:

    dependencies {
        implementation 'com.github.trustwallet:trustsdk-android:{latest version}'
    }

### Signing
Signing a message or a transaction is done with the `Trust`'s static methods. As the process of launching 
the wallet app is handled internally, the `Trust` methods also require an activity that's used to start the wallet app.
 
#### Sign a transaction

    val transaction = Transaction(address, amount, BigInteger.valueOf(21), BigInteger.valueOf(21000))
    Trust.signTransaction(transaction, myActivity)
    
#### Sign a message

    Trust.signMessage(message, null, myActivity)
    
### Handle Trust callbacks
Both `signMessage` and `signTransaction` launch the wallet app internally and to do so they require an 
activity. This is the last parameter in the previously mentioned methods.

In return, the wallet app promises to finish with a result that is delivered in your `Activity`'s `onActivityResult`.
Let `TrustSDK` parse the result:

    onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Trust.onActivityResult(requestCode, resultCode, data, this)
    }
    
Here, `this` is again your activity and it has to implement the `Trust`'s callback interface `SigningResponseHandler`.
    
## Wallet SDK
The second part of the SDK is the Wallet SDK that has to be implemented by the apps that want to sign a message or transaction.
There are three requirements:
### Activity intent filter
The Wallet has to declare that it can handle requests to sign by specifying activity `intent-filter`:
    
    <intent-filter>
        <data android:scheme="trust"/>
        <action android:name="com.trustwalletapp.action.SIGN_TRANSACTION" />
        <action android:name="com.trustwalletapp.action.SIGN_MESSAGE" />
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
### SDK
The Wallet has to use the provided `TrustWalletSDK`. The `TrustWalletSDK` handles incoming requests and makes the decicson what kind of 
signing should the wallet make.
 
### Delegate
To hook into the `TrustWalletSDK` and provide the appropriate signing, the Wallet app has to implement the `WalletDelegate` interface.
It is used by `TrustWalletSDK` to invoke the appropriate `signMessage` or `signTransaction` method as well as the callback for the 
Wallet app that let the app know the proccess has finished and it can return control to the calling client app.
 
## Example
There are a couple of sample apps that demonstrate the usage of the SDK.
* `Sample Client` demonstrates how to use the `TrustSDK` to send a message or transaction for signing to an Wallet app
* `Sample Wallet` demonstrates how to implement the lifecycle and callback methods on the Wallet side and return the signed data

Both are Android app modules and can be started as standalone apps. They both use the 'TrustSDK' submodule.

## License
