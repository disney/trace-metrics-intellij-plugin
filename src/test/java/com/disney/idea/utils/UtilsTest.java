package com.disney.idea.utils;

import org.junit.After;

import com.disney.idea.components.ProjectPreferencesState;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class UtilsTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final String METRIC_NAME = "some-metric-name";
    private static final String DEFAULT_NUM_DAYS = "1";

    @After
    public void testResetToDefaults() {
        Project project = myFixture.getProject();
        ProjectPreferencesState projectPreferencesState = ProjectPreferencesState.getInstance(project);
        projectPreferencesState.setNumDaysToQuery(DEFAULT_NUM_DAYS);
        projectPreferencesState.setUntilDateToQuery("");
    }

    public void testGetProject() {
        Project expectedProject = myFixture.getProject();
        Project actualProject = Utils.getProject();
        assertNotNull("The platform fixture should provide a Project", actualProject);
        assertEquals(expectedProject, actualProject);
    }

    public void testGetNewRelicUrl() throws Exception {
        //setup
        final String nrInsightsPrefix = "https://insights.newrelic.com/accounts/";
        String expectedNumDaysClause = getNumDaysClause("1").toLowerCase();

        //execute
        String newRelicURIAsString = Utils.getNewRelicUrl(METRIC_NAME).toString().toLowerCase();

        //verify
        assertTrue("NR query URI should contain metric named: " + METRIC_NAME,
                newRelicURIAsString.contains(METRIC_NAME));
        assertTrue("NR query URI should start with NR Insights prefix: " + nrInsightsPrefix,
                newRelicURIAsString.startsWith(nrInsightsPrefix));
        assertTrue(String.format("NR query URI should contain: '%s' but found: '%s'", expectedNumDaysClause,
                newRelicURIAsString), newRelicURIAsString.contains(expectedNumDaysClause));

        //update num days to query
        Project project = myFixture.getProject();
        ProjectPreferencesState.getInstance(project).setNumDaysToQuery("7");
        expectedNumDaysClause = getNumDaysClause("7");

        //execute
        newRelicURIAsString = Utils.getNewRelicUrl(METRIC_NAME).toString().toLowerCase();

        //verify
        assertTrue(String.format("NR query URI should contain: '%s' but found: '%s'", expectedNumDaysClause,
                newRelicURIAsString), newRelicURIAsString.contains(expectedNumDaysClause));

        //update until date
        project = myFixture.getProject();
        ProjectPreferencesState.getInstance(project).setUntilDateToQuery("2020-11-03");
        expectedNumDaysClause = getUntilDateClause("2020-10-27", "2020-11-03");

        //execute
        newRelicURIAsString = Utils.getNewRelicUrl(METRIC_NAME).toString().toLowerCase();

        //verify
        assertTrue(String.format("NR query URI should contain: '%s' but found: '%s'", expectedNumDaysClause,
                newRelicURIAsString), newRelicURIAsString.contains(expectedNumDaysClause));
    }

    private String getNumDaysClause(String numDays) {
        return String.format("since+%s+days+ago", numDays);
    }

    private String getUntilDateClause(String sinceDate, String untilDate) {
        return String.format("since+%%27%s%%27+until+%%27%s%%27", sinceDate, untilDate);
    }
}
