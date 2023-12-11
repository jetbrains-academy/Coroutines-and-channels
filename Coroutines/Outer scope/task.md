When you start new coroutines within a given scope, it becomes much easier to ensure that all of them run within the same
context. It also simplifies the process of replacing the context if needed.

Now, it's time to learn how utilizing the dispatcher from the outer scope works. The new scope created by either 
the `coroutineScope` or by the coroutine builders always inherits the context from the outer scope. In this case, the
outer scope is the scope the `suspend loadContributorsConcurrent()` function was called from:

```kotlin
launch(Dispatchers.Default) {  // outer scope
    val users = loadContributorsConcurrent(service, req)
    // ...
}
```

All nested coroutines are automatically started with the inherited context, of which the dispatcher is a part.
That's why all of the coroutines instigated by `async` are started within the context of the default dispatcher:

```kotlin
suspend fun loadContributorsConcurrent(
    service: GitHubService, req: RequestData
): List<User> = coroutineScope {
    // this scope inherits the context from the outer scope
    // ...
    async {   // nested coroutine started with the inherited context
        // ...
    }
    // ...
}
```

With structured concurrency, you can specify major context elements (such as the dispatcher) once, when creating the
top-level coroutine. All nested coroutines then inherit this context and only modify it as needed.

<div class="hint">

  > In the context of UI applications using coroutines, such as Android applications, a common practice involves
  > using `CoroutineDispatchers.Main` as the default for the top coroutine. Then, to run code on a different thread, a different dispatcher is
  > explicitly assigned.
</div>

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#using-the-outer-scope-s-context)
