This task helps you familiarize yourself with the task domain. Currently, each contributor's name is repeated
several times, once for every project they have participated in. Implement the `aggregate()` function to consolidate the users
so that each contributor is listed only once. The `User.contributions` property should contain the total number of
contributions made by the given user across _all_ projects. The resulting list should be sorted in descending order according
to the number of contributions.

Open [src/tasks/Aggregation.kt](course://Coroutines/Aggregation/src/tasks/Aggregation.kt) and implement the `List<User>.aggregate()` function. Users should be sorted by the total
number of their contributions.

The corresponding test file [test/tasks/AggregationKtTest.kt](course://Coroutines/Aggregation/test/tasks/AggregationKtTest.kt) provides an example of the expected result.

<div class="hint">

> You can seamlessly switch between the source code and the test class by using the [IntelliJ IDEA shortcut](https://www.jetbrains.com/help/idea/create-tests.html#test-code-navigation)
> &shortcut:GotoTest;

</div>

After implementing this task, the resulting list for the "kotlin" organization should look similar to the following:

![The list for the "kotlin" organization](images/aggregate.png)

For a more detailed description, you can refer to [this article](https://kotlinlang.org/docs/coroutines-and-channels.html#task-1)
