package tasks

import contributors.MockGithubService
import contributors.progressResults
import contributors.testRequestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import tasks.loadContributorsProgress

class Request6ProgressKtTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testProgress() = runTest {
        val startTime = currentTime
        var index = 0
        var progressUpdated = false
        loadContributorsProgress(MockGithubService, testRequestData) {
            users, _ ->
            val expected = progressResults[index++]
            val time = currentTime - startTime
            progressUpdated = true

            Assert.assertEquals("Wrong intermediate result after $time:", expected.users, users)
        }

        delay(1000) // Enough, since requests through the MockGithubService uses virtual time in delay
        Assert.assertEquals("loadContributorsProgress function should update results", true, progressUpdated)
    }
}