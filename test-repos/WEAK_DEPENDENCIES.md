# Weak Dependencies Feature

## What are Weak Dependencies?

**Weak dependencies** are dependencies with a **strength score less than 5** (on a scale of 1-10). They represent:

- **Low coupling**: Minimal shared code or libraries
- **Loose relationships**: Few common patterns or imports
- **Less critical**: Lower impact if one service changes

## Strength Scoring

The tool calculates dependency strength based on:

### Library Dependencies
- **Weak (4-5)**: 1-2 shared libraries
- **Medium (6-7)**: 3-5 shared libraries  
- **Strong (8-10)**: 6+ shared libraries

### File/Import Dependencies
- **Weak (2-4)**: Only a few common imports, no common files
- **Medium (5-7)**: Multiple common imports or some common files
- **Strong (8-10)**: Many common imports, files, and packages

### Feign Client Dependencies
- **Always Strong (8-10)**: Feign clients indicate direct service-to-service communication

## Visual Indicators

In the graph visualization:

1. **Opacity**: Weak dependencies appear more transparent (30% opacity vs 60% for strong)
2. **Tooltip**: Weak dependencies show "(Weak)" label in orange
3. **Filter**: Checkbox to show/hide weak dependencies

## Use Cases

### Filtering Weak Dependencies
- **Focus on critical dependencies**: Uncheck "Show Weak Dependencies" to see only strong relationships
- **Full picture**: Check the box to see all dependencies, including weak ones
- **Impact analysis**: Strong dependencies require more careful change management

### Examples from Test Repositories

**Weak Dependencies** (strength < 5):
- Services sharing only 1-2 common libraries
- Services with minimal common imports (just a few Spring annotations)
- Services with no direct Feign client communication

**Strong Dependencies** (strength >= 5):
- Services sharing 6+ common libraries (all 5 test services)
- Services with Feign clients (user-service ↔ order-service)
- Services with many common imports and patterns

## How to Use

1. **View all dependencies**: Keep "Show Weak Dependencies" checked (default)
2. **Focus on critical**: Uncheck to hide weak dependencies
3. **Hover over edges**: See strength score and "(Weak)" indicator
4. **Check statistics**: Counts update based on filter

## Benefits

- **Clarity**: Reduce visual clutter by hiding weak dependencies
- **Focus**: Identify critical dependencies that need attention
- **Analysis**: Understand the true coupling between services
- **Planning**: Make informed decisions about service boundaries

