package org.mym.plog.lint;

import com.android.tools.lint.detector.api.Issue;

import java.util.List;

/**
 * This is automatically called by lint tool.
 */
@SuppressWarnings("unused")
public class IssueRegistry extends com.android.tools.lint.client.api.IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        System.out.println("Called customized issue registry!");
        return LoggingIssueDetector.getIssues();
    }
}
