package contributors

import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls

object MockGithubService : GitHubService {
    override fun getOrgReposCall(org: String): Call<List<Repo>> {
        return Calls.response(repos)
    }

    override fun getRepoContributorsCall(owner: String, repo: String): Call<List<User>> {
        return Calls.response(reposMap.getValue(repo).users)
    }

}