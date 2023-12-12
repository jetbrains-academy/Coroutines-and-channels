In the previous solution, the whole loading logic was moved to a background thread. However, this may not be the best use of
resources. The issue is that all of the loading requests are going sequentially, blocking the thread while waiting for the loading result.
The thread could, instead, be occupied by other tasks. Specifically, the thread could start loading another request and thus
receive the entire result earlier.

The data handling for each repository should be divided into two parts: loading and processing the
resultant response. The latter _processing_ part should be extracted into a callback.

The loading for each repository can then start before the result for the previous repository is received and before the
corresponding callback is called:

![Using callback API](images/callbacks.png)

The Retrofit callback API can help achieve this. The `Call.enqueue()` function starts an HTTP request and takes a
callback as an argument. In this callback, you must specify what needs to be done after each request.

Open [src/tasks/Request3Callbacks.kt](course://Coroutines/Retrofit callback API/src/tasks/Request3Callbacks.kt) and see the implementation of `loadContributorsCallbacks()` that uses this API.

* For convenience, this code fragment uses the `onResponse()` extension function declared in the same file. This function takes a
  lambda as an argument rather than an object expression.
* The logic to handle the responses is extracted into callbacks: the corresponding lambdas start at lines `#1` and `#2`.

However, the provided solution doesn't work. If you run the program and load contributors by choosing the _CALLBACKS_
option, you'll see that nothing is displayed. However, the tests that immediately return the result pass.

## Task

Rewrite the code in the [src/tasks/Request3Callbacks.kt](course://Coroutines/Retrofit callback API/src/tasks/Request3Callbacks.kt) file so that the loaded list of contributors is displayed.

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#use-the-retrofit-callback-api).
