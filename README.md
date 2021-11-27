# DESCRIPTION

App Name - **Video Meet** 

A video and voice calling Native application with conference support.
Using **JITSI** android SDK.

# Features

1. Data hosted on Firebase Firestore
2. User Authentication
3. Select Multiple users for conference
4. Get list of all users

# GETTING STARTED

1. Clone the Project
2. Change Package Name
2. Connect The app to Firebase
3. Add google-services.json file


# WHAT DID I LEARNED ?

1. Firebase Firestore
2. Shared Preferences
3. Broadcast Recievers
4. Firebase Cloud Messaging
5. Using JITSI SDK
6. Retrofit POST
7. SwipeRefresh Layout


# SCREENSHOT

 | SIGN IN | USERS | SIGN UP | MEET ROOM | INCOMING INVITATION | INVITING | 
 --------------|------------|-------------|-----------|-----------|-----------|
 | ![](Images/img1.jpg) | ![](Images/img2.jpg)  | ![](Images/img3.jpg) | ![](Images/img4.jpg) | ![](Images/img5.jpg)  | ![](Images/img6.jpg)


# DEPENDENCIES

    `// MATERIAL DESIGN
    implementation 'com.google.android.material:material:1.2.0'

    // FIREBASE MESSAGING
    implementation 'com.google.firebase:firebase-messaging:20.2.4'

    // FIREBASE FIRE STORE
    implementation 'com.google.firebase:firebase-firestore:21.5.0'

    // SCALABLE SCREEN SIZE UNIT
    implementation 'com.intuit.sdp:sdp-android:1.0.6'
    implementation 'com.intuit.ssp:ssp-android:1.0.6'

    // RECYCLER VIEW
    implementation 'androidx.recyclerview:recyclerview:1.1.0'

    // MULTIDEX
    implementation 'com.android.support:multidex:1.0.3'

    // RETROFIT
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'

    // SWIPE REFRESH LAYOUT
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'

    // JITSI MEET
    implementation ('org.jitsi.react:jitsi-meet-sdk:2.8.2') { transitive = true }`
