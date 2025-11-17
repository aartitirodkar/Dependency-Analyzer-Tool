package com.dependencyanalyzer.analyzer;

import com.dependencyanalyzer.model.DependencyInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Analyzes common configuration files and database dependencies
 */
public class ConfigDependencyAnalyzer {
    
    private static final Set<String> CONFIG_EXTENSIONS = Set.of("yml", "yaml", "properties", "xml");
    private static final Set<String> CONFIG_PATHS = Set.of(
        "src/main/resources/application.yml",
        "src/main/resources/application.yaml",
        "src/main/resources/application.properties",
        "src/main/resources/bootstrap.yml",
        "src/main/resources/bootstrap.properties"
    );
    
    /**
     * Analyzes common configuration dependencies
     */
    public List<DependencyInfo> analyzeConfigDependencies(String repoPath, Map<String, String> otherRepos) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        try {
            // Extract config structure from source repository
            Map<String, Set<String>> sourceConfigs = extractConfigStructure(repoPath);
            Map<String, String> sourceDbConfigs = extractDatabaseConfigs(repoPath);
            
            // Compare with other repositories
            for (Map.Entry<String, String> entry : otherRepos.entrySet()) {
                String repoName = entry.getKey();
                String repoPath2 = entry.getValue();
                
                Map<String, Set<String>> targetConfigs = extractConfigStructure(repoPath2);
                Map<String, String> targetDbConfigs = extractDatabaseConfigs(repoPath2);
                
                // Find common config files
                Set<String> commonConfigs = new HashSet<>(sourceConfigs.keySet());
                commonConfigs.retainAll(targetConfigs.keySet());
                
                // Find common config keys/values
                Set<String> commonKeys = new HashSet<>();
                for (String configFile : commonConfigs) {
                    Set<String> sourceKeys = sourceConfigs.get(configFile);
                    Set<String> targetKeys = targetConfigs.get(configFile);
                    if (sourceKeys != null && targetKeys != null) {
                        Set<String> keys = new HashSet<>(sourceKeys);
                        keys.retainAll(targetKeys);
                        commonKeys.addAll(keys);
                    }
                }
                
                // Find common database configurations
                Set<String> commonDbConfigs = new HashSet<>();
                for (Map.Entry<String, String> dbEntry : sourceDbConfigs.entrySet()) {
                    String key = dbEntry.getKey();
                    String value = dbEntry.getValue();
                    if (targetDbConfigs.containsKey(key) && 
                        targetDbConfigs.get(key).equals(value)) {
                        commonDbConfigs.add(key + "=" + value);
                    }
                }
                
                if (!commonConfigs.isEmpty() || !commonKeys.isEmpty() || !commonDbConfigs.isEmpty()) {
                    DependencyInfo depInfo = new DependencyInfo(
                        getRepoName(repoPath),
                        repoName,
                        DependencyInfo.DependencyType.SHARED_CONFIG,
                        "Common configuration detected"
                    );
                    
                    if (!commonConfigs.isEmpty()) {
                        depInfo.addDetail("Common config files: " + commonConfigs.size());
                        commonConfigs.stream().limit(3).forEach(depInfo::addDetail);
                    }
                    
                    if (!commonKeys.isEmpty()) {
                        depInfo.addDetail("Common config keys: " + commonKeys.size());
                        commonKeys.stream().limit(5).forEach(depInfo::addDetail);
                    }
                    
                    if (!commonDbConfigs.isEmpty()) {
                        depInfo.addDetail("Common database configs: " + commonDbConfigs.size());
                        commonDbConfigs.stream().limit(3).forEach(depInfo::addDetail);
                    }
                    
                    // Calculate strength
                    int strength = 4; // Base for config similarity
                    if (commonConfigs.size() > 0) strength += 2;
                    if (commonKeys.size() > 5) strength += 2;
                    if (commonDbConfigs.size() > 0) strength += 2; // Database configs indicate strong coupling
                    depInfo.setStrength(Math.min(10, strength));
                    
                    dependencies.add(depInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing config dependencies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dependencies;
    }
    
    /**
     * Extracts configuration file structure
     */
    private Map<String, Set<String>> extractConfigStructure(String repoPath) throws IOException {
        Map<String, Set<String>> configs = new HashMap<>();
        File repoDir = new File(repoPath);
        
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            return configs;
        }
        
        File resourcesDir = new File(repoDir, "src/main/resources");
        if (!resourcesDir.exists()) {
            return configs;
        }
        
        Collection<File> configFiles = FileUtils.listFiles(
            resourcesDir,
            new String[]{"yml", "yaml", "properties", "xml"},
            false
        );
        
        for (File file : configFiles) {
            try {
                String relativePath = "src/main/resources/" + file.getName();
                Set<String> keys = extractConfigKeys(file);
                configs.put(relativePath, keys);
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return configs;
    }
    
    /**
     * Extracts configuration keys from a file
     */
    private Set<String> extractConfigKeys(File file) throws IOException {
        Set<String> keys = new HashSet<>();
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
                continue;
            }
            
            // Extract key (before = or :)
            int separatorIndex = Math.max(line.indexOf('='), line.indexOf(':'));
            if (separatorIndex > 0) {
                String key = line.substring(0, separatorIndex).trim();
                if (!key.isEmpty()) {
                    keys.add(key);
                }
            }
        }
        
        return keys;
    }
    
    /**
     * Extracts database configuration
     */
    private Map<String, String> extractDatabaseConfigs(String repoPath) throws IOException {
        Map<String, String> dbConfigs = new HashMap<>();
        File repoDir = new File(repoPath);
        
        File resourcesDir = new File(repoDir, "src/main/resources");
        if (!resourcesDir.exists()) {
            return dbConfigs;
        }
        
        Collection<File> configFiles = FileUtils.listFiles(
            resourcesDir,
            new String[]{"yml", "yaml", "properties"},
            false
        );
        
        for (File file : configFiles) {
            try {
                List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim().toLowerCase();
                    if (line.contains("datasource") || line.contains("database") || 
                        line.contains("jdbc") || line.contains("mysql") || 
                        line.contains("redis") || line.contains("mongodb")) {
                        // Extract key-value pairs
                        if (line.contains("url:") || line.contains("url=")) {
                            dbConfigs.put("db_url", extractValue(line));
                        }
                        if (line.contains("username:") || line.contains("username=")) {
                            dbConfigs.put("db_username", extractValue(line));
                        }
                        if (line.contains("driver:") || line.contains("driver=")) {
                            dbConfigs.put("db_driver", extractValue(line));
                        }
                    }
                }
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return dbConfigs;
    }
    
    private String extractValue(String line) {
        int separatorIndex = Math.max(line.indexOf('='), line.indexOf(':'));
        if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
            return line.substring(separatorIndex + 1).trim();
        }
        return "";
    }
    
    private String getRepoName(String repoPath) {
        File file = new File(repoPath);
        return file.getName();
    }
}

