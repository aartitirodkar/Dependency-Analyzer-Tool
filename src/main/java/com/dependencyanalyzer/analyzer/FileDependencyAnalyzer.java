package com.dependencyanalyzer.analyzer;

import com.dependencyanalyzer.model.DependencyInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes common files and imports between repositories
 */
public class FileDependencyAnalyzer {
    
    private static final Set<String> JAVA_EXTENSIONS = Set.of("java", "kt", "groovy", "scala");
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^import\\s+([^;]+);");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^package\\s+([^;]+);");
    
    /**
     * Analyzes common files and imports between repositories
     */
    public List<DependencyInfo> analyzeFileDependencies(String repoPath, Map<String, String> otherRepos) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        try {
            // Extract file structure and imports from source repository
            Map<String, Set<String>> sourceFiles = extractFileStructure(repoPath);
            Set<String> sourceImports = extractImports(repoPath);
            Set<String> sourcePackages = extractPackages(repoPath);
            
            // Compare with other repositories
            for (Map.Entry<String, String> entry : otherRepos.entrySet()) {
                String repoName = entry.getKey();
                String repoPath2 = entry.getValue();
                
                Map<String, Set<String>> targetFiles = extractFileStructure(repoPath2);
                Set<String> targetImports = extractImports(repoPath2);
                Set<String> targetPackages = extractPackages(repoPath2);
                
                // Find common file paths
                Set<String> commonFiles = new HashSet<>(sourceFiles.keySet());
                commonFiles.retainAll(targetFiles.keySet());
                
                // Find common imports
                Set<String> commonImports = new HashSet<>(sourceImports);
                commonImports.retainAll(targetImports);
                
                // Find common packages
                Set<String> commonPackages = new HashSet<>(sourcePackages);
                commonPackages.retainAll(targetPackages);
                
                if (!commonFiles.isEmpty() || !commonImports.isEmpty() || !commonPackages.isEmpty()) {
                    DependencyInfo depInfo = new DependencyInfo(
                        getRepoName(repoPath),
                        repoName,
                        DependencyInfo.DependencyType.COMMON_FILE,
                        "Common files/imports detected"
                    );
                    
                    if (!commonFiles.isEmpty()) {
                        depInfo.addDetail("Common file paths: " + commonFiles.size());
                        commonFiles.stream().limit(5).forEach(depInfo::addDetail);
                    }
                    
                    if (!commonImports.isEmpty()) {
                        depInfo.addDetail("Common imports: " + commonImports.size());
                        commonImports.stream()
                            .filter(imp -> !imp.startsWith("java.") && !imp.startsWith("javax."))
                            .limit(5)
                            .forEach(depInfo::addDetail);
                    }
                    
                    if (!commonPackages.isEmpty()) {
                        depInfo.addDetail("Common packages: " + commonPackages.size());
                        commonPackages.stream().limit(5).forEach(depInfo::addDetail);
                    }
                    
                    // Calculate strength based on findings
                    // Weak dependencies (strength < 5) for minimal matches
                    int strength = 2; // Base strength for any match
                    if (commonFiles.size() > 0) strength += 2;
                    if (commonImports.size() > 5) strength += 3;
                    else if (commonImports.size() > 0) strength += 1; // Weak if only a few imports
                    if (commonPackages.size() > 0) strength += 2;
                    depInfo.setStrength(Math.min(10, strength));
                    
                    dependencies.add(depInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing file dependencies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dependencies;
    }
    
    /**
     * Extracts file structure from repository
     */
    private Map<String, Set<String>> extractFileStructure(String repoPath) throws IOException {
        Map<String, Set<String>> fileStructure = new HashMap<>();
        File repoDir = new File(repoPath);
        
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            return fileStructure;
        }
        
        try (Stream<Path> paths = Files.walk(Paths.get(repoPath))) {
            paths.filter(Files::isRegularFile)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    String ext = fileName.contains(".") 
                        ? fileName.substring(fileName.lastIndexOf(".") + 1) 
                        : "";
                    return JAVA_EXTENSIONS.contains(ext.toLowerCase());
                })
                .forEach(path -> {
                    try {
                        Path relativePath = Paths.get(repoPath).relativize(path);
                        String relativePathStr = relativePath.toString().replace("\\", "/");
                        fileStructure.put(relativePathStr, new HashSet<>());
                    } catch (Exception e) {
                        // Skip if path resolution fails
                    }
                });
        }
        
        return fileStructure;
    }
    
    /**
     * Extracts import statements from Java files
     */
    private Set<String> extractImports(String repoPath) throws IOException {
        Set<String> imports = new HashSet<>();
        File repoDir = new File(repoPath);
        
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            return imports;
        }
        
        Collection<File> javaFiles = FileUtils.listFiles(
            repoDir,
            new String[]{"java"},
            true
        );
        
        for (File file : javaFiles) {
            try {
                List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("import ")) {
                        String importStmt = line.substring(7).replace(";", "").trim();
                        if (!importStmt.startsWith("java.") && !importStmt.startsWith("javax.")) {
                            imports.add(importStmt);
                        }
                    }
                }
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return imports;
    }
    
    /**
     * Extracts package declarations from Java files
     */
    private Set<String> extractPackages(String repoPath) throws IOException {
        Set<String> packages = new HashSet<>();
        File repoDir = new File(repoPath);
        
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            return packages;
        }
        
        Collection<File> javaFiles = FileUtils.listFiles(
            repoDir,
            new String[]{"java"},
            true
        );
        
        for (File file : javaFiles) {
            try {
                List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("package ")) {
                        String packageName = line.substring(8).replace(";", "").trim();
                        packages.add(packageName);
                        break; // Only first package declaration
                    }
                }
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return packages;
    }
    
    private String getRepoName(String repoPath) {
        File file = new File(repoPath);
        return file.getName();
    }
}

