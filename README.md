![alt text](https://www.clover.com/static/media/clover-logo.4204a79c.svg)

# Clover SDK for Android POS integration

## Version

Current version: 3.3.3-rc1

## Overview
This SDK allows your Android-based Point-of-Sale (POS) system to communicate with a Clover® payment device and process payments.

It includes the SDK and an example POS. To work with the project effectively, you will need:
* Android Studio 2.3.3 & above
* Android OS 4.4.4 (API 19) and above on your device

To experience transactions end-to-end from the merchant and customer perspectives, we also recommend ordering a [Clover Go DevKit](http://cloverdevkit.com/collections/devkits/products/clover-all-in-one-developer-kit).

The SDK enables your custom mobile point-of-sale (POS) to accept card present, EMV compliant payment transactions.
Clover Go supports two types of card readers: a magnetic stripe + EMV chip-and-signature card reader and an all-in-one card reader that supports Swipe, EMV Dip, and NFC Contactless payments. The SDK is designed to allow merchants to take payments on Android smartphones and tablets.

**Core features of the  SDK for Clover Go include:**
1. Card Present Transactions – Transactions in which the merchant uses the approved card reader to accept physical credit or debit cards on a connected smartphone or tablet. The Clover Go platform supports the following payment options:
   * **Magnetic Stripe Card** – A traditional payment card that has a magnetic stripe.
   * **EMV Card** – A payment card containing a computer chip that enhances data security. Clover Go's EMV compliant platform enables the customer or merchant to insert an EMV card into the card reader.
   * **NFC Contactless Payment** – A transaction in which a customer leverages an Apple Pay, Samsung Pay, or Android Pay mobile wallet by tapping their mobile device to the card reader. 

**The Clover Go SDK currently supports the following payment transactions:**
* **Sale** - A transaction used to both authorize and capture the payment amount at the same time. A Sale transaction is final and the amount cannot be adjusted.
* **Auth** - A transaction that can be tip-adjusted until it is finalized during a batch closeout. This is a standard model for a restaurant that adjusts the amount to include a tip after a card is charged.
* **Void** - A transaction that cancels or fully reverses a payment transaction.
* **Refund** - A transaction that credits funds to the account holder.
* **PreAuth** - A pre-authorization for a certain amount.
* **PreAuth Capture** - A Pre-Auth that has been finalized in order to complete a payment (i.e. a bar tab that has been closed out).
* **Partial Auth** - A partial authorization. The payment gateway may return a partial authorization if the transaction amount exceeds the customer’s credit or debit card limit.
* **Tip Adjust** - A transaction in which a merchant takes or edits a tip after the customer’s card has been processed (i.e. after the initial Auth transaction).

# Getting Started

This section will provide both high-level and detailed steps in getting started with the SDK.

## High-Level View of the Integration Process
1. Create a [sandbox developer account](https://sandbox.dev.clover.com/developer-home/create-account) to test the [sample app](https://github.com/clover/remote-pay-android-go/tree/master/remote-pay-android-example-go) included with the SDK.

    **Note**: You’ll need to request a sandbox API key and secret to process transactions with your Go Devkit. You can request these values from the DevRel team via dev@clover.com.

2. Apply the same steps you’ve learned from testing the sample app to test your own app.

   **Note**: You can use the same Sandbox API key and secret from step 1.

3. Once your app is ready to be released to production, your app will need to go through Clover Go’s QA review.
4. When your sandbox app is approved by the DevRel team, you will need to create a new prod developer account and register your application.
5. Your prod account and prod app then goes through the DevRel [App Approval process](https://docs.clover.com/docs/developer-app-approval). This is a relatively quick process where the DevRel team does the following:

   - Reviews and verifies the information submitted for your developer profile.
   - Ensures that your app’s Requested Permissions do not include Customer Read, Write permissions and Employee Write permissions, but includes everything else.
   - Ensures that your app is not published.

6. Once you have successfully completed the DevRel App Approval process, you can now request for a production API key and secret from the DevRel team to make live transactions!


## Tips on Integrating with the Sample App

### Initial Setup
1. **Create Developer Account**: [Go to the Clover sandbox developer portal](https://sandbox.dev.clover.com/developers/) and create a developer account.
![1](/images/1.png)

2. **Create a new application**: Log into your developer portal and create a new app.
![2](/images/2.png)
![2a](/images/2a.png)

### OAuth Flow
To integrate with Clover Go devices, you will need to initialize the `CloverGoDeviceConfiguration` object with the right initialization values. This includes the **access token** that you retrieve by going through the OAuth flow. Below is a guide through the OAuth flow.

The access token is generated for a specific merchant employee in order to provide user context for a given payment transaction.

1. Go to your **App**’s **Settings** on the [Sandbox Dev Dashboard](https://sandbox.dev.clover.com/developers/). Make sure to save your **App ID** and **App Secret** somewhere; you’ll need them for later.
![oauth1](/images/oauth1.png)

2. Change the App Type to be REST Clients > **Web**.
![oauth2](/images/oauth2.png)

3. Make sure that your app has *disabled* Customer Read/Write, Employees Write and enabled the rest of the Permissions in **App Settings** > **Requested Permissions**.
![oauth3](/images/oauth3.png)

   **Warning**: Failure to set these permissions accordingly will lead to an invalid access token, which will prevent the Clover Go SDK from being initialized.

4. Edit your app’s REST Configuration. The Site URL should be your app’s URL. But if you don’t have one set up yet, you can just use `https://sandbox.dev.clover.com` for now. Make sure the Default OAuth Response is **CODE**.
![oauth4](/images/oauth4.png)

   **Note**: The developer portal does not currently accept non-http(s) URL schemes. If you have a custom URL scheme for native iOS and Android applications (such as myPaymentApp://clovergoauthresponse), send an email to `dev@clover.com` with your App ID and redirect URL request.

5. Click the **Market Listing** tab and then on **Preview in App Market**.
![oauth5a](/images/oauth5a.png)

   **Preview In App Market** opens the app preview page as your test merchant:
![oauth5b](/images/oauth5b.png)

6. If the app is not installed for your test merchant, click **Connect** and then **Accept** to install the app. If the app is installed, click **Open App**. Either of these steps will open a browser tab with a URL containing a **CODE** parameter: 

   `https://sandbox.dev.clover.com/?merchant_id={MERCHANT_ID}1&employee_id={EMPLOYEE_ID}&client_id={CLIENT_ID}&code={CODE}`
![oauth6](/images/oauth6.png)

   Save the **CODE** value somewhere.

7. Pass in the **App ID**, **App Secret**, and **CODE** you saved earlier into the following URL: 

   `https://sandbox.dev.clover.com/oauth/token?client_id={APP_ID}&client_secret={APP_SECRET}&code={CODE}`

   (do not include the curly braces).

8. Visit that URL in your browser, and you should be provided with your access token 🎉. 

   **Note**: If you get an “Unknown Client ID” message, check that you don’t include any spaces in the URL and visit the URL again.

### Running the Sample App
#### Initial SDK Setup
  - Create or open your own project
  - Clone remote-pay-android-go into a separate project
  - Using Finder (Finder/Explorer) copy the following folder/module into your own project
    - roam
  - Update your settings.gradle file to include the newly added modules. It will look something like the following
```
      include ':roam', ':<your_app_module_here>'
```
  - In your project’s build.gradle file under buildscript, make the following changes
```
            buildscript {
                repositories {
                    mavenCentral()
                    jcenter()
                    google()
                }

                dependencies {
                    classpath 'com.android.tools.build:gradle:4.1.3'
                }
            }
            allprojects {
                repositories {
                    jcenter()
                    google()
                }
            }
```
  - In your app module’s build.gradle file, add the following line under dependencies
```
        api project(':roam')
        implementation ("com.firstdata.clovergo:remote-pay-android-go-connector:3.3.3-rc1@aar") {
            transitive = true
        }
```
  - In your `GoStartupActivity.java`, set your **demoAccessToken**, **goApiKey**, **goSecret**, **App ID**, and **App Secret** in the following code block for Sandbox env:
```
  goApiKey = "Get this value from your DevRel representative";
  goSecret = "Get this value from your DevRel representative";
  demoAccessToken = "Access token that you generated via the OAuth flow earlier";

  oAuthClientId = "App ID";
  oAuthClientSecret = "App Secret";
```

#### Important sample code to review:
  - Once you have your app module created, you can look at the remote-pay-android-example-pos activities, such as StartupActivity and ExamplePOSActivity to see how the Clover and/or Clover Go connectors are created and implemented.  The ExamplePOSActivity also has the listener implementations that you can reference.
    - Note: The CloverConnector is for use with the Clover Mini, Flex, and Station.  CloverGoConnector is for use with standard Android phones and tablets using Clover's Audio Jack (RP350) and Bluetooth (RP450) card readers.

  - The remote-pay-android-connector module contains the connector implementations for both Clover (CloverConnector) and Clover Go (CloverGoConnector)

### Leveraging SDK within your application
Use the following in your app
ICloverGoConnector cloverGo450Connector;
ICloverGoConnectorListener ccGoListener;
#### 1. Create and implement ICloverGoConnectorListener
```
ccGoListener = new new ICloverGoConnectorListener() {
public void...
...
}
```
*\*\*\*\* Below are useful and important functions \*\*\*\**
```
ccGoListener = new ICloverGoConnectorListener() {
  public void onDeviceDisconnected(ReaderInfo readerInfo) {}
  public void onDeviceConnected() {}
  public void onCloverGoDeviceActivity(final CloverDeviceEvent deviceEvent) {
    switch (deviceEvent.getEventState()) {
      case CARD_SWIPED:
      break;
      case CARD_TAPPED:
      break;
      case CANCEL_CARD_READ:
      break;
      case EMV_COMPLETE_DATA:
      break;
      case CARD_INSERTED_MSG:
      break;
      case CARD_REMOVED_MSG:
      break;
      case PLEASE_SEE_PHONE_MSG:
      break;
      case READER_READY:
      break;
    }
  }

  public void onDeviceDiscovered(ReaderInfo readerInfo) {}
  public void onAidMatch(final List<CardApplicationIdentifier>applicationIdentifiers, final AidSelection aidSelection) {}
  public void onDeviceReady(final MerchantInfo merchantInfo) {}
  public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {
    switch (deviceErrorEvent.getErrorType()) {
      case READER_ERROR:
      case CARD_ERROR:
      case READER_TIMEOUT:
      case COMMUNICATION_ERROR:
      case LOW_BATTERY:
      case PARTIAL_AUTH_REJECTED:
      case DUPLICATE_TRANSACTION_REJECTED:
      // notify user
      break;
      case MULTIPLE_CONTACT_LESS_CARD_DETECTED_ERROR:
      case CONTACT_LESS_FAILED_TRY_CONTACT_ERROR:
      case EMV_CARD_SWIPED_ERROR:
      case DIP_FAILED_ALL_ATTEMPTS_ERROR:
      case DIP_FAILED_ERROR:
      case SWIPE_FAILED_ERROR:
      // show progress to user
      break;
    }
  }
  public void onAuthResponse(final AuthResponse response) {}
  public void onPreAuthResponse(final PreAuthResponse response) {}
  public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {}
  public void onCapturePreAuthResponse(CapturePreAuthResponse response) {}
  public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {}
  public void onSaleResponse(final SaleResponse response) {}
  public void onRefundPaymentResponse(final RefundPaymentResponse response) {}
  public void onTipAdded(TipAddedMessage message) {}
  public void onVoidPaymentResponse(VoidPaymentResponse response) {}
```

#### 2. Initialize SDK with 450 (Bluetooth) Reader
Parameters (Required) to initialize SDK:
1.  access Token
2.  environment
3.  api key
4.  secret
5.  app ID
```
CloverGoDeviceConfiguration config = new CloverGoDeviceConfiguration.Builder(getApplicationContext(), accessToken, goEnv, apiKey, secret, appId).deviceType(ReaderInfo.ReaderType.RP450). allowAutoConnect(false).build();
ICloverGoConnector cloverGo450Connector = (CloverGoConnector)
ConnectorFactory.createCloverConnector(config);
cloverGo450Connector.addCloverGoConnectorListener(ccGoListener);
cloverGo450Connector.initializeConnection();
```
#### 3. Sale transaction
```
SaleRequest request = new
SaleRequest(store.getCurrentOrder().getTotal(), externalPaymentID);
request.setCardEntryMethods(store.getCardEntryMethods());
request.setAllowOfflinePayment(store.getAllowOfflinePayment());
request.setForceOfflinePayment(store.getForceOfflinePayment());
request.setApproveOfflinePaymentWithoutPrompt(store.getApproveOfflinePaymentWithoutPrompt());
request.setTippableAmount(store.getCurrentOrder().getTippableAmount());
request.setTaxAmount(store.getCurrentOrder().getTaxAmount());
request.setDisablePrinting(store.getDisablePrinting());
request.setTipMode(store.getTipMode());
request.setSignatureEntryLocation(store.getSignatureEntryLocation());
request.setSignatureThreshold(store.getSignatureThreshold());
request.setDisableReceiptSelection(store.getDisableReceiptOptions());
request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
request.setTipAmount(store.getTipAmount());
request.setAutoAcceptPaymentConfirmations(store.getAutomaticPaymentConfirmation());
request.setAutoAcceptSignature(store.getAutomaticSignatureConfirmation());
cloverGo450Connector.sale(request);
```
Required parameters for sale transaction:
1.  amount – which will be total amount you want to make a transaction
2.  externalPaymentID – random unique number for this transaction
and other Optional parameters

#### 4.  Auth transaction
```
AuthRequest request = new AuthRequest(store.getCurrentOrder().getTotal(), externalPaymentID);
request.setCardEntryMethods(store.getCardEntryMethods());
request.setAllowOfflinePayment(store.getAllowOfflinePayment());
request.setForceOfflinePayment(store.getForceOfflinePayment());
request.setApproveOfflinePaymentWithoutPrompt(store.getApproveOfflinePaymentWithoutPrompt());
request.setTippableAmount(store.getCurrentOrder().getTippableAmount());
request.setTaxAmount(store.getCurrentOrder().getTaxAmount());
request.setDisablePrinting(store.getDisablePrinting());
request.setSignatureEntryLocation(store.getSignatureEntryLocation());
request.setSignatureThreshold(store.getSignatureThreshold());
request.setDisableReceiptSelection(store.getDisableReceiptOptions());
request.setDisableDuplicateChecking(store.getDisableDuplicateChecking());
request.setAutoAcceptPaymentConfirmations(store.getAutomaticPaymentConfirmation());
request.setAutoAcceptSignature(store.getAutomaticSignatureConfirmation());
cloverConnector.auth(request);
```
Required parameters for auth transaction:
1.  amount – which will be total amount you want to make a transaction
2.  externalPaymentID – random unique number for this transaction
and other Optional parameters

### FAQ
- **How do I generate an OAuth token in prod?**

   Follow the same steps that were taken to generate the OAuth token for the sandbox environment but now use `clover.com`. More info [here](https://docs.clover.com/clover-platform/docs/using-oauth-20).
- **I want to publish my Clover Go Android/iOS app to Clover's App Market!**

   Clover Go developers cannot publish their app to Clover’s App Market because it will not work for any merchant as the app is not meant to be installed on Clover devices like Mini and Flex. The only exception is if your app’s type is strictly for the web. In all other cases, you will need to create a separate app and go through a different [App Approval process](https://docs.clover.com/clover-platform/docs/clover-app-approval-process) to get the app reviewed.
- **I’m getting an invalid credentials response.**

   Please consult with your DevRel representative to make sure that your API key and secret tokens are correct. If they are, please try uninstalling and reinstalling your app from your test merchant.
- **I have the correct API key and secret but I still can’t connect to the reader.**

   Please make sure that your Clover Go reader is on and that your Android or Apple device has bluetooth on.
- **I’ve tried everything and my app is still running into issues when attempting a transaction in Prod.**

   Please check if you are using a Production reader by ensuring that there is no "Development" text on your device. A sandbox reader will have the word "Development" on the device, while a production reader will not.
   
   If you have the correct reader, please make sure your app has disabled Customer R/W, Employees W and enabled the rest of the Permissions. Btw, if you've recently changed your app’s Permissions settings, you will need to uninstall and reinstall the app, and re-generate the access token. This is because earlier tokens you have will only work for older requested permissions.

   If our suggestions above do not work, we strongly encourage you to use your sandbox API key and secret to experiment with our sample app, to ensure that you understand how to accomplish certain implementations.
