package com.disney.idea.utils;

import com.disney.idea.components.ApplicationPreferencesState;
import com.disney.idea.components.ProjectPreferencesState;
import com.disney.idea.components.TraceTableModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.commons.lang3.CharEncoding;

import com.disney.idea.client.NewRelicClient;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import static java.awt.Desktop.isDesktopSupported;

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
        return DataKeys.PROJECT.getData(context);
    }

    /**
     * Returns a reference to the Trace Metrics UI table for the project in current UI
     * focus, to support handling menu actions.
     * @return a reference to the JTable holding the metrics view for the project in UI focus
     */
    public static JTable getTable() {
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
        return (TraceTableModel) table.getModel();
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
        ApplicationPreferencesState applicationPreferences = ApplicationPreferencesState.getInstance(getProject());
        String baseUrl = NewRelicClient.getInsightsUrl(applicationPreferences.getNewRelicAccountId());
        ProjectPreferencesState projectPreferences = ProjectPreferencesState.getInstance(getProject());
        String appName = projectPreferences.getNewRelicAppName();
        Integer numDays = projectPreferences.getNumDaysToQuery();

        String browserQuery = String.format("SELECT * from Transaction where appName = '%s' and name = 'WebTransaction/Custom/%s' since %d days ago", appName, searchTerm, numDays);
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
        PsiAnnotation parent = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class, onlySearchImmediateParent);
        return parent;
    }

}
