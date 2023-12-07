You will use the [Retrofit](https://square.github.io/retrofit/) library to perform HTTP requests to GitHub. It allows requesting the list of repositories under a given organization and the list of contributors for each repository. See [src/contributors/GitHubService.kt](course://Coroutines/Blocking requests/src/contributors/GitHubService.kt):

```kotlin
interface GitHubService
```

The `loadContributorsBlocking()` function uses this API to fetch the list of contributors for the specified organization.


1. Open [src/tasks/Request1Blocking.kt](course://Coroutines/Blocking requests/src/tasks/Request1Blocking.kt) to see its implementation.
   * At first, you get a list of the repositories under the given organization and store it in the `repos` list. Then, for
        each repository, the list of contributors is requested, and all of the lists are merged into one final list of
        contributors.
   * Both `getOrgReposCall()` and `getRepoContributorsCall()` return an instance of the `*Call` class (`#1`). At this point,
     no request is sent.
   * `*Call.execute()` is then invoked to perform the request (`#2`). `execute()` is a synchronous call that blocks the
     underlying thread.
   * Upon receiving the response, the result is logged by calling the specific `logRepos()` and `logUsers()` functions (`#3`).
     If the HTTP response contains an error, this error will also be logged here.
   * Finally, get the body of the response, which contains the data you need. For this tutorial, you'll use an empty list as a
     result in the event of an error, and the corresponding error will be logged (`#4`).
2. To avoid repeating `.body() ?: emptyList()`, an extension function `bodyList()` is declared.
3. Run the program again and take a look at the system output in IntelliJ IDEA. It should look something like this:

    ```text
    1770 [AWT-EventQueue-0] INFO  Contributors - kotlin: loaded 40 repos
    2025 [AWT-EventQueue-0] INFO  Contributors - kotlin-examples: loaded 23 contributors
    2229 [AWT-EventQueue-0] INFO  Contributors - kotlin-koans: loaded 45 contributors
    ...
    ```

    * The first item on each line indicates the number of milliseconds that have passed since the program started, followed by the thread
      name in square brackets. This helps identify the thread from which the loading request is called.
    * The final item on each line represents the actual message: the number of repositories or contributors that have been loaded.


   This log output demonstrates that all results were logged from the main thread. When you run the code with the _BLOCKING_
   option, the window freezes and doesn't respond to input until the loading is complete. All requests are executed from
   the same thread that invoked `loadContributorsBlocking()`, which is the main UI thread (in Swing, it's the AWT
   event dispatching thread). As this main thread becomes blocked, the UI freezes accordingly:

   ![The blocked main thread](images/blocking.png)

   Once the list of contributors has been loaded, the result is updated.
4. In [src/contributors/Contributors.kt](course://Coroutines/Blocking requests/src/contributors/Contributors.kt), find the `loadContributors()` function, which is responsible for choosing how
   the contributors are loaded, and see how `loadContributorsBlocking()` is invoked.

    * The call to `updateResults()` follows right after the call to `loadContributorsBlocking()`.
    * Since `updateResults()` updates the UI, it must always be called from the UI thread.
    * As `loadContributorsBlocking()` is also called from the UI thread, the UI thread becomes blocked, leading to a frozen UI. 
For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#blocking-requests).
