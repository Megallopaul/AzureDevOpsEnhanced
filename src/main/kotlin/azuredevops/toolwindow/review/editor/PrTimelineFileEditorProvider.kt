package azuredevops.toolwindow.review.editor

import azuredevops.services.PrReviewTabService
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Provider that creates a [PrTimelineFileEditor] for [PrTimelineVirtualFile] instances.
 */
class PrTimelineFileEditorProvider :
    FileEditorProvider,
    DumbAware {
    override fun accept(
        project: Project,
        file: VirtualFile,
    ): Boolean = file is PrTimelineVirtualFile

    override fun createEditor(
        project: Project,
        file: VirtualFile,
    ): FileEditor {
        val timelineFile = file as PrTimelineVirtualFile
        val pullRequest =
            PrReviewTabService.getInstance(project).getPullRequest(file)
                ?: throw IllegalStateException("Missing pull request data for timeline PR #${timelineFile.pullRequestId}")
        return PrTimelineFileEditor(project, file, pullRequest)
    }

    override fun getEditorTypeId(): String = "azuredevops-pr-timeline"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}
