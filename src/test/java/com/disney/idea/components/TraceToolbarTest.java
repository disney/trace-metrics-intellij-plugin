package com.disney.idea.components;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.disney.idea.actions.ConfigureAction;
import com.disney.idea.actions.RefreshAction;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class TraceToolbarTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testTraceToolbar() {
        List<Class> expectedClasses = Arrays.asList(ConfigureAction.class, Separator.class, RefreshAction.class);
        TraceToolbar traceToolbar = new TraceToolbar();
        Assert.assertNotNull(traceToolbar);
        ActionToolbar actionToolbar = traceToolbar.getActionToolbar();
        Assert.assertNotNull(actionToolbar);
        Assert.assertTrue(actionToolbar.hasVisibleActions());
        Assert.assertEquals(4, actionToolbar.getActions().size());
        Assert.assertEquals(expectedClasses.get(0), actionToolbar.getActions().get(0).getClass());
        Assert.assertEquals(expectedClasses.get(1), actionToolbar.getActions().get(1).getClass());
        Assert.assertEquals(expectedClasses.get(2), actionToolbar.getActions().get(2).getClass());
    }

}