package com.disney.idea.components;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
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
    private static String ACCOUNT_ID_LABEL = "Account ID";
    private static String API_KEY_LABEL = "API Key";
    private static String APP_NAME_LABEL = "Application Name";
    private static String DAYS_LABEL = "Days to Query";

    private String savedAccountId;
    private String savedApiKey;
    private String savedAppName;
    private Integer savedNumDays;
    private boolean isModified;

    private JBTextField accountIdField;
    private JBPasswordField apiKeyField;
    private JBTextField appNameField;
    private JBTextField numDaysField;
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
        numDaysField = new JBTextField(savedNumDays.toString());
        numDaysField.getDocument().addDocumentListener(getDocumentListener());

        FormLayout layout = new FormLayout(
                "10dlu, left:pref, 3dlu, pref, 7dlu, right:pref, 3dlu, pref:grow", // columns
                "p, 5dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");      // rows

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(SECTION_LABEL, cc.xyw(1,  1, 8));
        builder.addLabel(ACCOUNT_ID_LABEL, cc.xy (2,  3));
        builder.add(accountIdField, cc.xyw(3,  3, 6));
        builder.addLabel(API_KEY_LABEL, cc.xy(2, 5));
        builder.add(apiKeyField, cc.xyw(3, 5, 6));
        builder.addLabel(APP_NAME_LABEL, cc.xy(2, 7));
        builder.add(appNameField, cc.xyw(3, 7, 6));
        builder.addLabel(DAYS_LABEL, cc.xy(2, 9));
        builder.add(numDaysField, cc.xyw(3, 9, 6));

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
    }

    public void resetAllFields() {
        refreshSavedFields();
        accountIdField.setText(savedAccountId);
        apiKeyField.setText(savedApiKey);
        appNameField.setText(savedAppName);
        numDaysField.setText(savedNumDays.toString());
        isModified = false;
    }

}
