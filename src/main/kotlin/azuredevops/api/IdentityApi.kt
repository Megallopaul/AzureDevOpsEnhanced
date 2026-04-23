package azuredevops.api

import azuredevops.model.Identity
import azuredevops.services.AzureDevOpsApiException

/**
 * API interface for Identity/User operations
 */
interface IdentityApi {
    /**
     * Searches for identities (users) matching the search text.
     * Strategy: Gets users from recent PRs (createdBy + reviewers) since direct
     * identity API may require additional permissions.
     * @param searchText Text to search for (name, email, etc.)
     * @return List of matching Identity objects
     * @throws AzureDevOpsApiException on API failure
     */
    @Throws(AzureDevOpsApiException::class)
    fun searchIdentities(searchText: String): List<Identity>
}
