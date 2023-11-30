package tasks

import contributors.MockGithubService
import contributors.concurrentProgressResults
import contributors.testRequestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class Request7ChannelsKtTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testChannels() = runTest {
        val startTime = currentTime
        var index = 0
        var resultUpdated = false
        loadContributorsChannels(MockGithubService, testRequestData) {
                users, _ ->
            val expected = concurrentProgressResults[index++]
            val time = currentTime - startTime
            resultUpdated = true

            Assert.assertEquals("Wrong intermediate result after $time:", expected.users, users)
        }

        delay(1000) // Enough, since requests through the MockGithubService uses virtual time in delay
        Assert.assertEquals("loadContributorsChannels function should update results", true, resultUpdated)
    }
}
