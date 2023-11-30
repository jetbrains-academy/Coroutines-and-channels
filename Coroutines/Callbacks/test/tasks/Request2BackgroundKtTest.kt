package tasks

import contributors.MockGithubService
import contributors.expectedResults
import contributors.testRequestData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class Request2BackgroundKtTest {
    @Test
    fun testAggregation() = runBlocking{
        var resultsUpdated = false
        loadContributorsBackground(MockGithubService, testRequestData){
            Assert.assertEquals("List of contributors should be sorted " +
                    "by the number of contributions in a descending order",
                expectedResults.users, it)
            resultsUpdated = true
        }
        delay(1000) // Enough, since requests through the MockGithubService in this task do not simulate delay
        Assert.assertEquals("loadContributorsBackground function should update results", true, resultsUpdated)
    }
}