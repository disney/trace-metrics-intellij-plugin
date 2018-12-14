package com.disney.idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;

import java.util.ArrayList;

/**
 * Gathers references to New Relic trace annotations by inspecting the source
 * of the given project, providing a list of {@link Trace} objects to be instrumented
 * by the plugin for UI navigation from a trace point to its corresponding named
 * metric line.
 */
public class TraceLoader {
    private static final String FILE_TYPE_JAVA = "JAVA";

    private Project project;

    public TraceLoader(Project project) {
        this.project = project;
    }

    public ArrayList<Trace> load() {
        ArrayList<Trace> traces = new ArrayList<Trace>();
        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(fileInProject -> {
            PsiFile file = PsiManager.getInstance(project).findFile(fileInProject);
            if (file != null && FILE_TYPE_JAVA.equals(file.getFileType().getName())) {
                processFile(file, traces);
            }
            return true;
        });

        return traces;
    }

    private void processFile(PsiFile file, ArrayList<Trace> traces) {
        file.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (Utils.isNamedTraceElement(element)) {
                    PsiAnnotation annotation = (PsiAnnotation) element;
                    Trace trace = Trace.fromPsiAnnotation(annotation);
                    if (trace != null) {
                        traces.add(trace);
                    }
                }
                super.visitElement(element);
            }
        });
    }

}
