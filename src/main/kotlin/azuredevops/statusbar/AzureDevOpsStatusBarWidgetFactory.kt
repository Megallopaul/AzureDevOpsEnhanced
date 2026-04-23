package azuredevops.statusbar

import azuredevops.services.AzureDevOpsConfigService
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class AzureDevOpsStatusBarWidgetFactory : StatusBarWidgetFactory {
    companion object {
        const val WIDGET_ID = "AzureDevOps.StatusBar"
    }

    override fun getId(): String = WIDGET_ID

    override fun getDisplayName(): String = "Azure DevOps"

    override fun isAvailable(project: Project): Boolean = AzureDevOpsConfigService.getInstance(project).isAzureDevOpsRepository()

    override fun createWidget(project: Project): StatusBarWidget = AzureDevOpsStatusBarWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
