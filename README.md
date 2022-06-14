Original App Design Project - README Template
===

# dressmart

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
App that suggests an outfit for the day based on weather predictions. User can log the outfits that they wear on each day, and the app will suggest similar outfits on days when the weather is similar. 

### App Evaluation
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
* Users can upload a "virtual closet" with each garment they have
* Users can view weather predictions for the day
* Users can get a generated outfit suggestion for the day
* Users can view their past outfits on their profile


**Optional Nice-to-have Stories**

* Users will get a push notification in the morning to remind them
    * "Good morning! Check out today's weather to get started."
* Users will get a push notification in the evening to give feedback
    * "Hey there! How did your outfit work out today?"
* Users can give feedback and set their preferences for hot/cold/etc
    * The app will adjust outfit suggestions based on this
* Users can friend other users and see friends' outfits on a feed
* Users can share their outfits to friends
* Users can search for other users and view their profiles
* Users can "like" outfit posts

### 2. Screen Archetypes

* Stream
    * Users can "like" outfit posts
* Likes
    * Users can "like" outfit posts
* Search
    * Users can search for other users and view their profiles
    * Users can friend other users and see friends' outfits on a feed
* Profile
    * Users can log out
    * Users can view their past outfits on their profile
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
* Feedback
    * Users can give feedback and set their preferences for hot/cold/etc
* Outfit Detail
    * Users can view their past outfits on their profile
* Settings

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Feed
    * Logout
    * Profile (other users)
    * Stream
    * Search
* Profile
    * Outfit Detail
    * Upload Garment
    * Likes
* Today
    * Today(includes weather and suggestion)
    * Feedback
* Settings


**Flow Navigation** (Screen to Screen)

* Stream
   * Profile (other users)
   * Search
   * Outfit Detail
* Profile
   * Outfit Detail
   * Likes
   * Upload Garment
* Likes
    * Outfit Activity
* Search
    * Profile
* Login
    * Account Creation
    * Profile
* Account Creation
    * Profile

## Wireframes
[Add picture of your hand sketched wireframes in this section]
[PROFILE.pdf](https://github.com/niyatiprabhu/Dressmart/files/8903003/PROFILE.pdf)

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
