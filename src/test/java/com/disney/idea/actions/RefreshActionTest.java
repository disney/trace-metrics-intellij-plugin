package com.disney.idea.actions;

import org.junit.Assert;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class RefreshActionTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final String ACCOUNT_ID = "account-id";
    private static final String API_KEY = "api-key";
    private static final String APP_NAME = "app-name";
    private static final String NUM_DAYS = "10";
    private static final String UNTIL_DATE = "";

    public void testRefreshAction_getClient() {
        Assert.assertNotNull(RefreshAction.getClient(ACCOUNT_ID, API_KEY, APP_NAME, NUM_DAYS, UNTIL_DATE));
    }

    public void testRefreshAction_getClient_null() {
        Assert.assertNull(RefreshAction.getClient("", API_KEY, APP_NAME, NUM_DAYS, UNTIL_DATE));
        Assert.assertNull(RefreshAction.getClient(ACCOUNT_ID, "", APP_NAME, NUM_DAYS, UNTIL_DATE));
        Assert.assertNull(RefreshAction.getClient(ACCOUNT_ID, API_KEY, "", NUM_DAYS, UNTIL_DATE));
        Assert.assertNull(RefreshAction.getClient(ACCOUNT_ID, API_KEY, APP_NAME, "0", UNTIL_DATE));
    }

}