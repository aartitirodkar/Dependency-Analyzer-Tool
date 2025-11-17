package com.dependencyanalyzer.analyzer;

import com.dependencyanalyzer.model.DependencyInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes Feign client dependencies between repositories
 */
public class FeignClientAnalyzer {
    
    private static final Pattern FEIGN_CLIENT_PATTERN = Pattern.compile(
        "@FeignClient\\s*\\([^)]*name\\s*=\\s*[\"']([^\"']+)[\"']",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern FEIGN_CLIENT_URL_PATTERN = Pattern.compile(
        "@FeignClient\\s*\\([^)]*url\\s*=\\s*[\"']([^\"']+)[\"']",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern FEIGN_CLIENT_VALUE_PATTERN = Pattern.compile(
        "@FeignClient\\s*\\([\"']([^\"']+)[\"']",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Analyzes Feign client dependencies
     */
    public List<DependencyInfo> analyzeFeignClients(String repoPath, Map<String, String> otherRepos) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        try {
            // Extract Feign clients from source repository
            Map<String, Set<String>> sourceFeignClients = extractFeignClients(repoPath);
            
            // Compare with other repositories
            for (Map.Entry<String, String> entry : otherRepos.entrySet()) {
                String repoName = entry.getKey();
                String repoPath2 = entry.getValue();
                
                Map<String, Set<String>> targetFeignClients = extractFeignClients(repoPath2);
                
                // Find common Feign client names
                Set<String> commonClients = new HashSet<>();
                for (String clientName : sourceFeignClients.keySet()) {
                    if (targetFeignClients.containsKey(clientName)) {
                        commonClients.add(clientName);
                    }
                }
                
                // Also check for similar URLs
                Set<String> sourceUrls = new HashSet<>();
                sourceFeignClients.values().forEach(sourceUrls::addAll);
                
                Set<String> targetUrls = new HashSet<>();
                targetFeignClients.values().forEach(targetUrls::addAll);
                
                Set<String> commonUrls = new HashSet<>(sourceUrls);
                commonUrls.retainAll(targetUrls);
                
                if (!commonClients.isEmpty() || !commonUrls.isEmpty()) {
                    DependencyInfo depInfo = new DependencyInfo(
                        getRepoName(repoPath),
                        repoName,
                        DependencyInfo.DependencyType.FEIGN_CLIENT,
                        "Common Feign clients detected"
                    );
                    
                    if (!commonClients.isEmpty()) {
                        depInfo.addDetail("Common Feign client names: " + commonClients.size());
                        commonClients.forEach(client -> {
                            depInfo.addDetail("Client: " + client);
                        });
                        depInfo.setStrength(8); // Feign clients indicate strong coupling
                    }
                    
                    if (!commonUrls.isEmpty()) {
                        depInfo.addDetail("Common service URLs: " + commonUrls.size());
                        commonUrls.stream().limit(3).forEach(url -> {
                            depInfo.addDetail("URL: " + url);
                        });
                        if (depInfo.getStrength() < 8) {
                            depInfo.setStrength(6);
                        }
                    }
                    
                    dependencies.add(depInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing Feign clients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dependencies;
    }
    
    /**
     * Extracts Feign client information from repository
     */
    private Map<String, Set<String>> extractFeignClients(String repoPath) throws IOException {
        Map<String, Set<String>> feignClients = new HashMap<>();
        File repoDir = new File(repoPath);
        
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            return feignClients;
        }
        
        Collection<File> javaFiles = FileUtils.listFiles(
            repoDir,
            new String[]{"java"},
            true
        );
        
        for (File file : javaFiles) {
            try {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                
                if (content.contains("@FeignClient") || content.contains("@feignclient")) {
                    // Extract client name
                    String clientName = extractFeignClientName(content);
                    if (clientName != null && !clientName.isEmpty()) {
                        Set<String> urls = feignClients.getOrDefault(clientName, new HashSet<>());
                        
                        // Extract URL if present
                        String url = extractFeignClientUrl(content);
                        if (url != null && !url.isEmpty()) {
                            urls.add(url);
                        }
                        
                        feignClients.put(clientName, urls);
                    }
                }
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return feignClients;
    }
    
    /**
     * Extracts Feign client name from content
     */
    private String extractFeignClientName(String content) {
        // Try name attribute first
        Matcher matcher = FEIGN_CLIENT_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try value attribute
        matcher = FEIGN_CLIENT_VALUE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extracts Feign client URL from content
     */
    private String extractFeignClientUrl(String content) {
        Matcher matcher = FEIGN_CLIENT_URL_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private String getRepoName(String repoPath) {
        File file = new File(repoPath);
        return file.getName();
    }
}

