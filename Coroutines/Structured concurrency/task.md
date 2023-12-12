* The _coroutine scope_ is responsible for the structure and parent-child relationships between distinct coroutines. New
  coroutines usually need to be started within a scope.
* The _coroutine context_ stores additional technical information used to run a given coroutine, like the coroutine's custom
  name, or the dispatcher specifying the threads on which the coroutine should be scheduled.

When `launch`, `async`, or `runBlocking` are used to start a new coroutine, they automatically create the corresponding
scope. All of these functions take a lambda with a receiver as an argument, and `CoroutineScope` is the implicit receiver type:

```kotlin
launch { /* this: CoroutineScope */ }
```

* New coroutines can only be started within a scope.
* Both `launch` and `async` are declared as extensions to `CoroutineScope`, so an implicit or explicit receiver must always
  be passed when you call them.
* The coroutine started by `runBlocking` is the only exception, as `runBlocking` is defined as a top-level function.
  However, since it blocks the current thread, it's primarily intended to be used in `main()` functions and tests as a bridge
  function.

A new coroutine within `runBlocking`, `launch`, or `async` is automatically started within the scope:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking { /* this: CoroutineScope */
    launch { /* ... */ }
    // the same as:   
    this.launch { /* ... */ }
}
```

When you call `launch` inside `runBlocking`, it's called as an extension to the implicit receiver of
the `CoroutineScope` type. Alternatively, you could explicitly write `this.launch`.

The nested coroutine (started by `launch` in this example) can be considered a child of the outer coroutine (started
by `runBlocking`). This "parent-child" relationship works through scopes; the child coroutine is started from the scope
corresponding to the parent coroutine.

It's possible to create a new scope without starting a new coroutine, by using the `coroutineScope` function.
To start new coroutines in a structured way inside a `suspend` function without access to the outer scope, you can create
a new coroutine scope that automatically becomes a child of the outer scope from which this `suspend` function is called.
`loadContributorsConcurrent()`is a good example.

You can also start a new coroutine from the global scope using `GlobalScope.async` or `GlobalScope.launch`,
creating a top-level "independent" coroutine.

The mechanism behind the structure of coroutines is called _structured concurrency_. It provides the following
benefits over global scopes:

* The scope is generally responsible for child coroutines, whose lifetime is tethered to the lifetime of the scope.
* The scope can automatically cancel child coroutines if something goes wrong or if a user changes their mind and decides
  to revoke the operation.
* The scope automatically waits for the completion of all child coroutines.
  Therefore, if the scope corresponds to a coroutine, the parent coroutine does not complete until all the coroutines
  launched in its scope have completed.

When using `GlobalScope.async`, there is no structure that binds several coroutines to a smaller scope.
Coroutines started from the global scope are all independent – their lifespan is limited only by the lifetime of the
whole application. It's possible to store a reference to the coroutine started from the global scope and wait for its
completion or cancel it explicitly, but that won't happen automatically as it would with structured concurrency.

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#structured-concurrency).
