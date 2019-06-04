# iAsset
Android Application for asset management.

## Getting Started
- JDK 1.8.0_191 or Above
- Android Studio v3 or Above
- Gmail Account

### Prerequisites
- Android Smartphone have v7.0 (Nougat) or Above
- Active Internet Connection
- Android Studio

### Required API
- Google Geocoding API

### Setting Up
- Clone project by using below command in the terminal and make sure you have git installed on your system:
$ git clone https://github.com/MIETDevelopers/CRIE_iAsset
- Now open the project in Android Studio.
- Gradle build will not be successful because of missing google-services.json file.
  For google-services.json, you need to connect Firebase. (make sure you are using same account for firebase console and Android Studio)
  For setting up Firebase Go to:

  Tools >> Firebase >> Firestore >>  Read and Write Document with Firestore >> Connect to Firebase

  Tools >> Firebase >> Firebase Storage >> Upload and Download a File With Cloud Storage >> Connect To Firebase
 
- Now build project navigating to Build >> Rebuild Project.
  If gradle build is successful the proceed to next step. If not, then try cleaning the project by navigating to Build >> Clean Project.
  
-  Now find and open your google-services.json file and navigate to res >> values >> string.xml. Now replace the following string     variable in string.xml file using valid value from google-services.json file. You can find google-services.json file in the app directory inside your project. Refer to this screenshot here: https://prnt.sc/nxlpm3


```
    <string name="database_url">place your firebase_url value here</string>
    <string name="firebase_api">place current_key value here</string>
    <string name="firebase_application_id">place mobilesdk_app_id value here</string>
 ```
 
 - Now obtain Geocoding API by visiting https://console.cloud.google.com/google/maps-apis/overview.
   After obtaining API navigate to res >> values >> string.xml and replace dummy value of google-maps-api with valid API.
   
 ```
     <string name="google_maps_api">your geocoding api here</string>
```

- Now visit https://console.firebase.google.com. Login using the same account which you have used earlier and Select CRIE_iAsset project.

- Now inside Firebase console. Under Develop options click on "Storage" and then click on "Get Started" button and then "Got It". This will create a Firebase Storage Bucket for storing bills and qr codes.

- Now inside Firebase console. Under Develop options click on "Database" and then click on "Create database" (make sure it is        Firestore). In the next screen select "Start in test mode" and then "Enable". Now your Firestore Database will be ready.

- Now inside Firebase console. Under Develop options click on "Authentication" and then click on "Set up sign-in method". In the next screen click and enable following sign-in options: (make sure to select all three otherwise sign in functionality will not work correctly)

      Email/Password
      Phone
      Google

### Installing
- Build Apk by navigating to Build >> Build Bundle(s) / Apk(s) >> Build APK(s) or
- Install apk via USB cable by clicking on Run button >> selecting a target device

### Optional
- Currently we are using pre-trained model by Google for object detection. But if you want to use your own model or any custom model. Then you need to put your protbuf (.pb) file and labels file in assets folder. 
