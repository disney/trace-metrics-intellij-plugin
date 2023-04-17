package com.disney.idea.components;

import javax.swing.*;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;

/**
 * Per-project plugin UI component defining the actions available for the
 * Trace Metrics plugin.
 *
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class TraceToolbar {

    private static final String CONFIGURE_ACTION = "TraceMetrics.Configure";
    private static final String REFRESH_ACTION = "TraceMetrics.Refresh";
    private ActionToolbar actionToolbar;

    public TraceToolbar(){
        actionToolbar = createActionGroupToolbar();
    }

    public ActionToolbar getActionToolbar(){
        return actionToolbar;
    }

    public static TraceToolbar getInstance(Project project){
        return project.getComponent(TraceToolbar.class);
    }

    private ActionToolbar createActionGroupToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction configureAction = actionManager.getAction(CONFIGURE_ACTION);
        AnAction refreshAction = actionManager.getAction(REFRESH_ACTION);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(configureAction);
        actionGroup.addSeparator();
        actionGroup.add(refreshAction);
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, false);
        actionToolbar.setOrientation(SwingConstants.VERTICAL);
        return actionToolbar;
    }
}
