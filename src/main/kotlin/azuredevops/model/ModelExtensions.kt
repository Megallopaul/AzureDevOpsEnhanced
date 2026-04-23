package azuredevops.model

// region CommentThread Extensions

/**
 * Checks if a comment thread is active.
 * A thread is active if its status is Active or Pending.
 */
fun CommentThread.isActive(): Boolean =
    status == ThreadStatus.Active || status == ThreadStatus.Pending

/**
 * Checks if a comment thread is resolved.
 * A thread is resolved if it's not active.
 */
fun CommentThread.isResolved(): Boolean = !isActive()

/**
 * Checks if a comment thread is system-generated.
 * System threads typically have commentType = "system" or no human author.
 */
fun CommentThread.isSystemGenerated(): Boolean {
    val firstComment = comments?.firstOrNull() ?: return false
    return firstComment.commentType == "system" || firstComment.author == null
}

/**
 * Gets the display status for a comment thread.
 */
fun CommentThread.getDisplayStatus(): String =
    when {
        isResolved() -> "✓ Resolved"
        status == ThreadStatus.Pending -> "⏳ Pending"
        else -> "⚠ Active"
    }

// endregion

// region PullRequest Extensions

/**
 * Checks if a PR is active.
 */
fun PullRequest.isActive(): Boolean = status == PullRequestStatus.Active

/**
 * Checks if a PR is merged/completed.
 */
fun PullRequest.isMerged(): Boolean = status == PullRequestStatus.Completed

/**
 * Checks if a PR is abandoned.
 */
fun PullRequest.isAbandoned(): Boolean = status == PullRequestStatus.Abandoned

/**
 * Checks if a PR is created by the current user.
 */
fun PullRequest.isCreatedByUser(currentUserId: String?): Boolean =
    currentUserId != null && createdBy?.id == currentUserId

// endregion

// region TeamIteration Extensions

/**
 * Gets the formatted display name for an iteration.
 * Prefixes with "* " if it's the current iteration.
 */
fun TeamIteration.getDisplayName(): String =
    if (isCurrent()) "* $name" else name ?: ""

// endregion
