Google-Directions-Android
=========================

This project allows you to calculate the direction between two locations and display the route on a Google Map using the Google Directions API..



![Example Image][1]

Sample
------------

The sample makes use of the Google Places API for Android in order to provide a real life example of how the library can be used.[You can check it out on the store.](https://play.google.com/store/apps/details?id=com.directions.sample)

Download
--------


Grab via Maven:
```xml
<dependency>
  <groupId>com.github.jd-alexander</groupId>
  <artifactId>library</artifactId>
  <version>1.0.1</version>
</dependency>
```
or Gradle:
```groovy
    compile 'com.github.jd-alexander:library:1.0.1'
```

Usage
-----

To calculate the route you simply instantiate a Routing object and trigger the execute function with required parameters.


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


Known Issues
------------
* If the AutoComplete TextView/Map isn't probably the API key hasn't been set in the manifest.

* If the route is not being displayed then type "Routing" in your log cat to see the potential error messages from the library.


Contribution
------------

Please fork  repository and contribute using pull requests.

Any contributions, large or small, major features, bug fixes, additional language translations, unit/integration tests are welcomed and appreciated but will be thoroughly reviewed and discussed.

Contributors
------------
*   [Cyrille Berliat](https://github.com/licryle)
*   [Felipe Duarte](https://github.com/fcduarte)
*   [Kedar Sarmalkar](https://github.com/ksarmalkar)
*   [Fernando Gil](https://github.com/fgil)
*   [Furkan Tektas](https://github.com/furkantektas)
*   [João Pedro Nardari dos Santos](https://github.com/joaopedronardari)
*   [Hesham Saeed](https://github.com/HeshamSaeed)

Developed By
------------
Joel Dean 




[1]:http://i57.tinypic.com/2m7j04x.png




