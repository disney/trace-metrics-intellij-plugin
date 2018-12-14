package com.disney.idea.utils;

import com.disney.idea.components.ProjectPreferencesState;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.net.URI;

import org.junit.After;

public class UtilsTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final String METRIC_NAME = "some-metric-name";
    private static final Integer DEFAULT_NUM_DAYS = 1;

    @After
    public void testResetToDefaults() {
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);
        projectPreferencesState.setNumDaysToQuery(Integer.toString(DEFAULT_NUM_DAYS));
    }

    public void testGetProject() {
        Project expectedProject = myFixture.getProject();
        Project actualProject = Utils.getProject();
        assertNotNull("The platform fixture should provide a Project", actualProject);
        assertEquals(expectedProject, actualProject);
    }

    public void testGetNewRelicUrl() throws Exception {
        //setup
        final String nrInsightsPrefix="https://insights.newrelic.com/accounts/";
        String expectedNumDaysClause = getNumDaysClause("1");

        //execute
        URI newRelicURI = Utils.getNewRelicUrl(METRIC_NAME);

        //verify
        assertTrue("NR query URI should contain metric named: "+ METRIC_NAME,
                newRelicURI.toString().contains(METRIC_NAME));
        assertTrue("NR query URI should start with NR Insights prefix: " +nrInsightsPrefix,
                newRelicURI.toString().startsWith(nrInsightsPrefix));
        assertTrue(String.format("NR query URI should contain: '%s' but found: '%s'", expectedNumDaysClause, newRelicURI.toString()),
                newRelicURI.toString().contains(expectedNumDaysClause));

        //update num days to query
        Project project = myFixture.getProject();
        ProjectPreferencesState.getInstance(project).setNumDaysToQuery("7");
        expectedNumDaysClause = getNumDaysClause("7");

        //execute
        newRelicURI = Utils.getNewRelicUrl(METRIC_NAME);

        //verify
        assertTrue(String.format("NR query URI should contain: '%s' but found: '%s'", expectedNumDaysClause, newRelicURI.toString()),
                newRelicURI.toString().contains(expectedNumDaysClause));
    }

    private String getNumDaysClause(String numDays) {
        return String.format("since+%s+days+ago", numDays);
    }
}
