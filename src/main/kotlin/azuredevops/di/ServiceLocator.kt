package azuredevops.di

import azuredevops.services.*
import azuredevops.toolwindow.review.PrReviewToolWindow
import com.intellij.openapi.project.Project

/**
 * Service Locator / Composition Root for Dependency Injection
 *
 * This object serves as the central point for creating and managing service instances.
 * It bridges the gap between IntelliJ's service container and our manual DI pattern.
 *
 * Usage:
 * - For tool windows: ServiceLocator.createPrReviewToolWindow(project)
 * - For services: Use IntelliJ's service container directly
 *
 * Pattern: Manual Constructor Injection
 * - Services receive dependencies via constructor parameters
 * - Tool windows are created through factory methods
 * - No getInstance() calls inside business logic classes
 */
object ServiceLocator {
    // region API Client

    /**
     * Get or create AzureDevOpsApiClient instance
     * Uses IntelliJ's service container for singleton management
     */
    fun getApiClient(project: Project): AzureDevOpsApiClient =
        AzureDevOpsApiClient.getInstance(project)

    // endregion

    // region Services

    /**
     * Get PullRequestCommentsService instance
     */
    fun getCommentsService(project: Project): PullRequestCommentsService =
        PullRequestCommentsService.getInstance(project)

    /**
     * Get PrReviewStateService instance
     */
    fun getReviewStateService(project: Project): PrReviewStateService =
        PrReviewStateService.getInstance(project)

    /**
     * Get AvatarService instance
     */
    fun getAvatarService(project: Project): AvatarService =
        AvatarService.getInstance(project)

    /**
     * Get AzureDevOpsConfigService instance
     */
    fun getConfigService(project: Project): AzureDevOpsConfigService =
        AzureDevOpsConfigService.getInstance(project)

    // endregion

    // region Tool Window Factories

    /**
     * Creates a new PrReviewToolWindow instance with all dependencies injected
     *
     * @param project Current IntelliJ project
     * @param showSelector Whether to show the PR selector dropdown
     * @return Fully initialized PrReviewToolWindow
     */
    fun createPrReviewToolWindow(
        project: Project,
        showSelector: Boolean = true,
    ): PrReviewToolWindow {
        val apiClient = getApiClient(project)
        val reviewStateService = getReviewStateService(project)

        return PrReviewToolWindow(
            project = project,
            apiClient = apiClient,
            reviewStateService = reviewStateService,
            showSelector = showSelector,
        )
    }

    /**
     * Creates a new DiffViewerPanel instance with all dependencies injected
     *
     * @param project Current IntelliJ project
     * @param pullRequestId ID of the pull request
     * @param apiClient Azure DevOps API client
     * @param externalProjectName Optional external project name
     * @param externalRepositoryId Optional external repository ID
     * @return Fully initialized DiffViewerPanel
     */
    fun createDiffViewerPanel(
        project: Project,
        pullRequestId: Int,
        apiClient: AzureDevOpsApiClient,
        externalProjectName: String? = null,
        externalRepositoryId: String? = null,
    ): azuredevops.toolwindow.review.DiffViewerPanel =
        azuredevops.toolwindow.review.DiffViewerPanel(
            project = project,
            pullRequestId = pullRequestId,
            apiClient = apiClient,
            externalProjectName = externalProjectName,
            externalRepositoryId = externalRepositoryId,
        )

    // endregion
}
