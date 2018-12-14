package com.disney.idea.components;

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

    public static ProjectPreferencesState getInstance(Project project) {
        return project.getComponent(ProjectPreferencesState.class);
    }

    /**
     * Implements the IntelliJ standard preference persistence mechanism for project
     * level preferences.
     */
    public static class State {
        private String newRelicAppName;
        private Integer numDaysToQuery;

        private static final int DEFAULT_NUM_DAYS_TO_QUERY = 1;

        public String getNewRelicAppName() {
            return newRelicAppName == null ? "" : newRelicAppName;
        }

        public void setNewRelicAppName(String newRelicAppName) {
            this.newRelicAppName = newRelicAppName.trim();
        }

        public Integer getNumDaysToQuery() {
            return numDaysToQuery == null ? DEFAULT_NUM_DAYS_TO_QUERY : numDaysToQuery;
        }

        public void setNumDaysToQuery(String numDaysToQuery) {
            try {
                int num = Integer.parseInt(numDaysToQuery);
                // throw away if it's negative
                if (num >= 0){
                    this.numDaysToQuery = num;
                }
            } catch (NumberFormatException e) {
                // throw away value, it's not a int
            }
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

    public Integer getNumDaysToQuery() {
        return myState.getNumDaysToQuery();
    }

    public void setNumDaysToQuery(String numDaysToQuery) {
        myState.setNumDaysToQuery(numDaysToQuery);
    }
}