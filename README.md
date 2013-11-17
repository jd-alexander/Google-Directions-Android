Google-Directions-Android
=========================

This project allows you to  calculate the direction between two locations and display the route on a map based on the 
classes created by Hesham Saeed in this [post](http://stackoverflow.com/questions/11745314/why-retrieving-google-directions-for-android-using-kml-data-is-not-working-anymo/11745316#11745316).

Sample
------

A map with route overlay.

![Example Image][1]


Installation
------------

The sample project requires:

* The library project
* Google Play services SDK (bundled with project)
* android-support-v4-jar (bundled with project)
* Android dependencies installed on your development machine.

Usage
-----

Google Directions Android(the library folder) is presented as an [Android library project](http://developer.android.com/guide/developing/projects/projects-eclipse.html).
You can include this project by [referencing it as a library project](http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject) in Eclipse or ant.

For the library project a reference has to be done to the Google Play Services Lib project that contains the Google Maps Android API v2 dependencies.

To calculate the route and display it on the map you will need to run an async task that is present in the library.

N.B  Ensure that the google play servicers jar is attached to the Google Play Services lib project.


*You can execute the task in this manner. ( See the example for more details on the exact implementation)



``` java

        Routing routing = new Routing(/* Travel Mode */);
        routing.registerListener(/* Listener that delivers routing results.*/);
        routing.execute(/*LatLng(start)*/, /*LatLng(destination)*/);
        
```

actual code 
``` java
        start = new LatLng(18.015365, -77.499382);
        end = new LatLng(18.012590, -77.500659);
        
        Routing routing = new Routing(Routing.TravelMode.WALKING);
        routing.registerListener(this);
        routing.execute(start, end);
        
        .....
        
      @Override
      public void onRoutingSuccess(PolylineOptions mPolyOptions) 
      {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        polyoptions.addAll(mPolyOptions.getPoints());
        map.addPolyline(polyoptions);
      }
```

*The library is bundled with start and end pushpins of various colours, check the example included or just have a look in the res folder.
``
Known Issues
------------
*If after importing the project(s) you get an error stating that no resource was found that matches a given name,
just clean the project.

*Ensure that the Google Play Services SDK is attached to the libary project and that all references and values are in your manifest.

*For the example project it needs android-support-v4-jar due to use of the elements from the support library.

*If the route is not being displayed then type "Routing" in your log cat to see the potential error messages from the library.

*The color is of type int, its not an actual color. Just type Color. and wait for the intellisense the to suggest the colors.

Contribution
------------

Please fork  repository and contribute using pull requests.

Any contributions, large or small, major features, bug fixes, additional language translations, unit/integration tests are welcomed and appreciated but will be thoroughly reviewed and discussed.

Contributors
------------
[Cyrille Berliat](https://github.com/licryle)

Developed By
------------
* Joel Dean 
*   [Hesham Saeed](https://github.com/HeshamSaeed)


[1]:http://i41.tinypic.com/2dw97r7.jpg
