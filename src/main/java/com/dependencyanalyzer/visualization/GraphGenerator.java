package com.dependencyanalyzer.visualization;

import com.dependencyanalyzer.model.DependencyInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Generates interactive HTML visualization using D3.js
 */
public class GraphGenerator {
    
    private static String getHtmlTemplate() {
        return "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <title>Dependency Analysis Graph</title>\n" +
        "    <script src=\"https://d3js.org/d3.v7.min.js\"></script>\n" +
        "    <style>\n" +
        "        * { box-sizing: border-box; }\n" +
        "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }\n" +
        "        .container { max-width: 1600px; margin: 20px auto; background: rgba(255, 255, 255, 0.98); padding: 30px; border-radius: 20px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); backdrop-filter: blur(10px); }\n" +
        "        h1 { color: #2c3e50; text-align: center; margin-bottom: 10px; font-size: 2.5em; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; font-weight: 700; }\n" +
        "        .subtitle { text-align: center; color: #7f8c8d; margin-bottom: 30px; font-size: 1.1em; }\n" +
        "        .controls { margin-bottom: 25px; padding: 20px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n" +
        "        .controls label { margin-right: 25px; font-weight: 500; color: #2c3e50; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; }\n" +
        "        .controls input[type='checkbox'] { width: 18px; height: 18px; cursor: pointer; accent-color: #667eea; }\n" +
        "        .legend { display: flex; gap: 25px; margin-bottom: 25px; flex-wrap: wrap; justify-content: center; padding: 20px; background: #f8f9fa; border-radius: 12px; }\n" +
        "        .legend-item { display: flex; align-items: center; gap: 10px; padding: 8px 15px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: transform 0.2s; }\n" +
        "        .legend-item:hover { transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0,0,0,0.15); }\n" +
        "        .legend-color { width: 24px; height: 24px; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }\n" +
        "        .tooltip { position: absolute; padding: 15px; background: linear-gradient(135deg, rgba(44, 62, 80, 0.95) 0%, rgba(52, 73, 94, 0.95) 100%); color: white; border-radius: 10px; pointer-events: none; font-size: 13px; max-width: 350px; z-index: 1000; box-shadow: 0 10px 30px rgba(0,0,0,0.3); border: 2px solid rgba(255,255,255,0.2); backdrop-filter: blur(10px); line-height: 1.6; }\n" +
        "        .tooltip strong { color: #3498db; font-size: 14px; display: block; margin-bottom: 8px; }\n" +
        "        .bar { transition: all 0.3s ease; cursor: pointer; }\n" +
        "        .bar:hover { opacity: 0.8; }\n" +
        "        .pie-slice { transition: all 0.3s ease; cursor: pointer; stroke: white; stroke-width: 2; }\n" +
        "        .pie-slice:hover { opacity: 0.8; stroke-width: 3; }\n" +
        "        .chart-label { font-size: 12px; font-weight: 600; fill: #2c3e50; }\n" +
        "        .chart-axis { font-size: 11px; fill: #7f8c8d; }\n" +
        "        #dependencies-table tbody tr { border-bottom: 1px solid #e0e0e0; transition: background 0.2s; }\n" +
        "        #dependencies-table tbody tr:hover { background: #f5f7fa; }\n" +
        "        #dependencies-table tbody td { padding: 12px 15px; color: #2c3e50; }\n" +
        "        .type-badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }\n" +
        "        .type-library { background: #3498db; color: white; }\n" +
        "        .type-file { background: #2ecc71; color: white; }\n" +
        "        .type-feign { background: #e74c3c; color: white; }\n" +
        "        .type-config { background: #9b59b6; color: white; }\n" +
        "        .strength-bar { display: inline-block; width: 60px; height: 8px; background: #e0e0e0; border-radius: 4px; overflow: hidden; position: relative; }\n" +
        "        .strength-fill { height: 100%; background: linear-gradient(90deg, #e74c3c, #f39c12, #2ecc71); transition: width 0.3s; }\n" +
        "        .details-list { max-width: 400px; font-size: 11px; color: #7f8c8d; max-height: 200px; overflow-y: auto; padding: 5px; background: #f8f9fa; border-radius: 6px; }\n" +
        "        .details-list div { margin: 3px 0; padding: 3px 0; border-bottom: 1px solid #e9ecef; }\n" +
        "        .details-list div:last-child { border-bottom: none; }\n" +
        "        .details-list::-webkit-scrollbar { width: 6px; }\n" +
        "        .details-list::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 3px; }\n" +
        "        .details-list::-webkit-scrollbar-thumb { background: #888; border-radius: 3px; }\n" +
        "        .details-list::-webkit-scrollbar-thumb:hover { background: #555; }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <div class=\"container\">\n" +
        "        <h1>🔗 Repository Dependency Analysis</h1>\n" +
        "        <div class=\"subtitle\">Interactive visualization of dependencies between microservices</div>\n" +
        "        <div style=\"background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); margin-bottom: 30px;\">\n" +
        "            <h3 style=\"color: #2c3e50; margin-bottom: 20px; font-size: 1.5em;\">📈 Summary Statistics</h3>\n" +
        "            <div style=\"display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px;\">\n" +
       /* "                <div style=\"padding: 15px; background: #f8f9fa; border-radius: 8px;\">\n" +
        "                    <div style=\"font-size: 14px; color: #7f8c8d; margin-bottom: 5px;\">Repositories</div>\n" +
        "                    <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"total-nodes\">0</div>\n" +
        "                </div>\n" +*/
        "                <div style=\"padding: 15px; background: #f8f9fa; border-radius: 8px;\">\n" +
        "                    <div style=\"font-size: 14px; color: #7f8c8d; margin-bottom: 5px;\">Total Dependencies</div>\n" +
        "                    <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"total-links\">0</div>\n" +
        "                </div>\n" +
        "                <div style=\"padding: 15px; background: #3498db; border-radius: 8px;\">\n" +
        "                    <div style=\"font-size: 18px; color: #2c3e50; margin-bottom: 5px;\">Library Dependencies</div>\n" +
        "                    <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"library-deps\">0</div>\n" +
        "                </div>\n" +
        "                <div style=\"padding: 15px; background: #2ecc71; border-radius: 8px;\">\n" +
                "            <div style=\"font-size: 18px; color: #2c3e50; margin-bottom: 5px;\">CommonFile Dependencies</div>\n" +
                "            <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"file-deps\">0</div>\n" +
                "                </div>\n" +
        "                <div style=\"padding: 15px; background: #e74c3c; border-radius: 8px;\">\n" +
        "                    <div style=\"font-size: 18px; color: #2c3e50; margin-bottom: 5px;\">Feign Client Dependencies</div>\n" +
        "                    <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"feign-deps\">0</div>\n" +
        "                </div>\n" +
        "                <div style=\"padding: 15px; background: #9b59b6; border-radius: 8px;\">\n" +
        "                    <div style=\"font-size: 18px; color: #2c3e50; margin-bottom: 5px;\">Config Dependencies</div>\n" +
        "                    <div style=\"font-size: 28px; font-weight: 700; color: #2c3e50;\" id=\"config-deps\">0</div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div style=\"display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px;\">\n" +
        "            <div style=\"background: white; padding: 20px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">\n" +
        "                <h3 style=\"color: #2c3e50; margin-bottom: 15px; font-size: 1.3em;\">📊 Dependency Type Distribution</h3>\n" +
        "                <svg id=\"pie-chart\" width=\"100%\" height=\"300\"></svg>\n" +
        "            </div>\n" +
        "            <div style=\"background: white; padding: 20px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">\n" +
        "                <h3 style=\"color: #2c3e50; margin-bottom: 15px; font-size: 1.3em;\">📈 Repository Dependency Count</h3>\n" +
        "                <svg id=\"bar-chart\" width=\"100%\" height=\"300\"></svg>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div id=\"dependencies-table-container\" style=\"margin-top: 30px;\">\n" +
        "            <h2 style=\"color: #2c3e50; margin-bottom: 20px; font-size: 1.8em;\">📊 Dependencies Table</h2>\n" +
        "            <div style=\"overflow-x: auto; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">\n" +
        "                <table id=\"dependencies-table\" style=\"border-collapse: collapse; background: white;\">\n" +
        "                    <thead>\n" +
        "                        <tr style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;\">\n" +
        "                            <th style=\"padding: 15px; text-align: left; font-weight: 600;\">Source Repository</th>\n" +
        "                            <th style=\"padding: 15px; text-align: left; font-weight: 600;\">Target Repository</th>\n" +
        "                            <th style=\"padding: 15px; text-align: center; font-weight: 100;\">  </th>\n" +
        "                            <th style=\"padding: 15px; text-align: left; font-weight: 600;\">Type</th>\n" +
      //  "                            <th style=\"padding: 15px; text-align: center; font-weight: 600;\">Strength</th>\n" +
        "                            <th style=\"padding: 15px; text-align: left; font-weight: 600;\">Description</th>\n" +
        "                            <th style=\"padding: 15px; text-align: left; font-weight: 600;\">Details</th>\n" +
        "                        </tr>\n" +
        "                    </thead>\n" +
        "                    <tbody id=\"dependencies-tbody\">\n" +
        "                    </tbody>\n" +
        "                </table>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "    <div class=\"tooltip\" id=\"tooltip\" style=\"display: none;\"></div>\n" +
        "    <script>\n" +
        "        const data = %%JSON_DATA%%;\n" +
        "        \n" +
        "        let allLinks = data.links;\n" +
        "        let filteredLinks = allLinks;\n" +
        "        const tooltip = d3.select(\"#tooltip\");\n" +
        "        \n" +
        "        function updateStats() {\n" +
      //  "            document.getElementById('total-nodes').textContent = data.nodes.length;\n" +
        "            document.getElementById('total-links').textContent = filteredLinks.length;\n" +
        "            document.getElementById('library-deps').textContent = filteredLinks.filter(function(l) { return l.type === 'COMMON_LIBRARY'; }).length;\n" +
        "            document.getElementById('file-deps').textContent = filteredLinks.filter(function(l) { return l.type === 'COMMON_FILE'; }).length;\n" +
        "            document.getElementById('feign-deps').textContent = filteredLinks.filter(function(l) { return l.type === 'FEIGN_CLIENT'; }).length;\n" +
        "            document.getElementById('config-deps').textContent = filteredLinks.filter(function(l) { return l.type === 'SHARED_CONFIG'; }).length;\n" +
        "        }\n" +
        "        \n" +
        "        function populateTable() {\n" +
        "            const tbody = document.getElementById('dependencies-tbody');\n" +
        "            tbody.innerHTML = '';\n" +
        "            \n" +
        "            const typeClassMap = { 'COMMON_LIBRARY': 'type-library', 'COMMON_FILE': 'type-file', 'FEIGN_CLIENT': 'type-feign', 'SHARED_CONFIG': 'type-config' };\n" +
        "            const typeNameMap = { 'COMMON_LIBRARY': 'Library', 'COMMON_FILE': 'File', 'FEIGN_CLIENT': 'Feign Client', 'SHARED_CONFIG': 'Config' };\n" +
        "            \n" +
        "            allLinks.forEach(function(link) {\n" +
        "                const row = document.createElement('tr');\n" +
        "                const sourceCell = document.createElement('td');\n" +
        "                const sourceId = typeof link.source === 'object' ? link.source.id : link.source;\n" +
        "                const targetId = typeof link.target === 'object' ? link.target.id : link.target;\n" +
        "                sourceCell.innerHTML = '<strong style=\"color: #2c3e50; font-size: 14px;\">' + sourceId + '</strong>';\n" +
        "                const targetCell = document.createElement('td');\n" +
        "                targetCell.innerHTML = '<strong style=\"color: #2c3e50; font-size: 14px;\">' + targetId + '</strong>';\n" +
        "                \n" +
        "                // Add dependency relationship column\n" +
        "                const relationshipCell = document.createElement('td');\n" +
        "                relationshipCell.innerHTML = '<span style=\"color: #7f8c8d; font-size: 13px;\">' + sourceId + '</span> <span style=\"color: #667eea; font-weight: 600; margin: 0 8px;\">→</span> <span style=\"color: #7f8c8d; font-size: 13px;\">' + targetId + '</span>';\n" +
        "                const typeCell = document.createElement('td');\n" +
        "                const typeBadge = document.createElement('span');\n" +
        "                typeBadge.className = 'type-badge ' + (typeClassMap[link.type] || '');\n" +
        "                typeBadge.textContent = typeNameMap[link.type] || link.type;\n" +
        "                typeCell.appendChild(typeBadge);\n" +
    /*    "                const strengthCell = document.createElement('td');\n" +
        "                strengthCell.style.textAlign = 'center';\n" +
        "                const strengthBar = document.createElement('div');\n" +
        "                strengthBar.className = 'strength-bar';\n" +
        "                const strengthFill = document.createElement('div');\n" +
        "                strengthFill.className = 'strength-fill';\n" +
        "                strengthFill.style.width = (link.strength * 10) + '%';\n" +
        "                strengthBar.appendChild(strengthFill);\n" +
        "                strengthCell.appendChild(strengthBar);\n" +
        "                strengthCell.innerHTML += ' <span style=\"margin-left: 8px; font-weight: 600; color: #2c3e50;\">' + link.strength + '/10</span>';\n" +
        "                if (link.strength < 5) { strengthCell.innerHTML += ' <span style=\"color: orange; font-size: 11px; margin-left: 5px;\">(Weak)</span>'; }\n" +
       */ "                const descCell = document.createElement('td');\n" +
        "                descCell.textContent = link.description || '-';\n" +
        "                descCell.style.fontSize = '12px';\n" +
        "                const detailsCell = document.createElement('td');\n" +
        "                if (link.details && link.details.length > 0) {\n" +
        "                    const detailsDiv = document.createElement('div');\n" +
        "                    detailsDiv.className = 'details-list';\n" +
        "                    \n" +
        "                    // For file dependencies, extract and highlight file names\n" +
        "                    if (link.type === 'COMMON_FILE') {\n" +
        "                        link.details.forEach(function(detail) {\n" +
        "                            const detailDiv = document.createElement('div');\n" +
        "                            // Check if this is a file path (contains / or \\)\n" +
        "                            if (detail.includes('/') || detail.includes('\\\\')) {\n" +
        "                                detailDiv.innerHTML = '<span style=\"color: #e74c3c; font-weight: 600;\">📄</span> <span style=\"color: #2c3e50;\">' + detail + '</span>';\n" +
        "                            } else if (detail.includes('Common file paths:')) {\n" +
        "                                detailDiv.innerHTML = '<strong style=\"color: #667eea;\">' + detail + '</strong>';\n" +
        "                            } else if (detail.includes('Common imports:') || detail.includes('Common packages:')) {\n" +
        "                                detailDiv.innerHTML = '<span style=\"color: #7f8c8d; font-style: italic;\">' + detail + '</span>';\n" +
        "                            } else {\n" +
        "                                detailDiv.innerHTML = '<span style=\"color: #3498db;\">📦</span> <span style=\"color: #2c3e50;\">' + detail + '</span>';\n" +
        "                            }\n" +
        "                            detailsDiv.appendChild(detailDiv);\n" +
        "                        });\n" +
        "                    } else {\n" +
        "                        link.details.forEach(function(detail) {\n" +
        "                            const detailDiv = document.createElement('div');\n" +
        "                            detailDiv.textContent = '• ' + detail;\n" +
        "                            detailsDiv.appendChild(detailDiv);\n" +
        "                        });\n" +
        "                    }\n" +
        "                    detailsCell.appendChild(detailsDiv);\n" +
        "                } else { detailsCell.textContent = '-'; detailsCell.style.color = '#bdc3c7'; }\n" +
        "                row.appendChild(sourceCell); row.appendChild(targetCell); row.appendChild(relationshipCell);\n" +
        "                row.appendChild(typeCell); row.appendChild(descCell); row.appendChild(detailsCell);\n" +
        "                tbody.appendChild(row);\n" +
        "            });\n" +
        "        }\n" +
        "        \n" +
        "        // Create Pie Chart\n" +
        "        function createPieChart() {\n" +
        "            const pieSvg = d3.select('#pie-chart');\n" +
        "            pieSvg.selectAll('*').remove();\n" +
        "            const pieWidth = pieSvg.node().getBoundingClientRect().width || 400;\n" +
        "            const pieHeight = 300;\n" +
        "            const radius = Math.min(pieWidth, pieHeight) / 2 - 40;\n" +
        "            const g = pieSvg.attr('width', pieWidth).attr('height', pieHeight)\n" +
        "                .append('g').attr('transform', 'translate(' + (pieWidth / 2) + ',' + (pieHeight / 2) + ')');\n" +
        "            \n" +
        "            const typeCounts = {};\n" +
        "            filteredLinks.forEach(function(l) { typeCounts[l.type] = (typeCounts[l.type] || 0) + 1; });\n" +
        "            const pieData = Object.keys(typeCounts).map(function(key) { return { type: key, value: typeCounts[key] }; });\n" +
        "            \n" +
        "            if (pieData.length === 0) return;\n" +
        "            \n" +
        "            const pie = d3.pie().value(function(d) { return d.value; });\n" +
        "            const arc = d3.arc().innerRadius(0).outerRadius(radius);\n" +
        "            const arcLabel = d3.arc().innerRadius(radius * 0.7).outerRadius(radius * 0.7);\n" +
        "            \n" +
        "            const typeColorMap = { 'COMMON_LIBRARY': '#3498db', 'COMMON_FILE': '#2ecc71', 'FEIGN_CLIENT': '#e74c3c', 'SHARED_CONFIG': '#9b59b6' };\n" +
        "            \n" +
        "            const arcs = g.selectAll('arc').data(pie(pieData)).enter().append('g');\n" +
        "            arcs.append('path').attr('d', arc).attr('fill', function(d) { return typeColorMap[d.data.type] || '#999'; })\n" +
        "                .attr('class', 'pie-slice').on('mouseover', function(event, d) {\n" +
        "                    d3.select(this).attr('opacity', 0.7);\n" +
        "                    tooltip.style('display', 'block').html('<strong>' + d.data.type + '</strong><br/>Count: ' + d.data.value)\n" +
        "                        .style('left', (event.pageX + 10) + 'px').style('top', (event.pageY - 10) + 'px');\n" +
        "                }).on('mouseout', function() { d3.select(this).attr('opacity', 1); tooltip.style('display', 'none'); });\n" +
        "            \n" +
        "            arcs.append('text').attr('transform', function(d) { return 'translate(' + arcLabel.centroid(d) + ')'; })\n" +
        "                .attr('text-anchor', 'middle').attr('class', 'chart-label')\n" +
        "                .text(function(d) { return d.data.value; });\n" +
        "        }\n" +
        "        \n" +
        "        // Create Bar Chart\n" +
        "        function createBarChart() {\n" +
        "            const barSvg = d3.select('#bar-chart');\n" +
        "            barSvg.selectAll('*').remove();\n" +
        "            const barWidth = barSvg.node().getBoundingClientRect().width || 400;\n" +
        "            const barHeight = 300;\n" +
        "            const margin = { top: 20, right: 30, bottom: 60, left: 60 };\n" +
        "            const chartWidth = barWidth - margin.left - margin.right;\n" +
        "            const chartHeight = barHeight - margin.top - margin.bottom;\n" +
        "            \n" +
        "            const g = barSvg.attr('width', barWidth).attr('height', barHeight)\n" +
        "                .append('g').attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');\n" +
        "            \n" +
        "            const repoCounts = {};\n" +
        "            filteredLinks.forEach(function(l) {\n" +
        "                const sourceId = typeof l.source === 'object' ? l.source.id : l.source;\n" +
        "                const targetId = typeof l.target === 'object' ? l.target.id : l.target;\n" +
        "                repoCounts[sourceId] = (repoCounts[sourceId] || 0) + 1;\n" +
        "                repoCounts[targetId] = (repoCounts[targetId] || 0) + 1;\n" +
        "            });\n" +
        "            \n" +
        "            const barData = Object.keys(repoCounts).map(function(key) { return { repo: key, count: repoCounts[key] }; })\n" +
        "                .sort(function(a, b) { return b.count - a.count; });\n" +
        "            \n" +
        "            if (barData.length === 0) return;\n" +
        "            \n" +
        "            const xScale = d3.scaleBand().domain(barData.map(function(d) { return d.repo; })).range([0, chartWidth]).padding(0.2);\n" +
        "            const yScale = d3.scaleLinear().domain([0, d3.max(barData, function(d) { return d.count; })]).nice().range([chartHeight, 0]);\n" +
        "            \n" +
        "            g.append('g').attr('transform', 'translate(0,' + chartHeight + ')').call(d3.axisBottom(xScale))\n" +
        "                .selectAll('text').attr('class', 'chart-axis').attr('transform', 'rotate(-45)').style('text-anchor', 'end');\n" +
        "            g.append('g').call(d3.axisLeft(yScale)).selectAll('text').attr('class', 'chart-axis');\n" +
        "            \n" +
        "            const colorScale = d3.scaleSequential(d3.interpolateViridis).domain([0, barData.length]);\n" +
        "            \n" +
        "            g.selectAll('.bar').data(barData).enter().append('rect').attr('class', 'bar')\n" +
        "                .attr('x', function(d) { return xScale(d.repo); }).attr('y', function(d) { return yScale(d.count); })\n" +
        "                .attr('width', xScale.bandwidth()).attr('height', function(d) { return chartHeight - yScale(d.count); })\n" +
        "                .attr('fill', function(d, i) { return colorScale(i); })\n" +
        "                .on('mouseover', function(event, d) {\n" +
        "                    tooltip.style('display', 'block').html('<strong>' + d.repo + '</strong><br/>Dependencies: ' + d.count)\n" +
        "                        .style('left', (event.pageX + 10) + 'px').style('top', (event.pageY - 10) + 'px');\n" +
        "                }).on('mouseout', function() { tooltip.style('display', 'none'); });\n" +
        "        }\n" +
        "        \n" +
        "        updateStats();\n" +
        "        populateTable();\n" +
        "        createPieChart();\n" +
        "        createBarChart();\n" +
        "    </script>\n" +
        "</body>\n" +
        "</html>";
    }
    
    /**
     * Generates an interactive HTML graph visualization
     */
    public void generateGraph(List<DependencyInfo> dependencies, String outputPath) throws IOException {
        GraphData graphData = buildGraphData(dependencies);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonData = gson.toJson(graphData);
        
        // Use string replacement instead of String.format to avoid format specifier issues
        String htmlTemplate = getHtmlTemplate();
        String html = htmlTemplate.replace("%%JSON_DATA%%", jsonData);
        
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(html);
        }
        
        System.out.println("Graph visualization generated: " + outputPath);
    }
    
    /**
     * Builds graph data structure for D3.js
     */
    private GraphData buildGraphData(List<DependencyInfo> dependencies) {
        Set<String> nodeIds = new HashSet<>();
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();
        
        // Collect all unique repository names
        for (DependencyInfo dep : dependencies) {
            nodeIds.add(dep.getSourceRepo());
            nodeIds.add(dep.getTargetRepo());
        }
        
        // Create nodes
        Map<String, Integer> dependencyCounts = new HashMap<>();
        for (DependencyInfo dep : dependencies) {
            dependencyCounts.put(dep.getSourceRepo(), 
                dependencyCounts.getOrDefault(dep.getSourceRepo(), 0) + 1);
            dependencyCounts.put(dep.getTargetRepo(), 
                dependencyCounts.getOrDefault(dep.getTargetRepo(), 0) + 1);
        }
        
        for (String nodeId : nodeIds) {
            GraphNode node = new GraphNode();
            node.id = nodeId;
            node.dependencyCount = dependencyCounts.getOrDefault(nodeId, 0);
            nodes.add(node);
        }
        
        // Create links
        for (DependencyInfo dep : dependencies) {
            GraphLink link = new GraphLink();
            link.source = dep.getSourceRepo();
            link.target = dep.getTargetRepo();
            link.type = dep.getType().name();
           // link.strength = dep.getStrength();
            link.description = dep.getDescription();
            link.details = new ArrayList<>();
            if (!dep.getDetails().isEmpty()) {
                link.details.addAll(dep.getDetails());
            }
            links.add(link);
        }
        
        GraphData graphData = new GraphData();
        graphData.nodes = nodes;
        graphData.links = links;
        
        return graphData;
    }
    
    // Inner classes for graph data structure
    private static class GraphData {
        List<GraphNode> nodes;
        List<GraphLink> links;
    }
    
    private static class GraphNode {
        String id;
        int dependencyCount;
    }
    
    private static class GraphLink {
        String source;
        String target;
        String type;
        int strength;
        String description;
        List<String> details;
    }
}
