package com.tw.go.plugin;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class Filter {

    private String pipelinePattern;
    private String stagePattern;
    private BuildState status;

    public Filter(String pipelinePattern, String stagePattern, String status) {
        this.pipelinePattern = pipelinePattern;
        this.stagePattern = stagePattern;
        this.status = BuildState.fromRawString(status);
    }

    public boolean matches(String pipeline, String stage, BuildState status) {
        if (isNotAMatch(pipeline, convertToRegex(this.pipelinePattern))) {
            return false;
        }

        if (isNotAMatch(stage, convertToRegex(this.stagePattern))) {
            return false;
        }

        if (this.status != null && !this.status.equals(status)) {
            return false;
        }

        return true;
    }

    private boolean isNotAMatch(String value, String pattern) {
        if (isARegex(pattern)) {
            if (!value.toLowerCase().matches(pattern.toLowerCase())) {
                return true;
            }
        } else if (pattern != null && !pattern.equalsIgnoreCase(value)) {
            return true;
        }
    }

    private String convertToRegex(String pattern) {
        if (pattern == null || isARegex(pattern)) {
            return pattern;
        }
        return pattern.replaceAll("\\*", ".*");
    }

    private boolean isARegex(String value) {
        try {
            if (value != null) {
                Pattern.compile(value);
                return true;
            } else {
                return false;
            }
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public boolean matches(String pipeline, String stage, String rawStatus) {
        return matches(pipeline, stage, BuildState.fromRawString(rawStatus));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(pipelinePattern)
                .append(stagePattern)
                .append(status)
                .hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Filter)) {
            return false;
        }
        Filter otherFilter = (Filter) other;

        return new EqualsBuilder()
                .append(this.pipelinePattern, otherFilter.pipelinePattern)
                .append(this.stagePattern, otherFilter.stagePattern)
                .append(this.status, otherFilter.status)
                .build();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Pipeline: " + pipelinePattern + "\n");
        stringBuilder.append("Stage: " + stagePattern + "\n");
        stringBuilder.append("Status: " + status + "\n");

        return stringBuilder.toString();
    }
}
