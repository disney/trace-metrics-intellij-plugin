package com.disney.idea.components;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ProjectPreferencesStateTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final String DEFAULT_NUM_DAYS_TO_QUERY = "1";

    @After
    public void testResetToDefaults() {
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);
        projectPreferencesState.setNumDaysToQuery(DEFAULT_NUM_DAYS_TO_QUERY);
    }

    public void testProjectPreferencesState_setAndGetNumDaysToQuery() {
        //setup
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);

        //verify default value
        Assert.assertEquals(DEFAULT_NUM_DAYS_TO_QUERY, projectPreferencesState.getNumDaysToQuery());

        //update value and verify
        projectPreferencesState.setNumDaysToQuery("99");
        Assert.assertEquals("99", projectPreferencesState.getNumDaysToQuery());

        //attempt update to invalid values and verify
        projectPreferencesState.setNumDaysToQuery("-88");
        Assert.assertEquals("99", projectPreferencesState.getNumDaysToQuery());
        projectPreferencesState.setNumDaysToQuery("1.5");
        Assert.assertEquals("99", projectPreferencesState.getNumDaysToQuery());
        projectPreferencesState.setNumDaysToQuery("not a number");
        Assert.assertEquals("99", projectPreferencesState.getNumDaysToQuery());

        //update to 0 and verify
        projectPreferencesState.setNumDaysToQuery("0");
        Assert.assertEquals("0", projectPreferencesState.getNumDaysToQuery());
    }

    public void testProjectPreferencesState_setAndGetUntilDateToQuery() {
        //setup
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);

        //verify default value
        Assert.assertEquals("", projectPreferencesState.getUntilDateToQuery());

        //update value and verify
        projectPreferencesState.setUntilDateToQuery("2020-11-03");
        Assert.assertEquals("2020-11-03", projectPreferencesState.getUntilDateToQuery());

        //attempt update to invalid values and verify
        String futureDate = LocalDate.from(new Date().toInstant().atZone(ZoneId.of("UTC"))).plusDays(1).toString();
        projectPreferencesState.setUntilDateToQuery(futureDate);
        Assert.assertEquals("2020-11-03", projectPreferencesState.getUntilDateToQuery());
        projectPreferencesState.setUntilDateToQuery("01-01-1999");
        Assert.assertEquals("2020-11-03", projectPreferencesState.getUntilDateToQuery());
        projectPreferencesState.setUntilDateToQuery("not a date");
        Assert.assertEquals("2020-11-03", projectPreferencesState.getUntilDateToQuery());

        //update to 0 and verify
        projectPreferencesState.setUntilDateToQuery("");
        Assert.assertEquals("", projectPreferencesState.getUntilDateToQuery());
    }

}