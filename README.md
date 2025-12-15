
The project is about discovering the devices connected to the home network, using the Network Service Discovery (NSD) library.

[ TODO: More Code Optimisation and Remove HardCoding also add documentaion] 

[Note: Attached video of full app functionality below, It is an emulator recording as no mDNS available in home]

The App contains 3 screens.
1. Login Screen
       -- The b asic login screen where user can input userid and password, but this way os signin in not implemented since the app focus on Google OAuth SignIn.
       -- Only Google OAuth SignIn will work and user are recommended to perform this only.
       -- Clicking Google SignIn will search for linked Google Accounts in device and user can select the account for login. (If no account is linked one should add an account)
       -- After successful login token is saved in shared pref and on next time user can login without seeing the login screen.
       -- However token do not have an expiry set.
       -- If no network access is available user is forced logout and navigated to login screen.

2. Device List Screen
      -- User can see the devices connected to the same WiFi on cliking the Search Device Button.
      -- The Devices found are shown with the IP address and its name in a Card View.
      -- The devices found are stored in DB and retrieved easily on next app access.
      -- A Green light i shown for devices which are not online and Red light for offline devices in the Card View.
      -- User can navigate to device details screen on clicking any cards.
      -- API call for device details is done using OkHttp library. It will first fetch current IP address and then successively call another API for IP details.

4. Device Details Screen
     -- The IP address details are shown in this page.

   
![Screen_recording_20251215_102020](https://github.com/user-attachments/assets/873ecbc7-d3e1-40a2-b9da-9134def8aa0a)
