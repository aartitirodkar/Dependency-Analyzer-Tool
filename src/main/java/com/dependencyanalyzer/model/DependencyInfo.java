package com.dependencyanalyzer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents dependency information between repositories
 */
public class DependencyInfo {
    private String sourceRepo;
    private String targetRepo;
    private DependencyType type;
    private String description;
    private List<String> details;
    private int strength; // 1-10 scale indicating dependency strength

    public DependencyInfo(String sourceRepo, String targetRepo, DependencyType type, String description) {
        this.sourceRepo = sourceRepo;
        this.targetRepo = targetRepo;
        this.type = type;
        this.description = description;
        this.details = new ArrayList<>();
        this.strength = 5; // default strength
    }

    public enum DependencyType {
        COMMON_LIBRARY,
        COMMON_FILE,
        FEIGN_CLIENT,
        SHARED_CONFIG
    }

    // Getters and Setters
    public String getSourceRepo() {
        return sourceRepo;
    }

    public void setSourceRepo(String sourceRepo) {
        this.sourceRepo = sourceRepo;
    }

    public String getTargetRepo() {
        return targetRepo;
    }

    public void setTargetRepo(String targetRepo) {
        this.targetRepo = targetRepo;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = Math.max(1, Math.min(10, strength));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyInfo that = (DependencyInfo) o;
        return Objects.equals(sourceRepo, that.sourceRepo) &&
               Objects.equals(targetRepo, that.targetRepo) &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceRepo, targetRepo, type);
    }

    @Override
    public String toString() {
        return "DependencyInfo{" +
               "sourceRepo='" + sourceRepo + '\'' +
               ", targetRepo='" + targetRepo + '\'' +
               ", type=" + type +
               ", description='" + description + '\'' +
              // ", strength=" + strength +
               '}';
    }
}

