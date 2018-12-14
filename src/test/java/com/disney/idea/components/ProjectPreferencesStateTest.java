package com.disney.idea.components;

import org.junit.After;
import org.junit.Assert;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ProjectPreferencesStateTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final int DEFAULT_NUM_DAYS_TO_QUERY = 1;

    @After
    public void testResetToDefaults() {
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);
        projectPreferencesState.setNumDaysToQuery(Integer.toString(DEFAULT_NUM_DAYS_TO_QUERY));
    }

    public void testProjectPreferencesState_setAndGetNumDaysToQuery() {
        //setup
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);

        //verify default value
        Assert.assertEquals(Integer.valueOf(DEFAULT_NUM_DAYS_TO_QUERY), projectPreferencesState.getNumDaysToQuery());

        //update value and verify
        projectPreferencesState.setNumDaysToQuery("99");
        Assert.assertEquals(Integer.valueOf(99), projectPreferencesState.getNumDaysToQuery());

        //attempt update to invalid values and verify
        projectPreferencesState.setNumDaysToQuery("-88");
        Assert.assertEquals(Integer.valueOf(99), projectPreferencesState.getNumDaysToQuery());
        projectPreferencesState.setNumDaysToQuery("1.5");
        Assert.assertEquals(Integer.valueOf(99), projectPreferencesState.getNumDaysToQuery());
        projectPreferencesState.setNumDaysToQuery("not a number");
        Assert.assertEquals(Integer.valueOf(99), projectPreferencesState.getNumDaysToQuery());

        //update to 0 and verify
        projectPreferencesState.setNumDaysToQuery("0");
        Assert.assertEquals(Integer.valueOf(0), projectPreferencesState.getNumDaysToQuery());
    }

}