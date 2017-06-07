package org.mym.plog.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.TextFormat;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Detect issues for logging usage.
 * Created by muyangmin on Jun 05, 2017.
 * @see #ISSUE_LOG_CLASS
 */
/*package*/ final class LoggingIssueDetector extends Detector implements Detector.JavaPsiScanner {

    /**
     * Reports issue on calls to `android.util.Log` or `System.out.println`。
     */
    private static final Issue ISSUE_LOG_CLASS =
            Issue.create("LogNotPLog",
                    "Should use PLog or wrapper class",
                    "Your project has included PLog, logging calls should be going to PLog " +
                            "instead of `android.util.Log` or `System.out.println`.",
                    Category.MESSAGES,
                    6,
                    Severity.WARNING,
                    new Implementation(LoggingIssueDetector.class, Scope.JAVA_FILE_SCOPE));

    /**
     * Reports issue on explicit format on v/d/i/w/e methods.
     */
    private static final Issue ISSUE_NESTED_FORMAT =
            Issue.create("NestedFormatInPLog",
                    "explicit String.format() is redundant",
                    "PLog will handle string formatting automatically, " +
                            "so this explicit format() is useless",
                    Category.MESSAGES,
                    6,
                    Severity.WARNING,
                    new Implementation(LoggingIssueDetector.class, Scope.JAVA_FILE_SCOPE));

    private static final IssueReportHelper sHelper = new IssueReportHelper();

    /*package*/
    static List<Issue> getIssues() {
        List<Issue> issues = new ArrayList<>();

        issues.add(ISSUE_LOG_CLASS);
        //To be continue if needed ...

        return issues;
    }

    private static void checkNestedStringFormat(JavaContext context, PsiMethodCallExpression call) {
        PsiElement current = call;
        while (true) {
            current = LintUtils.skipParentheses(current.getParent());
            if (current == null || current instanceof PsiCodeBlock) {
                // Reached AST root or code block node; String.format not inside Timber.X(..).
                return;
            }
            if (current instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression expression = (PsiMethodCallExpression) current;
                if (Pattern.matches("org\\.mym\\.plog\\.PLog\\.(v|d|i|w|e)",
                        expression.getMethodExpression().getQualifiedName())) {
                    sHelper.reportIssue(context, ISSUE_NESTED_FORMAT, call);
                    return;
                }
            }
        }
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("v", "d", "i", "w", "e", "wtf");
    }

    @Override
    public void visitMethod(JavaContext context, JavaElementVisitor visitor,
                            PsiMethodCallExpression call, PsiMethod method) {
        PsiReferenceExpression methodExpression = call.getMethodExpression();
        String fullyQualifiedMethodName = methodExpression.getQualifiedName();

        if (fullyQualifiedMethodName.startsWith("android.util.Log")
                //Handle multiple overloaded out.print(and println, etc) methods.
                || fullyQualifiedMethodName.startsWith("java.lang.System.out.print")) {
            sHelper.reportIssue(context, ISSUE_LOG_CLASS, methodExpression);
            return;
        }

        if (fullyQualifiedMethodName.equals("java.lang.String.format")) {
            checkNestedStringFormat(context, call);
            return;
        }
    }

    private static class IssueReportHelper {
        /**
         * All param should be non-null.
         */
        void reportIssue(JavaContext context, Issue issue, PsiElement element) {
            context.report(issue, element, context.getLocation(element),
                    issue.getBriefDescription(TextFormat.TEXT));
        }
    }
}
