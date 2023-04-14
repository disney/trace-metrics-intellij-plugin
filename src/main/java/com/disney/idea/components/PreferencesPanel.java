package com.disney.idea.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Trace Metrics preference UI dialog allowing update of general and
 * project-specific preferences.
 */
public class PreferencesPanel extends JPanel {

    private static String SECTION_LABEL = "New Relic Configuration";
    private static String ACCOUNT_ID_LABEL = "Account ID: ";
    private static String API_KEY_LABEL = "API Key: ";
    private static String APP_NAME_LABEL = "Application Name: ";
    private static String DAYS_LABEL = "Days to Query: ";
    private static String UNTIL_DATE = "Until Date (YYYY-MM-DD): ";

    private String savedAccountId;
    private String savedApiKey;
    private String savedAppName;
    private String savedNumDays;
    private String savedUntilDate;
    private boolean isModified;

    private JBTextField accountIdField;
    private JBPasswordField apiKeyField;
    private JBTextField appNameField;
    private JBTextField numDaysField;
    private JBTextField untilDateField;
    private ApplicationPreferencesState applicationPreferences;
    private ProjectPreferencesState projectPreferences;

    public PreferencesPanel(ApplicationPreferencesState appPrefs, ProjectPreferencesState projPrefs) {
        applicationPreferences = appPrefs;
        projectPreferences = projPrefs;

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        refreshSavedFields();

        // create the account id field
        accountIdField = new JBTextField(savedAccountId);
        accountIdField.getDocument().addDocumentListener(getDocumentListener());

        // create the apiKey field
        apiKeyField = new JBPasswordField();
        apiKeyField.setText(savedApiKey);
        apiKeyField.getDocument().addDocumentListener(getDocumentListener());

        // create the appName field
        appNameField = new JBTextField(savedAppName);
        appNameField.getDocument().addDocumentListener(getDocumentListener());

        // create the num days field
        numDaysField = new JBTextField(savedNumDays);
        numDaysField.getDocument().addDocumentListener(getDocumentListener());

        // create the until date field
        untilDateField = new JBTextField(savedUntilDate);
        untilDateField.getDocument().addDocumentListener(getDocumentListener());

        // Reference: https://ptolemy.berkeley.edu/ptolemyII/ptII8.1/ptII/doc/whitepaper.pdf (see "Column and Row Specifications" section)
        // p = pref
        // dlu = dialog units
        FormLayout layout = new FormLayout(
                "10dlu, left:pref, 3dlu, pref:grow, 3dlu, pref", // 6 columns
                "p, 5dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");      // 11 rows

        //DefaultFormBuilder builder = new DefaultFormBuilder(layout, new FormDebugPanel()); // Useful for debugging layout
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(SECTION_LABEL, cc.xyw(1,  1, 6));
        builder.addLabel(ACCOUNT_ID_LABEL, cc.xy(2,  3));
        builder.add(accountIdField, cc.xyw(4,  3, 2));
        builder.addLabel(API_KEY_LABEL, cc.xy(2, 5));
        builder.add(apiKeyField, cc.xyw(4, 5, 3));
        builder.addLabel(APP_NAME_LABEL, cc.xy(2, 7));
        builder.add(appNameField, cc.xyw(4, 7, 3));
        builder.addLabel(DAYS_LABEL, cc.xy(2, 9));
        builder.add(numDaysField, cc.xyw(4, 9, 2));
        builder.addLabel(UNTIL_DATE, cc.xy(2, 11));
        builder.add(untilDateField, cc.xyw(4, 11, 2));
        builder.addLabel("(Past dates only - uses today's date if blank)", cc.xy(6, 11));

        this.add(builder.build());
    }

    public String getNewRelicAccountId() {
        return accountIdField.getText().trim();
    }

    public String getNewRelicApiKey() {
        return new String(apiKeyField.getPassword());
    }

    public String getNewRelicAppName() {
        return appNameField.getText().trim();
    }

    public String getNumDays() {
        return numDaysField.getText().trim();
    }

    public String getUntilDate() {
        return untilDateField.getText().trim();
    }

    public boolean isModified() {
        return isModified;
    }

    public DocumentListener getDocumentListener() {
        return new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateModified();
            }
            public void removeUpdate(DocumentEvent e) {
                updateModified();
            }
            public void insertUpdate(DocumentEvent e) {
                updateModified();
            }

            public void updateModified() {
                isModified = true;
            }
        };
    }

    public void refreshSavedFields() {
        savedAccountId = applicationPreferences.getNewRelicAccountId();
        savedApiKey = applicationPreferences.getNewRelicApiKey();
        savedAppName = projectPreferences.getNewRelicAppName();
        savedNumDays = projectPreferences.getNumDaysToQuery();
        savedUntilDate = projectPreferences.getUntilDateToQuery();
    }

    public void resetAllFields() {
        refreshSavedFields();
        accountIdField.setText(savedAccountId);
        apiKeyField.setText(savedApiKey);
        appNameField.setText(savedAppName);
        numDaysField.setText(savedNumDays);
        untilDateField.setText(savedUntilDate);
        isModified = false;
    }

}
