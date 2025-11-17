package com.dependencyanalyzer;

import com.dependencyanalyzer.analyzer.FeignClientAnalyzer;
import com.dependencyanalyzer.analyzer.FileDependencyAnalyzer;
import com.dependencyanalyzer.analyzer.LibraryDependencyAnalyzer;
import com.dependencyanalyzer.analyzer.ConfigDependencyAnalyzer;
import com.dependencyanalyzer.model.DependencyInfo;

import java.io.File;
import java.util.*;

/**
 * Main dependency analyzer that coordinates all analysis types
 */
public class DependencyAnalyzer {
    
    private final LibraryDependencyAnalyzer libraryAnalyzer;
    private final FileDependencyAnalyzer fileAnalyzer;
    private final FeignClientAnalyzer feignClientAnalyzer;
    private final ConfigDependencyAnalyzer configAnalyzer;
    
    public DependencyAnalyzer() {
        this.libraryAnalyzer = new LibraryDependencyAnalyzer();
        this.fileAnalyzer = new FileDependencyAnalyzer();
        this.feignClientAnalyzer = new FeignClientAnalyzer();
        this.configAnalyzer = new ConfigDependencyAnalyzer();
    }
    
    /**
     * Analyzes dependencies between the source repository and other repositories
     * 
     * @param sourceRepoPath Path to the source service repository
     * @param otherRepos Map of repository names to their paths
     * @return List of all detected dependencies
     */
    public List<DependencyInfo> analyzeDependencies(String sourceRepoPath, Map<String, String> otherRepos) {
        List<DependencyInfo> allDependencies = new ArrayList<>();
        
        System.out.println("Starting dependency analysis...");
        System.out.println("Source repository: " + sourceRepoPath);
        System.out.println("Comparing with " + otherRepos.size() + " repositories");
        
        // Analyze library dependencies
        System.out.println("\n[1/4] Analyzing library dependencies...");
        List<DependencyInfo> libraryDeps = libraryAnalyzer.analyzeLibraryDependencies(sourceRepoPath, otherRepos);
        allDependencies.addAll(libraryDeps);
        System.out.println("Found " + libraryDeps.size() + " library dependencies");
        
        // Analyze file dependencies
        System.out.println("\n[2/4] Analyzing file and import dependencies...");
        List<DependencyInfo> fileDeps = fileAnalyzer.analyzeFileDependencies(sourceRepoPath, otherRepos);
        allDependencies.addAll(fileDeps);
        System.out.println("Found " + fileDeps.size() + " file dependencies");
        
        // Analyze Feign client dependencies
        System.out.println("\n[3/4] Analyzing Feign client dependencies...");
        List<DependencyInfo> feignDeps = feignClientAnalyzer.analyzeFeignClients(sourceRepoPath, otherRepos);
        allDependencies.addAll(feignDeps);
        System.out.println("Found " + feignDeps.size() + " Feign client dependencies");
        
        // Analyze configuration dependencies
        System.out.println("\n[4/4] Analyzing configuration dependencies...");
        List<DependencyInfo> configDeps = configAnalyzer.analyzeConfigDependencies(sourceRepoPath, otherRepos);
        allDependencies.addAll(configDeps);
        System.out.println("Found " + configDeps.size() + " configuration dependencies");
        
        System.out.println("\nTotal dependencies found: " + allDependencies.size());
        
        return allDependencies;
    }
    
    /**
     * Discovers repositories in a given directory
     */
    public Map<String, String> discoverRepositories(String basePath) {
        Map<String, String> repositories = new HashMap<>();
        File baseDir = new File(basePath);
        
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return repositories;
        }
        
        File[] subdirs = baseDir.listFiles(File::isDirectory);
        if (subdirs != null) {
            for (File subdir : subdirs) {
                // Check if it's a repository (has pom.xml or build.gradle)
                File pomFile = new File(subdir, "pom.xml");
                File gradleFile = new File(subdir, "build.gradle");
                
                if (pomFile.exists() || gradleFile.exists()) {
                    repositories.put(subdir.getName(), subdir.getAbsolutePath());
                }
            }
        }
        
        return repositories;
    }
}

