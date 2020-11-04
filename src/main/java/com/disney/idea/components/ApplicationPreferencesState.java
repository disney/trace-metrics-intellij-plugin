package com.disney.idea.components;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

/**
 * Holds the IntelliJ IDEA preferences at the top level (plugin application) for the Trace Metrics plugin,
 * using IntelliJ's built-in persistence mechanisms, including PasswordSafe for the New Relic API key.
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
@State(name = "ApplicationPreferencesState", storages = { @Storage(file = "$APP_CONFIG$/configProvider.xml") })
public class ApplicationPreferencesState implements PersistentStateComponent<ApplicationPreferencesState.State> {

    private static final String PROJECT_NAME = "IntelliJ Trace Metrics Plugin";
    private static final String NR_API_KEY = "API Key";

    public static ApplicationPreferencesState getInstance(){
        return ApplicationManager.getApplication().getComponent(ApplicationPreferencesState.class);
    }

    /**
     * Implements the IntelliJ standard preference persistence mechanism for application
     * level preferences excluding the New Relic API Key.
     */
    public static class State {
        private String newRelicAccountId;

        public String getNewRelicAccountId() {
            return newRelicAccountId == null ? "" : newRelicAccountId;
        }

        public void setNewRelicAccountId(String newRelicAccountId) {
            this.newRelicAccountId = newRelicAccountId.trim();
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

    /**
     * Fetches the value of the New Relic account ID from the state managed by
     * the persistent preference store.
     * @return the currently configured New Relic account ID
     */
    public String getNewRelicAccountId() {
        return myState.getNewRelicAccountId();
    }

    /**
     * Updates the value of the New Relic account ID in the persistent preference store.
     * @param newRelicAccountId
     */
    public void setNewRelicAccountId(String newRelicAccountId) {
        myState.setNewRelicAccountId(newRelicAccountId.trim());
    }

    /**
     * Fetches the New Relic API Key from the secure PasswordSafe persistence store.
     * @return the currently configured New Relic API key
     */
    public String getNewRelicApiKey() {
        CredentialAttributes attributes = new CredentialAttributes(PROJECT_NAME, NR_API_KEY, this.getClass(), false);
        return PasswordSafe.getInstance().getPassword(attributes);
    }

    /**
     * Updates the New Relic API Key, using PasswordSafe to securely persist the value.
     * @param newRelicApiKey a String with the new value of the New Relic API key
     */
    public void setNewRelicApiKey(String newRelicApiKey) {
        CredentialAttributes attributes = new CredentialAttributes(PROJECT_NAME, NR_API_KEY, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), newRelicApiKey);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }
}