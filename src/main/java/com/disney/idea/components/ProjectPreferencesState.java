package com.disney.idea.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;

/**
 * Holds the IntelliJ IDEA preferences at the per-project level for the Trace Metrics plugin,
 * using IntelliJ's built-in persistence mechanisms.
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
@State(name = "ProjectPreferencesState")
public class ProjectPreferencesState implements PersistentStateComponent<ProjectPreferencesState.State> {

    private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static ProjectPreferencesState getInstance(Project project) {
        return project.getComponent(ProjectPreferencesState.class);
    }

    /**
     * Implements the IntelliJ standard preference persistence mechanism for project
     * level preferences.
     */
    public static class State {
        private String newRelicAppName;
        private String numDaysToQuery;
        private String untilDateToQuery;

        private static final String DEFAULT_NUM_DAYS_TO_QUERY = "1";

        public String getNewRelicAppName() {
            return newRelicAppName == null ? "" : newRelicAppName;
        }

        public void setNewRelicAppName(String newRelicAppName) {
            this.newRelicAppName = newRelicAppName.trim();
        }

        public String getNumDaysToQuery() {
            return numDaysToQuery == null ? DEFAULT_NUM_DAYS_TO_QUERY : numDaysToQuery;
        }

        public void setNumDaysToQuery(String numDaysToQuery) {
            try {
                // Only set if positive or zero
                if (Integer.parseInt(numDaysToQuery) >= 0){
                    this.numDaysToQuery = numDaysToQuery;
                }
            } catch (NumberFormatException e) {
                // throw away value, it's not a int
            }
        }

        public String getUntilDateToQuery() {
            return untilDateToQuery == null ? "" : untilDateToQuery;
        }

        public void setUntilDateToQuery(String untilDateToQuery) {
            // Only set if valid date
            if (isValidDateEntry(untilDateToQuery)) {
                this.untilDateToQuery = untilDateToQuery.trim();
            } else {
                // throw away value, it's not a valid date entry
            }
        }

        // A valid date is one that is valid and not in the future
        private boolean isValidDateEntry(String dateStr) {
            if (StringUtils.isNotBlank(dateStr) && dateStr != null) {
                sdf.setLenient(false);
                try {
                    Date parseDate = sdf.parse(dateStr);
                    if (parseDate.after(new Date())) {
                        return false;
                    }
                } catch (ParseException e) {
                    return false;
                }
            }
            return true;
        }
    }

    private State myState = new State();

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }

    public String getNewRelicAppName() {
        return myState.getNewRelicAppName();
    }

    public void setNewRelicAppName(String newRelicAppName) {
        myState.setNewRelicAppName(newRelicAppName);
    }

    public String getNumDaysToQuery() {
        return myState.getNumDaysToQuery();
    }

    public void setNumDaysToQuery(String numDaysToQuery) {
        myState.setNumDaysToQuery(numDaysToQuery);
    }

    public String getUntilDateToQuery() {
        return myState.getUntilDateToQuery();
    }

    public void setUntilDateToQuery(String untilDateToQuery) {
        myState.setUntilDateToQuery(untilDateToQuery);
    }

}