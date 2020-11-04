package com.disney.idea.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.disney.idea.client.NewRelicClient;
import com.disney.idea.components.ApplicationPreferencesState;
import com.disney.idea.components.ProjectPreferencesState;
import com.disney.idea.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

/**
 * Defines the menu action to refresh New Relic metrics for the current project
 * by creating a background task to query the remote New Relic server and update
 * the data model of the UI table displaying the trace metrics.
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class RefreshAction extends AnAction {

    public RefreshAction() {
        // Set the menu item name.
        super("Run or Refresh New Relic Query");
    }

    /**
     * Handles the refresh action event by making a remote call to New Relic's query
     * API as configured for the project and updating the project's metrics table with
     * the newly fetched metric data.
     * @param event the AnActionEvent instance containing a reference to the active IntelliJ project
     */
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Trace Metrics") {
            public void run(ProgressIndicator indicator) {
                indicator.setText("Running New Relic query");

                // call new relic
                Map<String, Long> traceCounts = callNewRelic(project, indicator);

                // refresh the table content from the query
                Utils.refreshCounts(traceCounts);
            }
        });
    }

    /**
     * Calls the New Relic API server with the values configured for this IntelliJ
     * project, retrieving query results and parsing them into a Map whose keys
     * are metric names and the values are counts for that metric.
     * @param project the IntelliJ project for which metrics will be fetched
     * @return a Map of metric name to count for that metric, or an empty Map if any configuration is missing.
     */
    public static Map<String, Long> callNewRelic(Project project, ProgressIndicator indicator){
        ApplicationPreferencesState applicationPreferences = ApplicationPreferencesState.getInstance();
        ProjectPreferencesState projectPreferences = ProjectPreferencesState.getInstance(project);
        String accountId = applicationPreferences.getNewRelicAccountId();
        String apiKey = applicationPreferences.getNewRelicApiKey();
        String appName = projectPreferences.getNewRelicAppName();
        String numDays = projectPreferences.getNumDaysToQuery();
        String untilDate = projectPreferences.getUntilDateToQuery();

        NewRelicClient client = getClient(accountId, apiKey, appName, numDays, untilDate);
        return client == null ? new HashMap<>() : client.query(indicator);
    }

    /**
     * Construct a new relic client for API requests.
     * @param accountId the New Relic account to query
     * @param apiKey    the New Relic API key for executing programmatic queries
     * @param appName   the name of the application to query in New Relic
     * @param numDays   the number of days back to query
     * @param untilDate the date to query back from (set to today if blank)
     * @return          a fully configured NewRelicClient ready for running an API query,
     *                  or null if the preferences are invalid such that query will not return any results.
     * */
    protected static NewRelicClient getClient(String accountId, String apiKey, String appName, String numDays, String untilDate) {
        if (StringUtils.isNotBlank(apiKey) && StringUtils.isNotBlank(accountId) && StringUtils.isNotBlank(appName) && Integer.parseInt(numDays) > 0) {
            String url = NewRelicClient.getApiUrl(accountId);
            return new NewRelicClient(apiKey, url, appName, numDays, untilDate);
        }
        return null;
    }

}
