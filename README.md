## Technologies
<ul> 
  <li>Java openjdk 11.10.10</li>
  <li>Android Studio</li>
  <li>Google Maps API</li>
  <li>Open Weather API</li>
  <li>Multi-threaded Application</li>
</ul>

## Description
This is a very simple and modest application to demonstrate my ability to make multiple calls to sepearte APIs. The application prompts the users 
to enter a destination in the search bar and hit enter. Upon hitting enter, a request is made to the open weather api and to the google maps api. The open weather 
app recieves the temperature, longitude, and latitude coordinates of a given location and google maps api uses the coordinates to center the map around the 
specified location.

<b>(Notes)</b> you must type the city you want to find in a city, country format. (spelling matters, capitilzation does not) The following is a list of examples

```
Boston, us
new york, us
london,france
beirut,lebanon
```
<img src="/imgs/Boston_app.jpg" height="500rem">
<img src="/imgs/GazaStrip_app.jpg" height="500rem">

## How to reproduce code

```
git clone https://github.com/PaulCardoos/GoogleMapsWeatherAPI-Android.git
```
After you clone the open simply open the application in Android Studio and run it on an emulator or your device. You can connect a phone to your computer, enable devoloper mode and execute the code via Android Studio.

This application was physically tested on a Samsung Galaxy S9+. 

 
  
