Kotlin coroutines are much less resource-intensive than threads.
Each time you want to start a new computation asynchronously, you can create a new coroutine instead.

To start a new coroutine, use one of the main _coroutine builders_: `launch`, `async`, or `runBlocking`. Different
libraries can define additional coroutine builders.

`async` starts a new coroutine and returns a `Deferred` object. `Deferred` represents a concept known by other names
such as `Future` or `Promise`. It stores a computation, but it _defers_ the moment you get the final result;
it _promises_ the result sometime in the _future_.

The main difference between `async` and `launch` is that `launch` is used to start a computation that isn't expected to
return a specific result. `launch` returns a `Job` that represents the coroutine. It is possible to wait until it completes
by calling `Job.join()`.

`Deferred` is a generic type that extends `Job`. An `async` call can return a `Deferred<Int>` or a `Deferred<CustomType>`,
depending on what the lambda returns (the last expression inside the lambda is the result).

To get the result of a coroutine, you can call `await()` on the `Deferred` instance. While waiting for the result,
the coroutine that this `await()` is called from is suspended. See the example in the [src/samples/ConcurrencySample.kt](course://Coroutines/Concurrency/src/samples/ConcurrencySample.kt). 

`runBlocking` is used as a bridge between regular and suspending functions, or between the blocking and non-blocking worlds. It works
as an adaptor for starting the top-level main coroutine. It is intended primarily to be used in `main()` functions and
tests.

> Watch <a href="https://www.youtube.com/watch?v=zEZc5AmHQhk" target="_blank">this video</a> for a better understanding of coroutines.

If there is a list of deferred objects, you can call `awaitAll()` to await the results of all of them. See the example in the [src/samples/ConcurrencySample.kt](course://Coroutines/Concurrency/src/samples/ConcurrencySample.kt)

When each "contributors" request is started in a new coroutine, all of the requests are started asynchronously. A new request
can be sent before the result for the previous one is received:

![Concurrent coroutines](images/concurrency.png)

The total loading time is approximately the same as in the _CALLBACKS_ version, but it doesn't need any callbacks.
What's more, `async` explicitly emphasizes which parts run concurrently in the code.

### Task condition

In the [src/tasks/Request5Concurrent.kt](course://Coroutines/Concurrency/src/tasks/Request5Concurrent.kt) file, implement a `loadContributorsConcurrent()` function by using the
previous `loadContributorsSuspend()` function.

<div class="hint">
You can only start a new coroutine inside a coroutine scope. Copy the content
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

For a more detailed description, you can look at [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#concurrency)
