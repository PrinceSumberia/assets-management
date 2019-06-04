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


### Installing
- Clone project by using below command in the terminal and make sure you have git installed on your system:
$ git clone https://github.com/MIETDevelopers/CRIE_iAsset
- Now open the project in Android Studio.
- Gradle build will not be successful because of missing google-services.json file.
  For google-services.json, you need to connect Firebase. (make sure you are using same account for firebase console and Android Studio)
  For setting up Firebase Go to:

  Tools >> Firebase >> Firestore >>  Read and Write Document with Firestore >> Connect to Firebase

  Tools >> Firebase >> Firebase Storage >> Upload and Download a File With Cloud Storage >> Connect To Firebase
 
 - Now build project Build >> Rebuild Project.
  If gradle build is successful the proceed to next step. If not, then try cleaning the project by navigating to Build >> Clean Project.
