# Testing the Dependency Analyzer

## Quick Test

After building the dependency analyzer tool, you can test it with these sample repositories.

### Step 1: Build the Tool

```bash
cd C:\Users\bipin\dependency-analyzer
mvn clean package
```

### Step 2: Run the Analyzer

```bash
java -jar target/dependency-analyzer-1.0.0.jar test-repos/user-service dependency-graph.html test-repos
```

### Expected Results

The tool should detect:

#### 1. Common Library Dependencies (6 shared dependencies):
- `org.springframework.boot:spring-boot-starter-web:2.7.14`
- `org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8`
- `org.projectlombok:lombok:1.18.28`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`
- `org.apache.commons:commons-lang3:3.12.0`
- `junit:junit:4.13.2`

#### 2. Common File/Import Dependencies:
- Both use `org.springframework.web.bind.annotation.RestController`
- Both use `org.springframework.web.bind.annotation.RequestMapping`
- Both use `org.springframework.beans.factory.annotation.Autowired`
- Both use `org.springframework.stereotype.Service`
- Both use `com.fasterxml.jackson.annotation.JsonProperty`
- Both use `org.apache.commons.lang3.StringUtils`
- Both use `java.util.List` and `java.util.ArrayList`

#### 3. Feign Client Dependencies:
- Both repositories have Feign client interfaces
- Both use `@FeignClient` annotation
- Both use Spring Cloud OpenFeign patterns

### Step 3: View the Results

Open `dependency-graph.html` in your web browser to see:
- Interactive graph visualization
- Repository nodes (user-service and order-service)
- Dependency edges colored by type
- Hover tooltips with details
- Statistics dashboard

## What to Look For

1. **Graph Visualization**: 
   - Two nodes representing `user-service` and `order-service`
   - Multiple edges showing different dependency types
   - Color coding: Blue (libraries), Green (files), Red (Feign clients)

2. **Console Output**:
   - Summary of dependencies found
   - Count of each dependency type
   - Top dependencies by strength

3. **HTML Output**:
   - Statistics showing total repositories and dependencies
   - Interactive graph with draggable nodes
   - Tooltips with detailed information

## Troubleshooting

If the tool doesn't find dependencies:
1. Ensure both repositories have `pom.xml` files
2. Check that Java source files are in `src/main/java`
3. Verify the paths are correct
4. Check console output for any error messages

