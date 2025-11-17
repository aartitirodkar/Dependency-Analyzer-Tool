package com.dependencyanalyzer.analyzer;

import com.dependencyanalyzer.model.DependencyInfo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Analyzes common library dependencies between repositories
 */
public class LibraryDependencyAnalyzer {
    
    /**
     * Analyzes library dependencies from pom.xml files
     */
    public List<DependencyInfo> analyzeLibraryDependencies(String repoPath, Map<String, String> otherRepos) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        try {
            // Get dependencies from source repository
            Map<String, String> sourceDependencies = extractDependencies(repoPath);
            
            // Compare with other repositories
            for (Map.Entry<String, String> entry : otherRepos.entrySet()) {
                String repoName = entry.getKey();
                String repoPath2 = entry.getValue();
                
                Map<String, String> targetDependencies = extractDependencies(repoPath2);
                
                // Find common dependencies
                Set<String> commonDeps = new HashSet<>(sourceDependencies.keySet());
                commonDeps.retainAll(targetDependencies.keySet());
                
                if (!commonDeps.isEmpty()) {
                    DependencyInfo depInfo = new DependencyInfo(
                        getRepoName(repoPath),
                        repoName,
                        DependencyInfo.DependencyType.COMMON_LIBRARY,
                        "Common libraries: " + commonDeps.size()
                    );
                    
                    for (String dep : commonDeps) {
                        String version1 = sourceDependencies.get(dep);
                        String version2 = targetDependencies.get(dep);
                        if (version1.equals(version2)) {
                            depInfo.addDetail(dep + ":" + version1);
                        } else {
                            depInfo.addDetail(dep + " (versions: " + version1 + " vs " + version2 + ")");
                            depInfo.setStrength(7); // Version mismatch increases dependency concern
                        }
                    }
                    
                    // Set strength based on number of common dependencies
                    // Weak dependencies (strength < 5) for 1-2 shared libraries
                    // Medium (5-7) for 3-5 shared libraries
                    // Strong (8-10) for 6+ shared libraries
                    if (commonDeps.size() <= 2) {
                        depInfo.setStrength(3 + commonDeps.size()); // 4-5 (weak)
                    } else if (commonDeps.size() <= 5) {
                        depInfo.setStrength(5 + commonDeps.size() / 2); // 6-7 (medium)
                    } else {
                        depInfo.setStrength(Math.min(10, 8 + commonDeps.size() / 3)); // 8-10 (strong)
                    }
                    
                    dependencies.add(depInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing library dependencies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dependencies;
    }
    
    /**
     * Extracts dependencies from pom.xml file
     */
    private Map<String, String> extractDependencies(String repoPath) {
        Map<String, String> dependencies = new HashMap<>();
        
        try {
            File pomFile = new File(repoPath, "pom.xml");
            if (!pomFile.exists()) {
                // Try to find pom.xml in subdirectories
                Collection<File> pomFiles = FileUtils.listFiles(
                    new File(repoPath),
                    new String[]{"xml"},
                    true
                );
                
                for (File file : pomFiles) {
                    if (file.getName().equals("pom.xml")) {
                        pomFile = file;
                        break;
                    }
                }
            }
            
            if (pomFile.exists()) {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = reader.read(new FileReader(pomFile));
                
                // Extract dependencies
                model.getDependencies().forEach(dep -> {
                    String key = dep.getGroupId() + ":" + dep.getArtifactId();
                    String version = dep.getVersion() != null ? dep.getVersion() : "unknown";
                    dependencies.put(key, version);
                });
            }
        } catch (Exception e) {
            System.err.println("Error reading pom.xml from " + repoPath + ": " + e.getMessage());
        }
        
        return dependencies;
    }
    
    private String getRepoName(String repoPath) {
        File file = new File(repoPath);
        return file.getName();
    }
}

