#iAsset
Android Application to manage assets

## Getting Started
- JDK 1.8.0_191 or Above
- Android Studio v3 or Above
- Gmail Account

### Prerequisites
- Android Smartphone have v7.0 (Nougat) or Above
- Active Internet Connection
- Android Studio

### Required API
- Google Maps API

### Installing
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
  
-  Now find and open your google-services.json file and navigate to res >> values >> string.xml. Now replace the following string variable in string.xml file using valid value from google-services.json file.

    <string name="database_url">*place your firebase_url value here*</string>
    <string name="firebase_api">*place current_key value here*</string>
    <string name="firebase_application_id">*place mobilesdk_app_id value here*</string>
    
