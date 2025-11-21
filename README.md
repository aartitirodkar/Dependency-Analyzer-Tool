# Dependency-Analyzer-Tool
Analyzes the dependencies in repositories

# Overview:
The dependency Analyzer Tool is designed to identify,visualize and manage dependencies within a given codebase or repositories.It helps developers and teams to understand the relationships between different components,libraries,imports and modules,thereby facilitating maintainance,refactoring and impact analysis for changes.This tool reduce the technical debt and provide clear insights into dependency graph.

# Architecture:
1. SourceCode Scanner : It scans the sourcecode for libraires mentioned in the pom.xml,imports used,FeignClients connection by parsing the pattern to extract the dependecy information.
2. Dependency Graph Builder : Constructs a graph repsentation of the identified dependencies.
3. Reporting/Visualisation : Generates reports including the pie chart,bar chart and detailed dependency information table for more user clarification.
4. API/CLI Interface : Currently its provides the CLI or local run in intellij.

# Execution Details: 
To run the project please follow the below instructions:
1. Make JAVA 11 or higher version is installed.
2. Checkout/fork the project from github to your local.
3. import the project to intellij
4. put all the necessary paths to intellij like JDK.
5. DependencyAnalyzerMain class from the project
6. dependency-graph.html as a report file will get generated in the same folder structure.
7. Open the dependency-graph.html in the browser.
