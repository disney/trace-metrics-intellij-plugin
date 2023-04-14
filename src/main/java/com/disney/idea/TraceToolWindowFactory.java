package com.disney.idea;

import com.disney.idea.components.TraceDataTable;
import com.disney.idea.components.TraceToolbar;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * Essential per-project UI setup hooks called by IntelliJ plugin framework.
 *
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class TraceToolWindowFactory implements ToolWindowFactory {

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        SimpleToolWindowPanel myJPanel = new SimpleToolWindowPanel(false, false);

        // toolbar
        ActionToolbar myActionToolbar = TraceToolbar.getInstance(project).getActionToolbar();
        myJPanel.setToolbar(myActionToolbar.getComponent());

        // table in scroll pane
        JTable table = TraceDataTable.getInstance(project).getTable();
        JScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, JBColor.border()));

        myJPanel.add(scrollPane);
        Content tableContent = contentFactory.createContent(myJPanel, "", false);
        toolWindow.getContentManager().addContent(tableContent);
    }
}
