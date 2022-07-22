Original App Design Project - README Template
===

# dressmart

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
![Empty User]<img src="https://user-images.githubusercontent.com/73393929/180579117-53aec52d-e527-4e7a-8eeb-08624abb67c4.gif" width="200" height="390">
![Sign Up User]<img src="https://user-images.githubusercontent.com/73393929/180579136-c0a84b79-35f1-4e44-bd26-b298a49122f9.gif" width="200" height="390">
![Posted Today]<img src="https://user-images.githubusercontent.com/73393929/180579138-97bf460a-4285-47f8-aea0-a72401d8c962.gif" width="200" height="390">
![Recommendation]<img src="https://user-images.githubusercontent.com/73393929/180579144-57249499-b03a-405f-91b9-b2114113aeb3.gif" width="200" height="390">


### Description
App that suggests an outfit for the day based on weather predictions. User can log the outfits that they wear on each day, and the app will suggest similar outfits on days when the weather is similar.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Weather, Fashion, Social
- **Mobile:** Camera, Location
- **Story:** Gives users a smarter way to plan out what to wear for the day based on weather predictions and the user's past preferences.
- **Market:** Anyone who checks the weather and picks out clothing based on that would find this application useful.
- **Habit:** The app would be useful to check every morning before getting ready. It would form a habit to plan out outfits and log "feedback" at the end of every day.
- **Scope:** Main features are for each specific user - logging and tracking their own outfits based on the weather. This could expand to create a social feed of other people's outfits for that day and being able to view other users' outfit profiles.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* Users can log in
* Users can register an account
* Users can log out
* Users can view weather predictions for the day
* Users can get a generated outfit suggestion for the day
* Users can view their past outfits on their profile


**Optional Nice-to-have Stories**

* Users can upload a "virtual closet" with each garment they have
* Users will get a push notification in the morning to remind them
  * "Good morning! Check out today's weather to get started."
* Users can share their outfits to friends
* Users can see other users' outfit posts
* Users can search for garments/outfits
* Users will not be recommended the same items back to back.
* Users will get a score based on how well the outfit matches

### 2. Screen Archetypes

* Stream
  * Users can see other users' outfit posts
* Profile
  * Users can log out
  * Users can view their past outfits on their profile
  * * Users can search for garments/outfits
* Login
  * Users can log in
* Account Creation
  * Users can register an account
* Upload Garmet
  * Users can upload a "virtual closet" with each garment they have
* Today
  * Users can view weather predictions for the day
  * Users can get a generated outfit suggestion for the day
  * Users can share their outfit to friends
  * Users will get a score based on how well the outfit matches



### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Feed
  * Profile (other users)
  * Stream
* Profile
  * Logout
* Today
  * Today(includes weather and suggestion)


**Flow Navigation** (Screen to Screen)

* Stream
  * Profile (other users)
* Profile
  * login
* Login
  * Account Creation
  * Profile
* Account Creation
  * Profile

## Wireframes
[Add picture of your hand sketched wireframes in this section]
![IMG_0063](https://user-images.githubusercontent.com/73393929/174159974-5f6fd541-ded2-4834-8348-15988b151440.JPG)

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema
[This section will be completed in Unit 9]
### Models
[Add table of models]

User Model
| property         | id             | description     |
| ---------------- |:-------------- |:--------------- |
| String           | displayName    | the user's name |
| String           | username       | user's username |
| String           | password       | user's password |
| File             | profilePicture | user's pfp      |
| List<OutfitPost> | outfits        | user's outfits. |



OutfitPost Model
| property      | id                   | description                              |
| ------------- | ------------------   |:---------------------------------------- |
| User          | author               | whoever made the post                    |
| List<User>    | likedBy              | whichever users liked this post          |
| List<Garment> | garments             | all the pieces of clothing in the outfit |
| File          | wearingOutfitPicture | picture of user wearing outfit           |
| int           | temperature          | today's avg temp                         |
| String        | conditions           | {sunny, cloudy, partly cloudy}           |


Garment
| property | id             | description                      |
|:-------- |:-------------- |:-------------------------------- |
| String   | description    | description of what the item is  |
| String   | garmentType    | {top, bottom, outer, shoes}      |
| String   | subType        | changes depending on garmentType |
| File     | garmentPicture | picture of the item              |
| User     | owner          | owner of the item                |


- if garmentType==Bottom
  subtype=={shorts, pants, skirt}
- if garmentType==Top
  subtype=={shortSleeve, longSleeve, sleeveless}
- if garmentType==Shoes
  subtype=={sneakers, boots, formal, sandals}
- if garmentType==Outer
  subType=={coat, jacket}

