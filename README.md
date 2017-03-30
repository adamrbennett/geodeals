# GeoDeals
> GeoDeals is a sample application to demonstrate geofencing in Android. When the user enters the geofence, the app will create a notification inviting them to engage.

## Prerequisites
1. Android Studio
1. Genymotion Android Emulator
1. Genymotion virtual device with Google Play Services installed (Google Maps app not required, but recommended)
1. Ruby with nokogiri gem (for the gpx_trip.rb script, but could also be ported to something like Node.js)

## Getting Started
Once the prerequisites above have been installed, follow the instructions below to execute the demonstration:
1. Run the app on your virtual device using AndroidStudio. A blank activity will appear.
1. Open Google Maps on your virtual device, and keep GeoDeals in the background.
1. Run the `gpx_trip.rb` Ruby script to simulate a trip from the Denver Zoo to the Denver Museum of Nature and Science. You should see your location moving in Google Maps. E.g.: `ruby gpx_trip.rb gpx_trip.gpx`
1. As you leave the Denver Zoo, you will exit the geofence and the GeoDeals app will show you a notification.
1. Once you near the Denver Museum of Nature and Science, you will enter the geofence, and the GeoDeals app will show you a notification.

## Notes
- The `gpx_trip.rb` Ruby script uses an XML file of trip waypoints (`gpx_trip.gpx`) and the Genymotion Shell (`genyshell`) to simulate movement in the virtual device. To run the script, make sure your Ruby environment is setup, and make sure the `genyshell` executable is on your system PATH.
- If you prefer not to use Ruby, the script could also be ported to another scripting language, i.e.: Node.js JavaScript, bash, etc.
- The trip waypoint gpx file can be generated from a Google Maps trip like so:
    1. Open [Google Maps](http://maps.google.com) in your browser.
    1. Get directions between at least two locations.
    1. Copy the Google Maps URL
    1. Use the [GPS Visualizer](http://www.gpsvisualizer.com/convert_input?convert_format=gpx) tool and paste the Google Maps URL into the URL field, then click the Covert button
    1. Save the GPX file and pass it to the `gpx_trip.rb` Ruby script