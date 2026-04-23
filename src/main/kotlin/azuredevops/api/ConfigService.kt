package azuredevops.api

import azuredevops.model.AzureDevOpsConfig

/**
 * Service interface for managing Azure DevOps configuration.
 *
 * Responsible for:
 * - Getting the current configuration (organization, project, repository, PAT)
 * - Saving configuration (manual overrides)
 * - Clearing credentials
 *
 * This service combines auto-detected values from RepoDetector with user-provided
 * manual overrides. PAT is stored securely via AuthenticationService.
 */
interface ConfigService {
    /**
     * Gets the complete Azure DevOps configuration.
     * Combines auto-detected values with manual overrides.
     * PAT is retrieved from secure storage.
     *
     * @return Complete AzureDevOpsConfig
     */
    fun getConfig(): AzureDevOpsConfig

    /**
     * Saves the complete configuration.
     * Manual overrides are persisted, PAT is stored securely.
     *
     * @param config Configuration to save
     */
    fun saveConfig(config: AzureDevOpsConfig)

    /**
     * Clears all stored credentials (PAT/token).
     * Manual configuration (organization, project, repository) is preserved.
     */
    fun clearCredentials()

    /**
     * Checks if the service is properly configured.
     *
     * @return true if organization, project, repository, and PAT are all set
     */
    fun isConfigured(): Boolean

    /**
     * Gets the API base URL for the current configuration.
     * Handles both cloud (dev.azure.com) and self-hosted instances.
     *
     * @return API base URL
     */
    fun getApiBaseUrl(): String
}
