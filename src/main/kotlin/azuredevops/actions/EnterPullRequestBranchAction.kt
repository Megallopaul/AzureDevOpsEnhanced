package azuredevops.actions

import azuredevops.model.PullRequest
import azuredevops.services.PullRequestBranchService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Action to enter (checkout) a Pull Request branch
 */
class EnterPullRequestBranchAction(
    private val pullRequest: PullRequest,
) : AnAction("Enter This Branch", "Checkout the source branch of this Pull Request", null) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val branchService = PullRequestBranchService.getInstance(project)
        branchService.enterPullRequestBranch(pullRequest)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && pullRequest.isActive()
    }
}
