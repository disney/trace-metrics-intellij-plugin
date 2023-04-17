package com.disney.idea.components;

import javax.swing.*;

import com.disney.idea.actions.RefreshAction;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

/**
 * Preferences holder for Trace Metrics plugin, allowing update of current plugin
 * preferences by providing workflow around the {@link PreferencesPanel} settings
 * according to the IntelliJ Configurable interface.
 */
public class PluginPreferences implements Configurable {

    private PreferencesPanel preferencesPanel;
    private ApplicationPreferencesState applicationPreferences;
    private ProjectPreferencesState projectPreferences;

    public PluginPreferences(Project project) {
        applicationPreferences = ApplicationPreferencesState.getInstance();
        projectPreferences = ProjectPreferencesState.getInstance(project);
    }

    @Override
    public String getDisplayName() {
        return "Trace Metrics";
    }

    @Override
    public String getHelpTopic() {
        // No help is available.
        return null;
    }

    @Override
    public JComponent createComponent() {
        this.preferencesPanel = new PreferencesPanel(applicationPreferences, projectPreferences);
        return this.preferencesPanel;
    }

    @Override
    public boolean isModified() {
        return preferencesPanel.isModified();
    }

    @Override
    public void apply() {
        applicationPreferences.setNewRelicAccountId(preferencesPanel.getNewRelicAccountId());
        applicationPreferences.setNewRelicApiKey(preferencesPanel.getNewRelicApiKey());
        projectPreferences.setNewRelicAppName(preferencesPanel.getNewRelicAppName());
        projectPreferences.setNumDaysToQuery(preferencesPanel.getNumDays());
        projectPreferences.setUntilDateToQuery(preferencesPanel.getUntilDate());
        // Auto refresh after clicking Apply or OK.
        // Plug-ins may fetch an existing RefreshAction from the project or ActionManager but this didn't work.
        new RefreshAction().actionPerformed(new AnActionEvent(null, DataManager.getInstance().getDataContext(),
                ActionPlaces.UNKNOWN, new Presentation(),
                ActionManager.getInstance(), 0));
        reset();
    }

    @Override
    public void reset() {
        preferencesPanel.resetAllFields();
    }

    @Override
    public void disposeUIResources() {
        // Nothing here.s
    }
}
