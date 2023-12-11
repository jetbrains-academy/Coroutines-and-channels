The code with suspending functions looks similar to the "blocking" version. The major difference from the blocking version
is that instead of blocking the thread, the coroutine is suspended:

```text
block -> suspend
thread -> coroutine
```

> Coroutines are often called lightweight threads due to the similarity in how code is run on both.
> Operations that previously blocked — and therefore had to be avoided — can now instead suspend the coroutine.


### Starting a new coroutine

If you look at how `loadContributorsSuspend()` is used in [src/contributors/Contributors.kt](course://Coroutines/Coroutines/src/contributors/Contributors.kt), you can see that it's
called within `launch`. `launch` is a library function that takes a lambda as an argument:

```kotlin
launch {
    val users = loadContributorsSuspend(req)
    updateResults(users, startTime)
}
```

Here, `launch` starts a new computation responsible for both loading the data and showing the results. The computation
is suspendable – when performing network requests, it is suspended and releases the underlying thread.
When the network request returns a result, the computation resumes.

Such a suspendable computation is called a _coroutine_. Thus, in this case, `launch` _starts a new coroutine_ responsible
for loading data and showing the results.

Coroutines run over threads and can be suspended. When a coroutine is suspended, the
corresponding computation is paused, removed from the thread, and stored in memory. Meanwhile, the thread becomes free to 
manage other tasks:

![Suspending coroutines](images/suspension-process.gif)

When a computation is ready to be continued, it is returned to a thread (though, not necessarily the same one).

In the `loadContributorsSuspend()` example, each "contributors" request now waits for the result by utilizing the suspension
mechanism. First, a new request is sent. Then, while waiting for a response, the entire "load contributors" coroutine
started by the `launch` function becomes suspended.

The coroutine resumes once the corresponding response is received:

![Suspending request](images/suspend-requests.png)

While awaiting the response, the thread is free to undertake other tasks, keeping the UI responsive
despite all the requests taking place on the main UI thread:

1. Run the program using the _SUSPEND_ option. The log confirms that all of the requests are sent to the main UI thread:

    ```text
    2538 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - kotlin: loaded 30 repos
    2729 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - ts2kt: loaded 11 contributors
    3029 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - kotlin-koans: loaded 45 contributors
    ...
    11252 [AWT-EventQueue-0 @coroutine#1] INFO  Contributors - kotlin-coroutines-workshop: loaded 1 contributors
    ```

2. The log can show you which coroutine is running the corresponding code. For configuration invoked by clicking the `Run` button, this is already set up. To enable this in your own configurations, open **Run | Edit configurations**
   and add the `-Dkotlinx.coroutines.debug` VM option:


   ![Edit run configuration](images/run-configuration.png) &shortcut:ChooseRunConfiguration;

   The coroutine name will be attached to the thread name while `main()` is running with this option. You may also
   modify the template for running all Kotlin files and enable this option by default.

Now all code runs within a single coroutine, the "load contributors" coroutine mentioned above, denoted as `@coroutine#1`.
While waiting for the result, you shouldn't reuse the thread for sending other requests, as the code is
written sequentially. A new request is sent only once the previous result has been received.

Suspending functions treat the thread fairly and don't block it for "waiting". However, this doesn't yet bring any concurrency
into the picture.

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#coroutines).
