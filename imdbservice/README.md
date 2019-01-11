# Design

### EER Diagram  ![adsf](db.png)

# Database Setup Instructions 

#REST APIS:



title/{id}

    Returns Title details

title/rating/{id}

    Returns Title ratings

title/cast/{id}
    Return Title Casts

person/{id}

    Returns Person details

All LIST APIs accept limit and offset query params

lists/calculatedRatings
    returns new calculated ratings

lists/adultTitles

    Returns list of all adult movies

lists/type/{type}

    Returns list of all movies of type in the path, e.x. short, movie, etc..

lists/genre/{genre}

    Returns list of all movies of genre in the path, e.x. Drama, Crime, etc..

search/{query}

    Returns both list titles & people returned

    "search": "brad",
    "title": {list:[...], ...},
    "people":{list:[...], ...},
    "timestamp": "01/07/2019 14:10:12.040"

Supports filtering by
if type query param has type=person, type=title


TODO:

12345 directed any actions titles?
    lists/action/?director=12345

12345 in any actions titles?
    lists/action/?actor=12345

