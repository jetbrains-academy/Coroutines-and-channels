You can implement the same logic using suspending functions. Instead of returning `Call<List<Repo>>`, define the API
call as a [suspending function](https://kotlinlang.org/docs/composing-suspending-functions.html) as follows:

```kotlin
interface GitHubService {
    @GET("orgs/{org}/repos?per_page=100")
    suspend fun getOrgRepos(
        @Path("org") org: String
    ): List<Repo>
}
```

* `getOrgRepos()` is defined as a `suspend` function. When you use a suspending function to perform a request, the
  underlying thread isn't blocked. More details about how this works will come in later sections.
* `getOrgRepos()` returns the result directly instead of returning a `Call`. If the result is unsuccessful, an
  exception is thrown.

Alternatively, Retrofit allows returning the result wrapped in `Response`. In this case, the result body is
provided, and it is possible to check for errors manually. This tutorial uses the versions that return `Response`.

See the new declarations for `getOrgRepos` and `getRepoContributors` in [src/contributors/GitHubService.kt](course://Coroutines/Suspending functions/src/contributors/GitHubService.kt)

## Task

Your task is to change the function code that loads contributors to make use of two new suspending functions,
`getOrgRepos()` and `getRepoContributors()`. The new `loadContributorsSuspend()` function is marked as `suspend` in order to use the
new API.

> Suspending functions can't be called everywhere. Calling a suspending function from `loadContributorsBlocking()` will
> result in an error with the message "Suspend function 'getOrgRepos' should be called only from a coroutine or another
> suspend function".


1. Copy the implementation of `loadContributorsBlocking()` defined in [src/tasks/Request1Blocking.kt](course://Coroutines/Suspending functions/src/tasks/Request1Blocking.kt)
   into `loadContributorsSuspend()` defined in [src/tasks/Request4Suspend.kt](course://Coroutines/Suspending functions/src/tasks/Request4Suspend.kt).
2. Modify the code so that the new suspending functions replace the ones that return `Call`s.
3. Run the program by choosing the _SUSPEND_ option and ensure that the UI remains responsive during the execution of GitHub requests.   

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#suspending-functions).
