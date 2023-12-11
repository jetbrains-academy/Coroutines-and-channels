Look at two versions of the function that loads the list of contributors. Compare how both versions behave when you try to
cancel the parent coroutine. The first version uses `coroutineScope` to start all of the child coroutines,
whereas the second one uses `GlobalScope`.

1. In [src/tasks/Request5Concurrent.kt](course://Coroutines/Canceling/src/tasks/Request5Concurrent.kt), uncomment the 3-second delay in the `loadContributorsConcurrent()` function.

   The delay affects all of the coroutines that send requests, allowing enough time to cancel the loading
   after the coroutines have started but before the requests are sent.

2. Look at the second version of the loading function, `loadContributorsNotCancellable()`, in [src/tasks/Request5NotCancellable.kt](course://Coroutines/Canceling/src/tasks/Request5NotCancellable.kt)
3. It uses `GlobalScope.async`:

    ```kotlin
    suspend fun loadContributorsNotCancellable(
        service: GitHubService,
        req: RequestData
    ): List<User> {   // #1
        // ...
        GlobalScope.async {   // #2
            log("starting loading for ${repo.name}")
            // load repo contributors
        }
        // ...
        return deferreds.awaitAll().flatten().aggregate()  // #3
    }
    ```

  * The function now returns the result directly, not as the last expression within the lambda (lines `#1` and `#3`).
  * All of the "contributors" coroutines are started inside the `GlobalScope`, not as children of the coroutine scope
    (line `#2`).

4. Run the program and choose the `CONCURRENT` option to load the contributors.
5. Wait until all of the "contributors" coroutines have started, then click _Cancel_. The log shows no new results,
   which means that all of the requests were indeed canceled:

    ```text
    2896 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - kotlin: loaded 40 repos
    2901 [DefaultDispatcher-worker-2 @coroutine#4] INFO  Contributors - starting loading for kotlin-koans
    ...
    2909 [DefaultDispatcher-worker-5 @coroutine#36] INFO  Contributors - starting loading for mpp-example
    /* click on 'cancel' */
    /* no requests are sent */
    ```

6. Repeat step 5, but this time, choose the `NOT_CANCELLABLE` option:

    ```text
    2570 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - kotlin: loaded 30 repos
    2579 [DefaultDispatcher-worker-1 @coroutine#4] INFO  Contributors - starting loading for kotlin-koans
    ...
    2586 [DefaultDispatcher-worker-6 @coroutine#36] INFO  Contributors - starting loading for mpp-example
    /* click on 'cancel' */
    /* but all the requests are still sent: */
    6402 [DefaultDispatcher-worker-5 @coroutine#4] INFO  Contributors - kotlin-koans: loaded 45 contributors
    ...
    9555 [DefaultDispatcher-worker-8 @coroutine#36] INFO  Contributors - mpp-example: loaded 8 contributors
    ```

   In this case, no coroutines are canceled, and all the requests are still sent.

7. See how the cancellation is triggered in [src/contributors/Contributors.kt](course://Coroutines/Canceling/src/contributors/Contributors.kt). When the _Cancel_ button is clicked,
   the main "loading" coroutine is explicitly canceled and the child coroutines are canceled automatically.

The `launch` function returns an instance of `Job`. `Job` stores a reference to the "loading coroutine", which loads
all of the data and updates the results. You can call the `setUpCancellation()` extension function on it (line `#1`),
passing an instance of `Job` as a receiver.

Another way to express this would be to explicitly write:

```kotlin
val job = launch { }
job.setUpCancellation()
```

* For readability, you could refer to the `setUpCancellation()` function receiver within the function using the
  new `loadingJob` variable (line `#2`).
* Then you could add a listener to the _Cancel_ button so that when it's clicked, the `loadingJob` is canceled (line `#3`).

With structured concurrency, you only need to cancel the parent coroutine, as this automatically results in the cancellation
of all child coroutines.

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#canceling-the-loading-of-contributors).
