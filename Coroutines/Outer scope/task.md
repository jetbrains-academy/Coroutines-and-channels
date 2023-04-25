When you start new coroutines inside the given scope, it's much easier to ensure that all of them run with the same
context. It is also much easier to replace the context if needed.

Now it's time to learn how using the dispatcher from the outer scope works. The new scope created by
the `coroutineScope` or by the coroutine builders always inherits the context from the outer scope. In this case, the
outer scope is the scope the `suspend loadContributorsConcurrent()` function was called from:

```kotlin
launch(Dispatchers.Default) {  // outer scope
    val users = loadContributorsConcurrent(service, req)
    // ...
}
```

All of the nested coroutines are automatically started with the inherited context. The dispatcher is a part of this
context. That's why all of the coroutines started by `async` are started with the context of the default dispatcher:

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

With structured concurrency, you can specify the major context elements (like dispatcher) once, when creating the
top-level coroutine. All the nested coroutines then inherit the context and modify it only if needed.

<div class="hint">

  > When you write code with coroutines for UI applications, for example Android ones, it's a common practice to
  > use `CoroutineDispatchers.Main` by default for the top coroutine and then to explicitly put a different dispatcher when
  > you need to run the code on a different thread.
</div>

