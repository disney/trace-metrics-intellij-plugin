package com.disney.idea.components;

import javax.swing.*;

import org.junit.Assert;

import com.disney.idea.utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class TraceDataTableTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testDataTable() {
        Project project = Utils.getProject();
        TraceDataTable traceDataTable = new TraceDataTable(project);
        Assert.assertNotNull("traceDataTable should not be null", traceDataTable);
        JTable jtable = traceDataTable.getTable();
        Assert.assertNotNull("A dataTable should be created", jtable);
        Assert.assertEquals("DataTable only allows single select", ListSelectionModel.SINGLE_SELECTION, jtable.getSelectionModel().getSelectionMode());

        JPopupMenu menu = jtable.getComponentPopupMenu();
        Assert.assertNotNull("A popupMenu should be created in dataTable", menu);
        JMenuItem openBrowser = (JMenuItem) menu.getComponent(0);
        Assert.assertEquals("Open in Browser", openBrowser.getText());
    }
}