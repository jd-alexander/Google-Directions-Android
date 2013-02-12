Google-Directions-Android
=========================

This project allows you to  calculate the direction between two locations and display the route on a map based on the 
classes created by Hesham Saeed in this [post](http://stackoverflow.com/questions/11745314/why-retrieving-google-directions-for-android-using-kml-data-is-not-working-anymo/11745316#11745316).

Description
-----------

When building location based android applications it’s always an added plus to include some form of routing so that the user can know how to navigate from place to place. In android, the SDK does not support routing out of the box. There are several approaches that can be taken to achieve this.  You can pass the start and destination points to a Google Maps intent or web view mechanisms. These approaches work well however they are a bit flawed, The user should not have to navigate from the app and the developer has no control once the user navigates to another app therefore  this implementation is best. This project utilizes a parser that translate the json received from the directions web service to bunch of geopoints that can be applied to the map control as a route overlay.

<b>Copyright (C) by Joel Dean</b>

Sample
------

A map with route overlay.

![Example Image][1]


Installation
------------

The sample project requires:

* The library project
* Google Maps API and all other Android dependencies installed on your development machine.

Usage
-----

Google Directions Android(the library folder) is presented as an [Android library project](http://developer.android.com/guide/developing/projects/projects-eclipse.html).
You can include this project by [referencing it as a library project](http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject) in Eclipse or ant.

To calculate the route and display it on the map you will need to run an async task that is present in the library.

*You can execute the task with these parameters.

``` java
new Routing(/*MapView*/,/*Color of line*/).execute(/*GeoPoint(start)*/,/*GeoPoint(destination)*/);
```

actual code 
``` java
new Routing(mapView,Color.GREEN).execute(new GeoPoint((int)(18.015365*1E6),(int)(-77.499382*1E6)), new GeoPoint((int)(18.012590*1E6),(int)(-77.500659*1E6)));
```

*Use these parameters if you want a progress dialog to be displayed while the task is running. 

``` java
new Routing(/*Context*/,/*MapView*/,/*Color of line*/).execute(/*GeoPoint(start)*/,/*GeoPoint(destination)*/);
```

actual code 
``` java
new Routing(this,mapView,Color.GREEN).execute(new GeoPoint((int)(18.015365*1E6),(int)(-77.499382*1E6)), new GeoPoint((int)(18.012590*1E6),(int)(-77.500659*1E6)));
```

Known Issues
------------
*If after importing the project(s) you get an error stating that no resource was found that matches a given name,
just clean the project.

*Ensure that the Google APIs version is included in your SDK and that all references and values are in your manifest.

*The color is of type int, its not an actual color. Just type Color. and wait for the intellisense the to suggest the colors.

Contribution
------------

Please fork  repository and contribute back using pull requests.

Any contributions, large or small, major features, bug fixes, additional language translations, unit/integration tests are welcomed and appreciated but will be thoroughly reviewed and discussed.

Developed By
------------
* Joel Dean 

For Support
------------

email : jdeanjj1000@gmail.com
website : www.joeldeandev.com



[1]:http://i47.tinypic.com/2l9krys.jpg
