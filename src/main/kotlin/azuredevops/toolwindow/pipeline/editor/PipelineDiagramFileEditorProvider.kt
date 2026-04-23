package azuredevops.toolwindow.pipeline.editor

import azuredevops.services.PipelineTabService
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Provider that creates a [PipelineDiagramFileEditor] for [PipelineDiagramVirtualFile] instances.
 */
class PipelineDiagramFileEditorProvider :
    FileEditorProvider,
    DumbAware {
    override fun accept(
        project: Project,
        file: VirtualFile,
    ): Boolean = file is PipelineDiagramVirtualFile

    override fun createEditor(
        project: Project,
        file: VirtualFile,
    ): FileEditor {
        val diagramFile = file as PipelineDiagramVirtualFile
        val tabService = PipelineTabService.getInstance(project)
        val build =
            tabService.getBuild(file)
                ?: throw IllegalStateException("Missing build data for diagram build #${diagramFile.buildId}")
        val timeline =
            tabService.getTimeline(file)
                ?: throw IllegalStateException("Missing timeline data for diagram build #${diagramFile.buildId}")
        return PipelineDiagramFileEditor(project, diagramFile, build, timeline)
    }

    override fun getEditorTypeId(): String = "azuredevops-pipeline-diagram"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}
