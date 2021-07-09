# Android demo

This is  a simple app that was made for a two-weeks code challenge and I'm now using it as an example of how I'd build an app from scratch and which patterns I'd apply. This is not an exhaustive example due to the time limit , it is just as how it was when I finished ,  there are lots of stuff that I'd probably be refactoring in the future, and I reused many components from my personal (and perpetually  _unfinished_ ) projects.
### About the app

The app consists on a marketplace search client using Mercado Libre's public API to perform the searches. We have a home screen, search results screen and item details screen. That was the goal of the exercise.


In the Search Results section I also added a contextual filters dialog that wasn't part of the exercise but I took advantage of the power of MeLi's context filters systems that change according to the search keywords and context (Which I think it's awesome!)



### Requirements
- **Kotlin 1.5.10 support**
- **Android Studio > 4.2.0**


### Architecture
In order to make the app extensible and to achieve a better separations of concerns I've divided it in the following modules:

- **app** : Contains all the business & UI logic.
- **architecture** : It's like the core module of the app. It'll contain all the shared code and structural components such as the navigation framework, deeplinks handling , Intents handling & interactions with the OS. The idea of this module is to hold all the dependencies that future feature modules would require regarding the application  and it's interaction with the Android framework.
- **network** : It contains all the network framework to perform our newtork calls based on   Retrofit + OkHttp + Gson . It also includes the relevant seladed classes for transporting the network responses back to the UI.
- **models** : Has  all the models that our app will use. For simplicity (to meet the 2wks deadline) ,  these are basically the gson models fetched from the server, however , in a better approach,  it should be divided  further in : _dtos_ , _domain_ and _entities (Room)_  classes to keep the business logic away from the transport or persistence concerns.
- **mocks** :  Contains all the factories and common helpers for Unit & UI tests.



### Network Framework
Our Network layer is exclusively based on coroutines and I've put all the calls low-level logic in a  class called NetworkCall and it's corresponding implementation, taking advantage of Retrofit's Type Adapters (will further explain bellow). I think  the main advantage of having a Network Framework in place is for all the team members to be in the same page when making network calls and also it helps to separate concerns  and centralize all our network decisions in the same place.

##### NetworkCall
It's main objective is to centralize all the transport-level network calling logic. Using an interface here will allow us to further separate the implementation from it's uses and also allows us to write more testable code. It only has a single suspended `execute()` method to enforce the use of coroutines.

````
interface NetworkCall<T>{
    suspend fun execute() : NetworkResponse<T>
}
````

###### Why to enforce the use of coroutines?
One of the biggest advantages of starting an app from scratch is the possibility of defining your own framework by solving small pieces of a bigger problem, such as handling the async calls in a network layer, and have your whole team togheter aligned with that solution. Corutines , as per today, are considered to be one of the best solutions to the asynchronism  problem in Kotlin, therefore, I chose them as the solution in our network layer. On the other hand, it could be anything : AsyncTasks , RxJava or even callback-based approaches. Whatever fit the best to your use cases. The only important thing IMO is to have that defined as part of your network framework, make it consistent across the app to avoid a Quimera code but with the flexibility for , eventually, migrating it if a new and better solution appears.


##### NetworkResponse:
Sealed class that represents the result of the network call.
```
sealed class NetworkResponse<out T> {
    class Success<T>(val value : T) : NetworkResponse<T>()
    class Error(val error : NetworkException) : NetworkResponse<Nothing>()
}
```
One advantage of returning a sealed class from the NetworkCall such as NetworkResponse<T> instead of returning just T and throwing an exception for the network errors is that it'd be less error prone in Kotlin. Kotlin doesn't enforce exception catching as Java does so , if someone in your team (that can be really anyone in big teams) ever forgets catching a NetworkException while doing a call , that would mean a runtime crash in Kotlin. Using a sealed class here force you to treat the Success and Error cases (in a similar way that [Rust](https://doc.rust-lang.org/book/ch09-00-error-handling.html) does), and there are no risks of crashes.




##### NetworkException
It represents a network error based on the API error responses. As it's a business object that we control we can react differently to different kind of error codes, for example, if it was a `NOT_FOUND` error we could show one error state, if it's  a `SERVER_ERROR` show a different error message, etc. It's always good to have your API errors translated to your own error objects that you control so your app can be aware of them, rather than just using generic IOExceptions. This  should also be standar when possible and part of the network module

```

class NetworkException(
    private val code: Int,
    private val errorCode : Error,
    cause : Throwable?
) : Exception(cause){
    enum class Error {
        NETWORK_ISSUE,
        NOT_FOUND,
        SERVER_ERROR,
        BAD_REQUEST,
        REDIRECT;
        companion object {
            fun fromCode(code : Int) : Error {
                return when(code) {
                    in 300 until 399 -> REDIRECT
                    in 400 until 403 -> BAD_REQUEST
                    404 -> NOT_FOUND
                    in 405 until 499 -> BAD_REQUEST
                    in 500 until 599 -> SERVER_ERROR
                    else -> NETWORK_ISSUE
                }
            }
        }
    }
```
##### ThreadSafetyCheckInterceptor

Consists of an OkHttp Interceptor that would make the debuggable variants of our app  crash whenever we perform a network call on the UI threead by mistake. This will force us to fix the problem in development, effectively preventing UI thread network calls to arrive to production. Also, as we make sure it's only applied in debuggable variants, it would never  crash in production . If we also used **MockWebServer** for UI tests then the tests would also catch these bad blocking calls.

```
class ThreadSafetyCheckInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if(BuildConfig.DEBUG && checkIfMainThread()){
            val url =  chain.request().url
            throw IllegalStateException("Network call on the main thread for url : $url")
        }
        return chain.proceed(chain.request())
    }

```


### Pagination and PaginatedAdapter
An important part of this excercise was to solve the pagination. We'd want to have an infinite scroll style pagination so a new page gets loaded when the user scrolls to the end of the recycler view. It would be great if we could also make it reusable for any kind of pagination system in the future. We could make use of generalization and polymorphism to achieve this.

I chose to not use Google's Paging library (even when I have worked with it before)  because  IMO it could be solved with a simpler code , giving us more control and ownership on our own codebase while, at the same time, having a better sepparation of concerns in layers:  keeping the network layer away from the UI layer and better integrating this with whatever your architecture is : MVVM, MVP, etc

We begin defining the following interfaces:
```
interface Page<T> {
    val pagination: Pagination
    val items: List<T>
    fun append(next : Page<T>) : Page<T>
}
```
Pagination :
```
interface Pagination {
    val hasMoreItems: Boolean
    fun nextPage(): Pagination
    fun toQueryMap(): Map<String, String>
}
```



A Page implementation must :
- Have a list of  T elements represeting the contents of the page itself
- Have a Pagination object which knows in which page we are and what is the following page.
- Being able to append another `Page<T>`  of the same kind , producing a Page with the elements of the next page (n+1) at the end of the list , which  `Pagination.nextPage()`  pointing to the Page (n+2)

A Pagination implementation must:
  - Know whether or not if there are more pages available
  - Be able to produce a new Pagination object pointing to the next page
  - Transform itself to a string,string map  so it can be sent through query parameters or as part of a json body, depending on the backend implementation.

We'll be able to easily construct a reusable infinite-scroll pagination with any Page and Pagination implementations that complies with the former requirements.

If we see an implementation of `Pagination` , for example , `OffsetPagination` ,  we'll see how easily we could implement the Pagination interface. In this case it consist of an object comming from a server response with an `offset`and a `limit` . You just pass those properties as query parameters in the next page  request and the MeLi API retrieves the page you asked for.

```
data class OffsetPagination(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("limit")
    val limit: Int
) : Pagination {
    override val hasMoreItems: Boolean
        get() = offset + limit < total

    override fun nextPage(): Pagination {
        return this.copy(
            offset = offset + limit
        )
    }

    override fun toQueryMap(): Map<String, String> {
        return mapOf(
            "offset" to offset.toString(),
            "limit" to limit.toString()
        )
    }
}
```
But we could also easily implement a  `Pagination` of the type `HashScanPagination`  using a hash-scan based pagination system (in which the backend send you a token which you could use to require the next page) just checking if the next page hash is null or not for `hasMorePages` and  returnign a copy of itself with the next page hash as the  `nextPage()` Pagination.

It is also worth mentioning that , given these data structures, we could sepparate the Page<T> implementation from the Pagination implementation, being able to reuse the same Pagination system for diferent type of Pages

In our concrete code, the SearchPage<T> is an implementation example of Page<T> representing a paginated generic search of items T that uses an offset-based pagination.

```
data class SearchPage(
...
@SerializedName("paging")
val paging: OffsetPagination,
@SerializedName("results")
val results: List<T>,
....
) : Page<T> {
    override val items: List<T>
        get() = results
    override val pagination: Pagination
        get() = paging
}
```
As this is a GSON model that will be deserialized in our network stack, all the Pagination and next page fetching logic will be already implemented for us in the interface implementation and we can just use that response in our PaginatedAdapter without writing more code.

##### PaginatedAdapter and BaseAdapter

`BaseAdapter` responsability is to adapt and render different types of views for a RecyclerView. It's similar to `Groupie` from Square, and it allows a huge deal of flexibility as it's able to inflate any kind of view for the same adapter. You can see it's implementation here (link)

In order to use it every row that will be inserted in the adapter must implement the interface  `Row<T : ViewBinding>`

```
interface Row<B : ViewBinding> {
    fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ) : B

    fun bind(binding : B)

}
```
- `createBinding` Must inflate the ViewBinding for rendering that row  . It's worth mentioning that  `createBinding` is not relaed with any Row instance and is just a view factory for the given type of row.

- `bind(binding : B)` binds the current Row instance with the ViewBinding for that row. Here we should use the row data to populate the view and this will be executed in the onBindViewHolder of BaseAdapter.

 `PaginatedAdapter<T>` holds all the logic of our infinite-scroll pagination. You could see it's implementation here (link) . What it does is to render all the rows for the current page's items , then check if `page.pagination.hasMoreElements == true` to see if there are more pages and , if there are, then add a LoadingRow at the end of the list which would invoke the loadNedxtPage(pagination) callback when it gets binded.

 Even if it's sounds a bit complicated at first , it's use in the code is rather simple:

```
 val adapter: PaginatedAdapter<Item> =  PaginatedAdapter(
            loadNextPage = { pagination ->
                searchViewModel.loadNextPage(pagination)
            },
            mapRow = { item ->
                SearchResultRow(item) {
                    SearchResultsFragmentDirections
                        .openItemFromSearch(item.id, item)
                        .open(context)
                }
            },
```

SearchResultRow

```
class SearchResultRow(
    private val item : Item,
    private val onClick : (Item) -> Unit
) : Row<SearchResultRowBinding> {
   ....
}
```


The adapter needs to know
- How to fetch a new page
-  How to convert a given item to a BaseRow

The UI component that uses the adapter will answer those questions. It'll delegate all the network fetching to a ViewModel following  a MVVM architecture an pass a row mapping function



### Some notes
* Rather to give any perfect solution for a given problem, I'm trying to express my thinking process and how I break down problems to smaller pieces when building an app. There are countless better approaches or different architectures or patterns, one of the most important things IMO is to be consistent with those patterns and architectures.
* Further documentation will  be added

## Thank you for reading
##### Hope we could meet again soon.