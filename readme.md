# Popular Movies - Stage 2

A simple Android application that communicates with [TMDb API](https://www.themoviedb.org/documentation/api)

The app consists of two activities, one that uses a RecyclerView to list the most popular or top rated movies and the other providing the details for those movies

The application now offers the option to save favourite movies to a local room database and the ability to view a list of reviews and trailers.

Trailers are shown in either the YouTube app or via a web browser when tapped

### Adding the API Key
For the app to work an API key is required from TMDb. 

In the `/app/build.gradle` file replace the `"YOUR_API_KEY_HERE"` with your api key on line 13
```
resValue "string", "movie.db.api.key", "YOUR_API_KEY_HERE"
```