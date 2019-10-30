![alt text](https://www.clover.com/assets/images/public-site/press/clover_primary_gray_rgb.png)

# Clover SDK for Android POS integration

## Version

Current version: 3.3.1.3

## Overview
This SDK allows your Android-based Point-of-Sale (POS) system to communicate with a Clover® payment device and process payments.

It includes the SDK and an example POS. To work with the project effectively, you will need:
* Android Studio 2.3.3 & above
* Android OS 4.2 (API 17) and above on your device

To experience transactions end-to-end from the merchant and customer perspectives, we also recommend ordering a [Clover Go DevKit](http://cloverdevkit.com/collections/devkits/products/clover-all-in-one-developer-kit)

The SDK enables your custom mobile point-of-sale (POS) to accept card present, EMV compliant payment transactions.
Clover Go supports two types of card readersa magnetic stripe, EMV chip-and-signature card reader and an all-in-one card reader that supports Swipe, EMV Dip, and NFC Contactless payments. The SDK is designed to allow merchants to take payments on iOS smartphones and tablets.

**Core features of the  SDK for Clover Go include:**
1. Card Present Transactions – Transactions in which the merchant uses the approved card reader to accept physical credit or debit cards on a connected smartphone or tablet. The Clover Go platform supports the following payment options:
   * **Magnetic Stripe Card** – A traditional payment card that has a magnetic stripe.
   * **EMV Card** – A payment card containing a computer chip that enhances data security. Clover Go's EMV compliant platform enables the customer or merchant to insert an EMV card into the card reader.
   * **NFC Contactless Payment** – A transaction in which a customer leverages an Apple Pay, Samsung Pay, or Android Pay mobile wallets by tapping their mobile device to the card readerNFC Contactless Payment – A transaction in which a customer leverages an Apple Pay, Samsung Pay, or Android Pay mobile wallets by tapping their mobile device to the card reader.

**The Clover Go SDK currently supports the following payment transactions:**
* **Sale** - A transaction used to authorize and capture the payment amount in at the same time. A Sale transaction is final and the amount cannot be adjusted.
* **Auth** - A transaction that can be tip-adjusted until it is finalized during a batch closeout. This is a standard model for a restaurant that adjusts the amount to include a tip after a card is charged.
* **Void** - A transaction that cancels or fully reverses a payment transaction.
* **Refund** - A transaction that credits funds to the account holder.
* **PreAuth** - A pre-authorization for a certain amount.
* **PreAuth Capture** - A Pre-Auth that has been finalized in order to complete a payment (i.e., a bar tab that has been closed out).
* **Partial Auth** - A partial authorization. The payment gateway may return a partial authorization if the transaction amount exceeds the customer’s credit or debit card limit.
* **Tip Adjust** - A transaction in which a merchant takes or edits a tip after the customer’s card has been processed (i.e., after the initial Auth transaction).

## Getting Started
This section will provide some quick steps to get started with the SDK. To integrate with Clover Go devices you will need initialize the CloverGoDeviceConfiguration object with the right initialization values and that includes the accesstoken that you retreive by going through the OAuth flow. You will need to follow these initial steps

### Initial Setup
**1. Create Developer Account:** Go to the Clover sandbox developer portal at https://sandbox.dev.clover.com/developers/ and create a developer account.

![developer_account](/images/developer-account.png)

**2. Create a new application:** Log into developer portal and create a new app - enter app name, unique package name, and check all the clover permissions your application will require to function properly.

![create_app](/images/app_create.png)

**3. Application Settings/Credentials:** Once your application is created you can note down the App ID and Secret which will be required in your code for OAuth flow.

![appid_secret](/images/appid_secret.png)

**4. Provide re-direct URL for your OAuth flow:** Enter the redirect URL where Clover should redirect the authorization response to in the site URL field in the Web Configuration settings. The default OAuth response should be "Code".

![app_redirect](/images/app_redirect.png)

**Note:** The developer portal does not currently accept non-http(s) URL schemes. If you have a custom URL scheme for native iOS and Android applications (such as myPaymentApp://clovergoauthresponse), send an email to Clovergo-Integrations@firstdata.com with your App ID and redirect URL request.

**5. Set app permissions:** Your application will require Clover permissions to work correctly. Set your permissions by going to Settings, then Required Permissions menu.

![app_permissions](/images/app_permissions.png)

**6. Setup your unique application id:** Provide a unique application id for your application, you can use your package name or any identifier that uniquely identifies the transactions of your application. Set this up in the Semi-integrated App section of your application settings.

![app_remoteid](/images/app_remoteid.png)

Please make sure that your application bundle id is the same as the one defined in this field.

### OAuth Flow
This section describes the OAuth flow steps to get the access token required to initialize the CloverGoDeviceConfiguration object.

![oauth_flow](/images/oauth_flow.png)

**Step 1.** Invoke the Clover Authorize URL from your pos application using the App ID of your application (Step #3 above). This action will prompt the user to log into clover merchant account, once successfuly logged in they will need to approve the app for the first inital login. Authorize URL for Sandbox Environment: https://sandbox.dev.clover.com/oauth/authorize?client_id={app_id}&response_type=code

**Step 2.** The user will be redirected to the redirect URL set in step 4 above.

**Step 3.** Parse the URI data to get the Merchant ID, Employee ID, Client ID and Code.

**Step 4.** Make a REST call that includes the Client ID (it's the app id), secret, and Code from your backend server to get the access token. https://sandbox.dev.clover.com/oauth/token?client_id={appId}&client_secret={appSecret}&code={codeUrlParam}
**Note** Please note that the sample application as part of this project provides a hosted service for Step 4. Use your own such service to execute this step.

**Step 5.** Parse the response of step 4 and retreive the access token. The access token provides the Merchant and Employee context to the SDK, all transactions processed will be under this context.


### Initial SDK Setup
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
					classpath 'com.android.tools.build:gradle:3.1.4'
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
		implementation ("com.firstdata.clovergo:remote-pay-android-go-connector:3.3.1.3@aar") {
			transitive = true
		}
```

### Important sample code to review:

  - Once you have your app module created, you can look at the remote-pay-android-example-pos activities, such as StartupActivity and ExamplePOSActivity to see how the Clover and/or Clover Go connectors are created and implemented.  The ExamplePOSActivity also has the listener implementations that you can reference.
    - Note: The CloverConnector is for use with Clover Mini POS stations.  CloverGoConnector is for use with standard Android phones and tablets using Clover's Audio Jack (RP350) and Bluetooth (RP450) card readers.

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
