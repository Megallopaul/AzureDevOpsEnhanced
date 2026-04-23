package azuredevops.api

/**
 * Information detected from an Azure DevOps repository
 */
data class AzureDevOpsRepoInfo(
    val organization: String,
    val project: String,
    val repository: String,
    val remoteUrl: String,
    val useVisualStudioDomain: Boolean = false,
    val selfHostedUrl: String? = null,
) {
    fun isValid(): Boolean =
        organization.isNotBlank() &&
            project.isNotBlank() &&
            repository.isNotBlank()

    fun isSelfHosted(): Boolean = selfHostedUrl != null
}

/**
 * Service interface for detecting Azure DevOps repositories.
 *
 * Responsible for:
 * - Detecting if a Git repository is hosted on Azure DevOps
 * - Extracting organization, project, and repository from remote URLs
 * - Supporting both cloud (dev.azure.com) and self-hosted instances
 */
interface RepoDetector {
    /**
     * Detects if the current repository is an Azure DevOps repository.
     *
     * @return true if the repository is hosted on Azure DevOps
     */
    fun isAzureDevOpsRepository(): Boolean

    /**
     * Automatically detects Azure DevOps information from the remote URL.
     * Results are cached for 30 seconds to avoid repeated parsing.
     *
     * @return AzureDevOpsRepoInfo if detected, null otherwise
     */
    fun detectAzureDevOpsInfo(): AzureDevOpsRepoInfo?

    /**
     * Gets the detected repository information.
     * Alias for detectAzureDevOpsInfo().
     *
     * @return AzureDevOpsRepoInfo if detected, null otherwise
     */
    fun getRepoInfo(): AzureDevOpsRepoInfo?
}
