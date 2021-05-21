Name: Tommi Häkkinen

Topic: Android application that displays Finland's covid statistics nationally, areally and locally using THL's api.
       The app's main view displays Finland's current number of registered covid cases, covid related deaths and percentage 
       of population that has received their first covid vaccine. User can also get the number of covid cases in their current
       city based on their device's location.
       The app features an interactable map of Finland that displays the number of covid cases and percentage of vaccinated 
       population in different health care districts.
       

Target: Android/Kotlin

Google Play link: TBA
Screencast: https://www.youtube.com/watch?v=T0SRP5PU8-o

Release 1:
- App connects to THL's covid api.
- App displays overall cases of covid in Finland.

Release 2:
- App displays number of covid related deaths in Finland.
- App displays the percentage of Finnish population who have received their covid vaccine.
- App displays covid cases in the user's current city based on their device's location.
- Interactable map view that demonstrates the different health care districts in Finland.
  User can view district specific covid case and vaccination information by pressing different areas on the map.

Known bugs:
- The district images in the map view are rectangles so the odd shaped ones' clickable areas can overlap with others, making them harder to press.
- Location does not work in Ahvenanmaa.