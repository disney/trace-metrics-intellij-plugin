package com.disney.idea.actions;

import com.disney.idea.components.PluginPreferences;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;

/**
 * Defines the menu action to configure Trace Metrics, which launches the settings dialog.
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class ConfigureAction extends AnAction {

    public ConfigureAction() {
        // Set the menu item name.
        super("Configure New Relic Application");
    }

    /**
     * Handles selecting the "Configure" menu item by opening the settings dialog in the context
     * of the current IntelliJ project.
     * @param event AnActionEvent instance for selecting Configure, containing project context.
     */
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, PluginPreferences.class);
    }

}
