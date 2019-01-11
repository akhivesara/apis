# Design

### EER Diagram  ![database diagram](db.png)

# Database Setup Instructions
If using MySQL Workbench import init_database.sql
TODO: Write a bash script that creates and populates the database  

#### Approach:

# REST APIS SUPPORTED:

### title/{id} 
***Rest API: To fetch title data***

---
### title/rating/{id}
***Rest API: To fetch title ratings***

---
### title/calculatedrating{id}
***Rest API: To fetch title rating, on re-calculation. Algorithm used is: average of all episode ratings for that show***

---
### title/cast/{id}
***Rest API: To fetch cast info for a title. Possible cast categories are PersonCategory***

---
### person/{id}
***Rest API: To fetch person data***

---


## All *list* rest apis accept limit and offset query params to support pagination

---

### lists/titles/calculatedratings
***Rest API: To fetch a list of ratings for all titles that includes both ratings re-calculated and old ratings. Re-calculation Algorithm used is: average of all episode ratings for that show***

---
### lists/titles/adultTitles
***Rest API: To fetch a list of all adult titles on the service.***

---
### lists/titles/type/{type}
***Rest API: To fetch a list of all titles of type. Possible values { short |movie |tvMovie |tvSeries |tvEpisode |tvShort |tvMiniSeries |tvSpecial |video |videoGame }***

---
### lists/titles/genre/{genre}
***Rest API: To fetch a list of all titles of specified genre. Possible values for genre are { Documentary |Short |Animation |Comedy |Romance |Sport |News |Drama |Fantasy |Horror |Biography |Music |War |Crime |Western |Family |Adventure |History |Sci-Fi |Action |Mystery |Thriller |Musical |Film-Noir |Game-Show |Talk-Show |Reality-TV |Adult }***

---
### search/{query}
***Rest API: Search. Currently Supports Title and People. Implementation performs partial word match provided word begins with the query. Support filtering by query param type. if type=person returns only people, if type=title returns only title. default if no type returns both. limit: page size for pagination support. default is set at 100. offset: offset for pagination support***

    "search": "brad",
    "title": {list:[...], ...},
    "people":{list:[...], ...},
    "timestamp": "01/07/2019 14:10:12.040"

---
### Future Work 

#### 1. API To Build:

### lists/titles/cast/{castId}
***Rest API: All titles a particular person has a role in***

---
### lists/titles/cast/role/{roleId}/{castId}
***Rest API: All titles a particular person has played a specific role***

---
### title/{id}/episodes
***Rest API: All episodes for a title***

---
### lists/genres
***Rest API: All Possible Genres***

---
### lists/types
***Rest API: All Possible Types***

---

#### 2. Better logger using tags, log levels

#### 3. Custom Response (decorated) objects, per resource, lists

#### 4. Bash script to create and populate db

#### 5. Abstract design so that any other database can be plugged in. The idea for that support is in some way already in there, due to the Entity object (IMDBEntity) and AbstractDBValuator. So the remaining part is abstraction layer for MySQLStore. 
Here is how it could happen
MySQLStore will implement an IDatabaseImpl
IDatabaseImpl will have methods to populate, retrieve, delete, close connection, so forth
IMDBService, DataUpdatingTool will be instantiated with a IDatabaseImpl
Now in theory, IMDBService can be instantiated by any other database implementation
     