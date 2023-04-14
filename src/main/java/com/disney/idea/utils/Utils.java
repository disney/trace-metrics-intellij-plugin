package com.disney.idea.utils;

import static java.awt.Desktop.isDesktopSupported;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.disney.idea.client.NewRelicClient;
import com.disney.idea.components.ApplicationPreferencesState;
import com.disney.idea.components.ProjectPreferencesState;
import com.disney.idea.components.TraceTableModel;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Provides utility and convenience methods related to plugin UI and source navigation.
 */
public class Utils {

    private static final String TRACE_QUALIFIED_NAME = "com.newrelic.api.agent.Trace";

    /**
     * Convenience method which looks up the IntelliJ Project object corresponding to
     * the current UI focus, allowing the plugin to operate on the correct configuration
     * and state.
     * @return the IntelliJ project object corresponding to the current UI focus
     */
    public static Project getProject() {
        DataContext context = DataManager.getInstance().getDataContextFromFocus().getResultSync();
        return getProject(context);
    }

    /**
     * Looks up the IntelliJ project object from the given IntelliJ DataContext.
     * @param context IntelliJ application DataContext
     * @return the IntelliJ project object corresponding to the given DataContext.
     */
    public static Project getProject(DataContext context) {
        return PlatformDataKeys.PROJECT.getData(context);
    }

    /**
     * Returns a reference to the Trace Metrics UI table for the project in current UI
     * focus, to support handling menu actions.
     * @return a reference to the JTable holding the metrics view for the project in UI focus
     */
    public static JTable getTable() {
        if (getProject() == null) {
            return null;
        }
        JScrollPane scrollPane = (JScrollPane) ToolWindowManager.getInstance(getProject())
                .getToolWindow("Trace Metrics").getContentManager().findContent("").getComponent().getComponent(1);
        JViewport viewport = scrollPane.getViewport();
        return (JTable) viewport.getView();
    }

    /**
     * Returns a reference to the underlying data model underlying the JTable component
     * in current UI focus, used for updating the content.
     * @return the runtimeTableModel underlying the metrics display table for the project in UI focus
     */
    public static TraceTableModel getTableModel() {
        JTable table = getTable();
        return table == null ? null : (TraceTableModel) table.getModel();
    }

    public static void refreshCounts(Map<String, Long> traceCounts) {
        TraceTableModel model = Utils.getTableModel();
        if (model != null) {
            model.addTraceCounts(traceCounts);
            Utils.getTable().setModel(model);
        }
    }

    /**
     * Constructs a URL to open a metrics query for the given metrics name in an
     * external browser web view rendered by New Relic Insights.
     * @param searchTerm the metric name to query for in the configured application.
     * @return a URI pointing to a New Relic Insights query result view for the named metric.
     * @throws UnsupportedEncodingException never (we pass a static reference to a supported encoding)
     * @throws URISyntaxException never (we use a static template and type-checked inputs)
     */
    public static URI getNewRelicUrl(String searchTerm) throws UnsupportedEncodingException, URISyntaxException {
        ApplicationPreferencesState applicationPreferences = ApplicationPreferencesState.getInstance();
        String baseUrl = NewRelicClient.getInsightsUrl(applicationPreferences.getNewRelicAccountId());
        ProjectPreferencesState projectPreferences = ProjectPreferencesState.getInstance(getProject());
        String appName = projectPreferences.getNewRelicAppName();
        String numDays = projectPreferences.getNumDaysToQuery();
        String untilDate = projectPreferences.getUntilDateToQuery();

        String browserQuery;
        if (StringUtils.isBlank(untilDate)) {
            browserQuery = String.format("SELECT * FROM Transaction WHERE appName = '%s' AND name = 'WebTransaction/Custom/%s' SINCE %s days ago LIMIT 1000", appName, searchTerm, numDays);
        } else {
            String startDate = LocalDate.parse(untilDate).minusDays(Integer.parseInt(numDays)).toString();
            browserQuery = String.format("SELECT * FROM Transaction WHERE appName = '%s' AND name = 'WebTransaction/Custom/%s' SINCE '%s' UNTIL '%s' LIMIT 1000", appName, searchTerm, startDate, untilDate);
        }

        String queryUrl = baseUrl + URLEncoder.encode(browserQuery, CharEncoding.US_ASCII);
        return new URI(queryUrl);
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Check if a given PSI element is a New Relic trace annotation with a metric name.
     * @param element   the PSI element to check
     * @return          true if the element is a named trace annotation
     */
    public static boolean isNamedTraceElement(PsiElement element) {
        if (element == null) {
            return false;
        }
        if (element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            return TRACE_QUALIFIED_NAME.equals(annotation.getQualifiedName()) && Trace.getMetricName(annotation) != null;
        }
        return false;
    }

    /**
     * Check if a given PSI element is a child of a New Relic trace annotation with a metric name.
     * @param element   the PSI element to check
     * @return          true if the element is a child of a named trace annotation
     */
    public static boolean isPartOfTraceElement(PsiElement element) {
        PsiElement parent = getTraceAnnotationParent(element);
        return isNamedTraceElement(parent);
    }

    /**
     * Navigate the PSI tree to get a parent of the given element which is an annotation.
     * @param element   the child PSI element to check
     * @return          the first parent (not necessarily immediate) of the element which is an annotation or null if
     *                  no annotation type parent is found.
     */
    public static PsiAnnotation getTraceAnnotationParent(PsiElement element) {
        boolean onlySearchImmediateParent = false;
        return PsiTreeUtil.getParentOfType(element, PsiAnnotation.class, onlySearchImmediateParent);
    }

}
