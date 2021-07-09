package com.gabo.architecture.utils

/**
 *  This class represents a Result of a remote resource request. It is used in this demo for
 *  network calls but can be used in the future for loading other resources like database entities.
 *
 *  As it could be used too for local cached data  it shouldn't be tied to NetworkException, hence
 *  the generic throwable.
 *
 *  If we wanted to add a local cache, we could solve the caching / request logic in the repo with
 *  little to no changes in the ViewModel layer.
 *
 */
sealed class Resource<out T, out R : Throwable> {
    class Success<T>(val value : T) : Resource<T, Nothing>()
    class Error<R : Throwable>(val error : R) : Resource<Nothing, R>()

    /**
     *  Utility for transforming one resource to another when it's successful, for example,
     *  within the Repo layer to filter or map stuff.
     */
    fun <E> map(transform: (T)->E ) : Resource<E, R>{
        return when(this){
            is Success -> Success(transform(this.value))
            is Error -> Error(this.error)
        }
    }
    /**
     *  Useful for performing actions in a ViewModel when a Resource is Successful.
     *  It is important to note that this action will only be executed if the call for the
     *  resource succeed.
     *
     */
    fun alsoDo(toDo : (T) -> Unit) : Resource<T, R> {
        if(this is Success){
            toDo(this.value)
        }
        return this
    }
}