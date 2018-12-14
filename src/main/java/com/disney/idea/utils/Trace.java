package com.disney.idea.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;

/**
 * Represents a source-code annotation where New Relic will record metrics
 * for code execution through that trace point. Trace Metrics requires that
 * the trace annotation contains an explicit metric name in order to be used
 * for UI navigation and metric reporting.
 */
public class Trace {

    private static final String METRIC_NAME = "metricName";

    private final String metricName;
    private final TextRange textRange;
    private final PsiFile file;
    private final int lineNumber;

    public Trace(String metricName, TextRange textRange, PsiFile file, int lineNumber) {
        this.metricName = metricName;
        this.textRange = textRange;
        this.file = file;
        this.lineNumber = lineNumber;
    }

    public static Trace fromPsiAnnotation(PsiAnnotation annotation) {
        String metricName = getMetricName(annotation);
        if (metricName != null) {
            PsiFile file = annotation.getContainingFile();
            final int lineNumber = StringUtil.offsetToLineNumber(file.getText(), annotation.getTextOffset()) + 1;
            return new Trace(metricName, annotation.getTextRange(), file, lineNumber);
        }
        return null;
    }

    public static String getMetricName(PsiAnnotation annotation) {
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        for (PsiNameValuePair p : attributes) {
            if (METRIC_NAME.equals(p.getName())) {
                return p.getLiteralValue();
            }
        }
        return null;
    }

    public PsiFile getFile() {
        return file;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public String getMetricName() {
        return metricName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
