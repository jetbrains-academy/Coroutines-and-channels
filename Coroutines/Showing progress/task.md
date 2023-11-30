Despite the information for some repositories being loaded rather quickly, the user only sees the resulting list after all of
the data has been loaded. Until then, the loader icon runs showing the progress, but there's no information about the current
state or what contributors are already loaded.

You can show the intermediate results earlier and display all of the contributors after loading the data for each of the
repositories:

![Loading data](images/loading.gif)

To implement this functionality, in the [src/tasks/Request6Progress.kt](course://Coroutines/Showing progress/src/tasks/Request6Progress.kt), you'll need to pass the logic updating the UI
as a callback, so that it's called on each intermediate state:

```kotlin
suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    // loading the data
    // calling `updateResults()` on intermediate states
}
```

On the call site in [src/contributors/Contributors.kt](course://Coroutines/Showing progress/src/contributors/Contributors.kt), the callback is passed to update the results from the `Main` thread for
the _PROGRESS_ option:

```kotlin
launch(Dispatchers.Default) {
    loadContributorsProgress(service, req) { users, completed ->
        withContext(Dispatchers.Main) {
            updateResults(users, startTime, completed)
        }
    }
}
```

* The `updateResults()` parameter is declared as `suspend` in `loadContributorsProgress()`. It's necessary to call
  `withContext`, which is a `suspend` function inside the corresponding lambda argument.
* `updateResults()` callback takes an additional Boolean parameter as an argument specifying whether the loading has
  completed and the results are final.

### Task condition

In the [src/tasks/Request6Progress.kt](course://Coroutines/Showing progress/src/tasks/Request6Progress.kt) file, implement the `loadContributorsProgress()` function that shows the intermediate
progress. Base it on the `loadContributorsSuspend()` function from [src/tasks/Request4Suspend.kt](course://Coroutines/Showing progress/src/tasks/Request4Suspend.kt).

* Use a simple version without concurrency; you'll add it later in the next section.
* The intermediate list of contributors should be shown in an "aggregated" state, not just the list of users loaded for
  each repository.
* The total number of contributions for each user should be increased when the data for each new
  repository is loaded.

For a more detailed description, you can look at [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#showing-progress)