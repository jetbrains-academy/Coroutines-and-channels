The previous solution works, but it blocks the thread and therefore freezes the UI. A traditional approach to prevent this
is to use _callbacks_.

Instead of executing the code immediately after the operation is completed, you can extract it
into a separate callback, often a lambda, and pass that lambda to the caller to be invoked later.

To make the UI responsive, you can either move the entire computation to a separate thread or switch to the Retrofit API,
which uses callbacks instead of blocking calls.
### Use a background thread

1. Open [src/tasks/Request2Background.kt](course://Coroutines/Callbacks/src/tasks/Request2Background.kt) and examine its implementation. First, the entire computation is moved to a different
   thread. The `thread()` function starts a new thread:

    ```kotlin
    thread {
        loadContributorsBlocking(service, req)
    }
    ```

   Now that all of the loading has been moved to a separate thread, the main thread is free and can be occupied by other
   tasks:

   ![The freed main thread](images/background.png)

2. The signature of the `loadContributorsBackground()` function has changed. It now takes an `updateResults()`
   callback as the last argument to be invoked once all the data has been loaded.
3. Once the `loadContributorsBackground()` is called, the invocation of  `updateResults()` is moved to the callback, instead of being executed immediately
   afterward, as was done previously:

    ```kotlin
    loadContributorsBackground(service, req) { users ->
        SwingUtilities.invokeLater {
            updateResults(users, startTime)
        }
    }
    ```

   By calling `SwingUtilities.invokeLater`, you ensure that the `updateResults()` call, which updates the results,
   occurs on the main UI thread (AWT event dispatching thread).

However, if you try to load the contributors via the `BACKGROUND` option, you can see that while the list is updates,
there are no visible changes.

## Task

Fix the `loadContributorsBackground()` function in [src/tasks/Request2Background.kt](course://Coroutines/Callbacks/src/tasks/Request2Background.kt) so that the resulting list is shown
in the UI.

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#callbacks).
