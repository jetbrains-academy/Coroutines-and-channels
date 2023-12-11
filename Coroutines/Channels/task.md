## Consecutive vs concurrent

The `updateResults()` callback is called after each request is completed:

![Progress on requests](images/progress.png)

This code doesn't include concurrency. It's sequential, so you don't need synchronization.

The optimal approach would be to send requests concurrently and update the intermediate results after getting the response
for each repository:

![Concurrent requests](images/progress-and-concurrency.png)

To add concurrency, use _channels_.

## Channels

Writing code with a shared mutable state can be quite difficult and error-prone (like in the solution using callbacks).
A simpler way entails sharing information through communication, rather than via a common mutable state.
Coroutines can communicate with each other through _channels_.

Channels are communication primitives that allow data to be passed between coroutines. One coroutine can _send_
some information to a channel, while another can _receive_ that information from the same channel:

![Using channels](images/using-channel.png)

A coroutine that sends (produces) information is often called a producer, and a coroutine that receives (consumes)
information is called a consumer. One or multiple coroutines can send information to the same channel, and one or multiple
coroutines can receive data from it:

![Using channels with many coroutines](images/using-channel-many-coroutines.png)

When many coroutines receive information from the same channel, each element is only handled once by one of the
consumers. Once an element is handled, it is immediately removed from the channel.

You can think of a channel as similar to a collection of elements or, more precisely, a queue, in which elements are added
to one end and received from the other. However, there's an important difference: unlike collections, even in their
synchronized versions, a channel can _suspend_ `send()` and `receive()` operations when the channel is empty
or full. The channel can become full if the channel size has an upper bound.

`Channel` is represented by three different interfaces: `SendChannel`, `ReceiveChannel`, and `Channel`, with the latter
extending the first two. Often, a channel is created and given to the producers as a `SendChannel` instance, ensuring that only
they can send information to the channel.
Conversely, a channel is given to consumers as a `ReceiveChannel` instance so that only they can receive from it. Both `send`
and `receive` methods are declared as `suspend`:

```kotlin
interface SendChannel<in E> {
    suspend fun send(element: E)
    fun close(): Boolean
}

interface ReceiveChannel<out E> {
    suspend fun receive(): E
}

interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
```

The producer can close a channel to indicate that no more elements are coming.

Various types of channels are defined in the library. They differ in the number of elements they can store internally, and
whether the `send()` call can be suspended or not.
For all channel types, the `receive()` call behaves similarly: it receives an element if the channel is not empty;
otherwise, it is suspended.

### Unlimited channel
An unlimited channel is the closest analog to a queue: producers can send elements to this channel and it will
keep growing indefinitely. The <code>send()</code> call will never be suspended.
However, if the program runs out of memory, an <code>OutOfMemoryException</code> will be thrown.
The key difference between an unlimited channel and a queue is this: when a consumer tries to receive from an empty channel,
it becomes suspended until some new elements are sent.
![Unlimited channel](images/unlimited-channel.png)
### Buffered channel
The size of a buffered channel is constrained by a specified number.
Producers can send elements to this channel until the size limit is reached, with all elements being stored internally.
When the channel is full, any subsequent `send` call on it is suspended until more free space becomes available.
![Buffered channel](images/buffered-channel.png)
### Rendezvous channel
The "Rendezvous" channel is a channel without a buffer, acting the same as a buffered channel with a zero size.
One of the functions (<code>send()</code> or <code>receive()</code>) is always suspended until the other function is called. 
If the <code>send()</code> function is called and there isn't a suspended <code>receive</code> call ready to process the element, then <code>send()</code>
is suspended. Similarly, if the <code>receive</code> function is called and the channel is empty or, which means there's no
suspended <code>send()</code> call ready to send the element, the <code>receive()</code> call is then suspended. 

The term "rendezvous" (meaning "a meeting at an agreed time and place") refers to the fact that <code>send()</code> and <code>receive()</code>
must "meet on time".
![Rendezvous channel](images/rendezvous-channel.png)

### Conflated channel
When a new element is sent to the conflated channel, it overwrites the previously sent element, so the receiver will always
get only the latest element. The <code>send()</code> call will never be suspended.
![Conflated channel](images/conflated-channel.gif) 

When you create a channel, specify its type or the buffer size (if you need a buffered one):

```kotlin
val rendezvousChannel = Channel<String>()
val bufferedChannel = Channel<String>(10)
val conflatedChannel = Channel<String>(CONFLATED)
val unlimitedChannel = Channel<String>(UNLIMITED)
```

By default, a "Rendezvous" channel is created.

In the following task, you'll create a "Rendezvous" channel, along with two producer coroutines and one consumer coroutine. Check out [src/samples/ChannelsSample.kt](course://Coroutines/Channels/src/samples/ChannelsSample.kt).

<div class="hint">

> Watch <a href="https://www.youtube.com/watch?v=HpWQUoVURWQ" target="_blank">this video</a> for a better understanding of channels.

</div>


## Task

In [src/tasks/Request7Channels.kt](course://Coroutines/Channels/src/tasks/Request7Channels.kt), implement the function `loadContributorsChannels()`. This function should concurrently request all GitHub
contributors while showing intermediate progress simultaneously.

Use the previous functions, `loadContributorsConcurrent()` from [src/tasks/Request5Concurrent.kt](course://Coroutines/Channels/src/tasks/Request5Concurrent.kt)
and `loadContributorsProgress()` from [src/tasks/Request6Progress.kt](course://Coroutines/Channels/src/tasks/Request6Progress.kt), for this task.


<div class="hint"> 

Keep in mind that different coroutines, which concurrently receive contributor lists for various repositories, can send all the received
results to the same channel:

```kotlin
val channel = Channel<List<User>>()
for (repo in repos) {
    launch {
        val users = TODO()
        // ...
        channel.send(users)
    }
}
```

Then, the elements from this channel can be received one by one and processed:

```kotlin
repeat(repos.size) {
    val users = channel.receive()
    // ...
}
```

Since the `receive()` calls are sequential, no additional synchronization is needed.
</div>

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#channels).
