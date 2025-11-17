package com.dependencyanalyzer;

import com.dependencyanalyzer.model.DependencyInfo;
import com.dependencyanalyzer.visualization.GraphGenerator;

import java.io.File;
import java.util.*;

/**
 * Main entry point for the Dependency Analyzer tool
 */
public class DependencyAnalyzerMain {
    
    public static void main(String[] args) {
        // Hardcoded paths for testing with sample repositories
        // Change these paths to match your test repository locations
        String baseDir = System.getProperty("user.dir");
        String sourceRepoPath = baseDir + File.separator + "test-repos" + File.separator + "user-service";
        String reposBasePath = baseDir + File.separator + "test-repos";
        String outputPath = args.length > 0 ? args[0] : "dependency-graph.html";
        
        // Alternative: Use absolute paths (uncomment and modify if needed)
        // String sourceRepoPath = "C:/Users/bipin/dependency-analyzer/test-repos/user-service";
        // String reposBasePath = "C:/Users/bipin/dependency-analyzer/test-repos";
        
        System.out.println("=== Dependency Analyzer Tool ===");
        System.out.println("Source Repository: " + sourceRepoPath);
        System.out.println("Base Path: " + reposBasePath);
        System.out.println("Output File: " + outputPath);
        System.out.println();
        
        // Validate source repository path
        File sourceRepo = new File(sourceRepoPath);
        if (!sourceRepo.exists() || !sourceRepo.isDirectory()) {
            System.err.println("Error: Source repository path does not exist: " + sourceRepoPath);
            System.err.println("Please ensure the test repositories are in: " + reposBasePath);
            System.exit(1);
        }
        
        DependencyAnalyzer analyzer = new DependencyAnalyzer();
        Map<String, String> otherRepos;
        
        // Discover repositories from base path
        System.out.println("Discovering repositories in: " + reposBasePath);
        otherRepos = analyzer.discoverRepositories(reposBasePath);
        
        // Remove source repo from the list
        String sourceRepoName = sourceRepo.getName();
        otherRepos.remove(sourceRepoName);
        
        if (otherRepos.isEmpty()) {
            System.err.println("Error: No other repositories found to compare with.");
            System.err.println("Expected to find repositories in: " + reposBasePath);
            System.exit(1);
        }
        
        System.out.println("Found " + otherRepos.size() + " repositories to compare:");
        otherRepos.keySet().forEach(System.out::println);
        System.out.println();
        
        // Perform analysis
        List<DependencyInfo> dependencies = analyzer.analyzeDependencies(
            sourceRepoPath,
            otherRepos
        );
        
        if (dependencies.isEmpty()) {
            System.out.println("\nNo dependencies found between repositories.");
            return;
        }
        
        // Print summary
        printSummary(dependencies);
        
        // Generate visualization
        try {
            GraphGenerator generator = new GraphGenerator();
            generator.generateGraph(dependencies, outputPath);
            System.out.println("\n✓ Analysis complete! Open " + outputPath + " in a web browser to view the graph.");
        } catch (Exception e) {
            System.err.println("Error generating graph: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printUsage() {
        System.out.println("Dependency Analyzer Tool");
        System.out.println("========================");
        System.out.println();
        System.out.println("Usage: java -jar dependency-analyzer.jar <source-repo-path> [output-file] [repos-base-path]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  source-repo-path  : Path to the service repository to analyze");
        System.out.println("  output-file       : Output HTML file path (default: dependency-graph.html)");
        System.out.println("  repos-base-path   : Base path containing multiple repositories (optional)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar dependency-analyzer.jar /path/to/service-repo");
        System.out.println("  java -jar dependency-analyzer.jar /path/to/service-repo output.html /path/to/repos");
    }
    
    private static void printSummary(List<DependencyInfo> dependencies) {
        System.out.println("\n=== Dependency Summary ===");
        
        Map<DependencyInfo.DependencyType, Long> typeCounts = dependencies.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                DependencyInfo::getType,
                java.util.stream.Collectors.counting()
            ));
        
        typeCounts.forEach((type, count) -> {
            System.out.println(type + ": " + count);
        });
        
        System.out.println("\nTop dependencies by strength:");
        dependencies.stream()
            .sorted((a, b) -> Integer.compare(b.getStrength(), a.getStrength()))
            .limit(10)
            .forEach(dep -> {
                System.out.println(String.format("  %s -> %s [%s] (strength: %d/10)",
                    dep.getSourceRepo(),
                    dep.getTargetRepo(),
                    dep.getType(),
                    dep.getStrength()
                ));
            });
    }
}

