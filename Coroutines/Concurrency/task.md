Kotlin coroutines are much less resource-intensive than threads.
Whenever you want to start a new computation asynchronously, you can create a new coroutine instead.

To start a new coroutine, use one of the primary _coroutine builders_: `launch`, `async`, or `runBlocking`. Additional coroutine builders 
can be defined by different libraries.

`async` starts a new coroutine and returns a `Deferred` object. `Deferred` represents a concept known by other names,
such as `Future` or `Promise`. It stores a computation but _defers_ the retrieval of the final result,
_promising_ to deliver it in the _future_.

The main difference between `async` and `launch` is that `launch` is used to start a computation that isn't expected to
return a specific result. `launch` returns a `Job` that represents the coroutine. A call to `Job.join()` 
allows one to wait until the computation completes.

`Deferred` is a generic type that extends `Job`. An `async` call can return a `Deferred<Int>` or a `Deferred<CustomType>`,
depending on the return output of the lambda (i.e., the last expression inside the lambda).

To get the result of a coroutine, you can call `await()` on the `Deferred` instance. While waiting for the result,
the coroutine from which this `await()` is called will be suspended. See the example in [src/samples/ConcurrencySample.kt](course://Coroutines/Concurrency/src/samples/ConcurrencySample.kt). 

`runBlocking` serves as a bridge between regular and suspending functions, or between the blocking and non-blocking worlds. It works
as an adaptor for starting the top-level main coroutine. It is primarily intended for use within `main()` functions and
tests.

> Watch <a href="https://www.youtube.com/watch?v=zEZc5AmHQhk" target="_blank">this video</a> for a better understanding of coroutines.

If there is a list of deferred objects, you can call `awaitAll()` to wait for the results of all of them. See an example in [src/samples/ConcurrencySample.kt](course://Coroutines/Concurrency/src/samples/ConcurrencySample.kt).

When each "contributors" request is initiated in a new coroutine, all of the requests are started asynchronously. Hence, a new request
can be sent even before the result for the previous one is received:

![Concurrent coroutines](images/concurrency.png)

The total loading time is approximately equal to that in the _CALLBACKS_ version, but this version doesn't require any callbacks.
Furthermore, `async` explicitly highlights the parts of the code that run concurrently.

## Task

In the [src/tasks/Request5Concurrent.kt](course://Coroutines/Concurrency/src/tasks/Request5Concurrent.kt) file, implement the `loadContributorsConcurrent()` function using the
previously created `loadContributorsSuspend()` function.

<div class="hint">
You can start a new coroutine only within a coroutine scope. Copy the content
from `loadContributorsSuspend()` to the `coroutineScope` call so that you can call `async` functions there:

```kotlin
suspend fun loadContributorsConcurrent(
    service: GitHubService,
    req: RequestData
): List<User> = coroutineScope {
    // ...
}
```

Base your solution on the following scheme:

```kotlin
val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
    async {
        // load contributors for each repo
    }
}
deferreds.awaitAll() // List<List<User>>
```
</div>

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#concurrency).
