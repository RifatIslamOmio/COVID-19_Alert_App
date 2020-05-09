# Location Aware COVID-19_Alert_App

## Features Overview:
1)	Sign-up verification by phone number. User registration contains name, age, home address, work address (optional).
2)	User locations are tracked by explicit user choice and stored in local storage only. During tracking user is 
3)	User uploads locally stored tracked locations and home address after testing positive for COVID-19. These locations are stored in cloud storage. Note that, no other user information except the locations are accessible by other users. Nevertheless, in case of false information administrative measures can be taken to track down the culprit (not implemented yet).
4)	The App queries the database storage of infected locations by locally stored locations and notifies the user immediately if a match is found. This background process runs once every hour.
5)	Users can view and post queries or relief requests through a global news feed, similar to other social media platforms.
6)	Relief request posts are anonymous and provide only a post author contact number, which was used for verification. Other users willing to help can contact him through the app using the phone number.

<br>

The strongest and the weakest point of this App is that it is <b>completely user based</b>. Without a positive tested user uploading their locations or, users allowing the app to track user location or, users  getting tested after being notified of probable close contact with covid positive person or, users helping relief seekers from the app’s newsfeed, functionality of this app would render useless. We have deliberately kept it this way as most other proposed apps focus on major authoritative control over monitoring users, having little or no regard for privacy.

<br>

## App Specialties:

### (1)	User Privacy Protection

Location tracking is enabled by the user and is informed to the user via a fixed notification. Before user tests positive for COVID-19 and uploads all his/her locations, the locations are stored in the device’s local storage, none but the user has access to it. Once user tests positive for COVID-19 and uploads his/her locations, the identity of the user is preserved and not accessible to any other user. However, administrative access is enabled for tracking down false claims (not implemented yet) for taking legal actions.

<br>

&nbsp;&nbsp;&nbsp;&nbsp;![User Location Permission](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/permission.jpg) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Permission Explanation](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/permission-explanation.png)

<br>
<br>

### (2)	Efficient Access to potential Huge Server Data Storage

Tracked location data of COVID-19 positive patients will evidently get very large, as the number of affected people is rising each day. Moreover, in many areas people are still reluctant or don’t have the luxury to maintain social distancing. To somewhat make the query process of a possible huge data storage a hashing algorithm is implemented. A particular tracked location is converted into its corresponding square block/s of area 20 meters x 20 meters along with and hourly time frame. 

The block generation is similar to hashing function by providing a key that is the particular index for a query, with the additional benefit that the block also defines a radius of presence for any particular location. A block is defined by its bottom left and top right diagonal coordinates.

<br>

&nbsp;&nbsp;&nbsp;&nbsp;![Locations of close contact with covid-19 positive person/s](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/infected-locations.png) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Infected person recorded location block](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/map-boxed-area.png)

<br>
<br>

### (3)	Anonymous Relief Posts:

Through the app’s global news feed, relief requests can be posted without directly sharing personal or family information of a user. A contact button is attached to relief posts through which any other user can call and contact the relief request post’s author and reach out for help. This feature especially targets the middle-class families that are suffering greatly in silence and cannot seek help publicly. A user is allowed to make only one relief post every seven days, this is a measure taken to stop misuse of the feature. 

<br>

![relif request post](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/relief-post-typing.png) &nbsp;&nbsp;&nbsp; ![anonymous on newfeed](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/relief-post-newsfees.png) &nbsp;&nbsp;&nbsp;![relief request denied within 3 days](https://github.com/ferdouszislam/COVID-19_Alert_App/blob/master/screenshots/relief-post-3-days-message.png)

<br>

#### For the full project report please [visit this link](google.com)
