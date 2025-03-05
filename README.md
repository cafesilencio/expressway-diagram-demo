Previously I had a task that required displaying a driver's position on a diagram that represented the major expressways around various citites in Japan. The diagram was a simple image file with lines representing the expressways. The diagram was not a literal representation and was not to scale. As a driver was on any of the expressways the diagram should have an icon representing the driver's postion and it should move across the diagram as the driver is moving. 

This project demonstrates a process for implementing the required functionality. The diagram used is a substitue for the original diagram and shows only a partial view of the expressways.


https://github.com/user-attachments/assets/f1793a9d-08b5-4c2f-9806-fe479328fad1

Initially there was a non-trivial amount of work to map the pixel coordinates for key points in the diagram to real world latitude/longitude coordinates. A process for better automating this could be implemented. There is an association between a range of pixel coordinates and road geometry. 

At runtime: 
The pixel/location mappings for the area around the driver are loaded.
A more dense road geometry is generated and the pixel coordinates between the start end endpoints for road sections on the diagram are interpolated.
These pixel/location mappings are loaded into a search tree.
During navigation the location updates are used to search for the nearest mapping to the driver's postion in order to derive the pixel location for the icon representing the driver on the diagram. 
The diagram is updated with the driver's position.


Omitted from the demo is a check to determine when the driver is actually on an expressway. The demo uses a static route on an expressway.

One of the biggest challenges which isn't fully demonstrated here was determing the correct bearing for the diagram icon. Since the diagram isn't a literal representation of the real world there were often instances in which a section of road would be going north/south for example and the line on the digram was horizontal. Also roads don't go in straight lines. So using the driver's literal bearing by itself was insufficient for determining the direction of the icon.


To use this project you'll need a Mapbox token. 
Go to the Mapbox website to create an account if necessay.
Open the file in /res/values/mapbox_access_token.xml and put your token in it.


