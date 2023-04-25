package tasks

import contributors.MockGithubService
import contributors.concurrentProgressResults
import contributors.testRequestData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class Request7ChannelsKtTest {
    @Test
    fun testChannels() = runBlocking {
        val startTime = System.currentTimeMillis()
        var index = 0
        loadContributorsChannels(MockGithubService, testRequestData) {
                users, _ ->
            val expected = concurrentProgressResults[index++]
            val time = System.currentTimeMillis() - startTime
            /*
            // TODO: uncomment this assertion
            Assert.assertEquals("Expected intermediate result after virtual ${expected.timeFromStart} ms:",
                expected.timeFromStart, time)
            */
            Assert.assertEquals("Wrong intermediate result after $time:", expected.users, users)
        }
    }

    @Test
    fun testChannels1() = runTest {
        val startTime = currentTime
        var index = 0
        loadContributorsChannels(MockGithubService, testRequestData) { users, _ ->
            val expected = concurrentProgressResults[index++]
            val time = currentTime - startTime
            Assert.assertEquals(
                "Expected intermediate results after ${expected.timeFromStart} ms:",
                expected.timeFromStart, time
            )
            Assert.assertEquals("Wrong intermediate results after $time:", expected.users, users)
        }
    }
}
