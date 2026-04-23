package azuredevops.util

/**
 * Azure DevOps API endpoint constants.
 * Centralizes all API endpoint paths to avoid magic strings.
 */
object AzureDevOpsEndpoints {
    // Git/Pull Request endpoints
    const val PULL_REQUESTS = "/pullrequests"
    const val PULL_REQUEST_THREADS = "/pullrequests/{id}/threads"
    const val PULL_REQUEST_THREAD_COMMENTS = "/pullrequests/{pullRequestId}/threads/{threadId}/comments"
    const val PULL_REQUEST_CHANGES = "/pullrequests/{id}/iterations/{iterationId}/changes"
    const val PULL_REQUEST_COMMITS = "/pullrequests/{id}/commits"
    const val PULL_REQUEST_POLICY_EVALUATIONS = "/pullrequests/{id}/policyevaluations"

    // Work Item Tracking endpoints
    const val WORK_ITEMS = "/workitems"
    const val WORK_ITEM_TYPES = "/workitemtypes"
    const val WORK_ITEM_COMMENTS = "/workitems/{id}/comments"
    const val WIQL_QUERY = "/wiql"

    // Build/Pipeline endpoints
    const val BUILDS = "/builds"
    const val BUILD_DEFINITIONS = "/definitions"
    const val BUILD_TIMELINE = "/builds/{id}/timeline"
    const val BUILD_LOGS = "/builds/{id}/logs"

    // Core endpoints
    const val CONNECTION_DATA = "/connectionData"
    const val IDENTITIES = "/identities"
}

/**
 * API parameter constants.
 * Centralizes all API parameter values to avoid magic strings.
 */
object ApiParameters {
    // API Versions
    const val API_VERSION_7_0 = "7.0"
    const val API_VERSION_7_1_PREVIEW = "7.1-preview.1"
    const val API_VERSION_7_0_PREVIEW_4 = "7.0-preview.4"

    // Pull Request Status
    const val PR_STATUS_ACTIVE = "active"
    const val PR_STATUS_COMPLETED = "completed"
    const val PR_STATUS_ABANDONED = "abandoned"
    const val PR_STATUS_ALL = "all"

    // Build Status
    const val BUILD_STATUS_IN_PROGRESS = "inProgress"
    const val BUILD_STATUS_COMPLETED = "completed"
    const val BUILD_STATUS_CANCELLED = "cancelled"
    const val BUILD_STATUS_ALL = "all"

    // Build Result
    const val BUILD_RESULT_SUCCEEDED = "succeeded"
    const val BUILD_RESULT_FAILED = "failed"
    const val BUILD_RESULT_PARTIALLY_SUCCEEDED = "partiallySucceeded"
    const val BUILD_RESULT_CANCELLED = "cancelled"

    // Thread Status (numeric values for API)
    const val THREAD_STATUS_UNKNOWN = 0
    const val THREAD_STATUS_ACTIVE = 1
    const val THREAD_STATUS_FIXED = 2
    const val THREAD_STATUS_WONT_FIX = 3
    const val THREAD_STATUS_CLOSED = 4
    const val THREAD_STATUS_BY_DESIGN = 5
    const val THREAD_STATUS_PENDING = 6

    // Comment Types
    const val COMMENT_TYPE_SYSTEM = "system"
    const val COMMENT_TYPE_USER = 1

    // WIQL Constants
    const val WIQL_PROJECT_PLACEHOLDER = "@project"
    const val WIQL_USER_PLACEHOLDER = "@Me"

    // Expansion Options
    const val EXPAND_ALL = "All"

    // HTTP Headers
    const val HEADER_ACCEPT_JSON = "application/json"
    const val HEADER_CONTENT_TYPE_JSON = "application/json"
    const val HEADER_CONTENT_TYPE_JSON_PATCH = "application/json-patch+json"

    // URL Patterns
    const val VISUAL_STUDIO_DOMAIN = "visualstudio.com"
    const val DEV_AZURE_DOMAIN = "dev.azure.com"
}

/**
 * Error message constants.
 */
object ErrorMessages {
    const val AUTH_REQUIRED = """Authentication required. Please login:
1. Go to File → Settings → Tools → Azure DevOps Accounts
2. Click 'Add' button to add your account
3. Complete the authentication in your browser

The plugin will automatically use your authenticated account for this repository."""

    const val NO_GIT_REPO = "No Git repository found in this project."
    const val NOT_AZURE_DEVOPS = "Azure DevOps is not configured for this project."
    const val NO_ACTIVE_BRANCH = "No active Git branch."
    const val NO_PULL_REQUEST = "The branch does not have an active Pull Request."
}
