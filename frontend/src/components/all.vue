<template>
    <!-- 模板部分 -->
    <div class="knowledge-graph-page">
        <AppHeader />

        <div class="main-content">
            <div ref="graphCanvas" class="graph-canvas"></div>

            <div v-if="searchMessage" class="search-result-message">
                <p>{{ searchMessage }}</p>
            </div>

            <div v-if="loading" class="loading-overlay">
                <div class="loading-spinner">
                    <div class="spinner"></div>
                    <p>正在加载图谱数据...</p>
                </div>
            </div>

            <div class="top-toolbar">
                <h1 class="graph-title">🌐 神话知识图谱</h1>
                <div class="toolbar-buttons">
                    <button @click="toggleRelationType('person')" :class="['btn', { active: showPersonRelations }]">
                        人人关系 ({{ totalPersonRelations }})
                    </button>
                    <button @click="toggleRelationType('place')" :class="['btn', { active: showPlaceRelations }]">
                        人地关系 ({{ totalPlaceRelations }})
                    </button>
                    <!-- 🆕 新增：数据量控制按钮 -->
                    <button @click="toggleDataLimit" :class="['btn', 'btn-limit', { active: showAllData }]">
                        {{ showAllData ? '显示部分数据' : '显示全部数据' }}

                    </button>
                    <button @click="resetGraph" class="btn btn-reset">重置视图</button>
                    <button @click="toggleSearchPanel" class="btn btn-search">
                        {{ showSearchPanel ? '隐藏搜索' : '显示搜索' }}
                    </button>
                </div>
            </div>

            <div class="side-legend">
                <h4>图例说明</h4>
                <div class="legend-item">
                    <span class="legend-dot person-node"></span>
                    <span>人物 ({{ totalPersons }})</span>
                </div>
                <div class="legend-item">
                    <span class="legend-dot place-node"></span>
                    <span>地点 ({{ totalPlaces }})</span>
                </div>
                <div v-if="currentSearchTarget" class="legend-item search-target">
                    <span class="legend-dot search-target-node"></span>
                    <span>搜索目标: {{ currentSearchTarget }}</span>
                </div>
                <!-- 🆕 新增：数据状态显示 -->
                <div class="legend-item data-status">
                    <span class="legend-info">📊</span>
                    <span>{{ showAllData ? '全部数据' : `部分数据 (${dataLimit}个)` }}</span>
                </div>
            </div>

            <div v-if="showSearchPanel" class="search-panel">
                <div class="panel-header">
                    <h3>搜索与筛选</h3>
                    <button @click="toggleSearchPanel" class="close-btn">×</button>
                </div>

                <div class="search-section">
                    <h4>实体搜索</h4>
                    <div class="search-input-group">
                        <input type="text" v-model="searchKeyword" @keyup.enter="performSearch" @input="onSearchInput"
                            placeholder="输入人名或地名..." class="search-input" />
                        <button @click="performSearch" class="search-btn">搜索</button>
                    </div>

                    <div v-if="searchSuggestions.length > 0" class="search-suggestions">
                        <div v-for="suggestion in searchSuggestions" :key="suggestion.uniqueKey"
                            @click="selectSuggestion(suggestion)" class="suggestion-item">
                            {{ suggestion.name }} <span class="suggestion-type">({{ suggestion.type }})</span>
                        </div>
                    </div>

                    <div v-if="currentSearchTarget" class="search-status">
                        <p><strong>当前搜索：</strong>{{ currentSearchTarget }}</p>
                        <p><strong>搜索类型：</strong>{{ searchTargetType === 'person' ? '人物' : '地点' }}</p>
                        <button @click="clearSearch" class="btn-clear-search">清除搜索</button>
                    </div>
                </div>

                <!-- 🆕 新增：数据控制面板 -->
                <div class="data-control-section">
                    <h4>数据显示控制</h4>
                    <div class="control-group">
                        <label>显示数量限制：</label>
                        <select v-model="dataLimit" @change="onDataLimitChange" :disabled="showAllData">
                            <option value="200">200个关系</option>
                            <option value="500">500个关系</option>
                            <option value="1000">1000个关系</option>
                            <option value="2000">2000个关系</option>
                        </select>
                    </div>
                    <div class="control-group">
                        <button @click="toggleDataLimit" :class="['btn-toggle-all', { active: showAllData }]">
                            {{ showAllData ? '🔄 显示部分数据' : '🌐 显示全部数据' }}
                        </button>
                    </div>
                    <div class="data-warning" v-if="!showAllData && totalAvailableRelations > dataLimit">
                        <p>⚠️ 当前仅显示 {{ dataLimit }} 个关系，数据库中共有 {{ totalAvailableRelations }} 个关系</p>
                    </div>
                </div>

                <div v-if="selectedNode" class="node-info-section">
                    <h4>节点信息</h4>
                    <div class="node-info">
                        <div><strong>名称：</strong>{{ selectedNode.name }}</div>
                        <div><strong>类型：</strong>{{ selectedNode.type === 'person' ? '人物' : '地点' }}</div>
                        <div><strong>连接数：</strong>{{ selectedNode.degree }}</div>
                        <div v-if="selectedNode.relations && selectedNode.relations.length > 0">
                            <strong>相关关系：</strong>
                            <ul class="relation-list">
                                <li v-for="relation in selectedNode.relations" :key="relation.key">
                                    {{ relation.type }} ({{ relation.count }}个)
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div class="bottom-stats">
                <div class="stat-item">
                    <span class="stat-number">{{ nodeCount }}</span>
                    <span class="stat-label">当前节点</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ relationCount }}</span>
                    <span class="stat-label">当前关系</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ totalPersons }}</span>
                    <span class="stat-label">人物</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ totalPlaces }}</span>
                    <span class="stat-label">地点</span>
                </div>
                <!-- 🆕 新增：数据状态指示器 -->
                <div class="stat-item" :class="{ 'stat-limited': !showAllData }">
                    <span class="stat-number">{{ showAllData ? '全部' : '部分' }}</span>
                    <span class="stat-label">数据模式</span>
                </div>
            </div>

            <div v-if="hoveredNode" class="node-tooltip" :style="tooltipStyle">
                <h5>{{ hoveredNode.name }}</h5>
                <p>{{ hoveredNode.type === 'person' ? '人物' : '地点' }}</p>
                <p>连接数: {{ hoveredNode.degree }}</p>
            </div>
        </div>

        <AppFooter />
    </div>
</template>

<script>
import AppHeader from './AppHeader.vue';
import AppFooter from './AppFooter.vue';

export default {
    name: "KnowledgeGraph",
    components: { AppHeader, AppFooter },

    data() {
        return {
            graphData: { nodes: [], links: [] },
            allNodes: [],
            myChart: null,
            loading: false,
            showSearchPanel: false,
            showPersonRelations: true,
            showPlaceRelations: true,
            searchKeyword: '',
            searchSuggestions: [],
            selectedNode: null,
            hoveredNode: null,
            tooltipStyle: {},
            currentSearchTarget: null,
            searchTargetId: null,
            searchTargetType: null,
            searchMessage: '',
            nodeCount: 0,
            relationCount: 0,
            totalPersons: 0,
            totalPlaces: 0,
            totalPersonRelations: 0,
            totalPlaceRelations: 0,
            // 关系计数和管理
            relationCountMap: new Map(),
            nodePairRelations: new Map(),
            // 🆕 新增：数据量控制相关变量
            showAllData: false, // 默认显示部分数据
            dataLimit: 300, // 默认显示300个关系
            totalAvailableRelations: 0 // 数据库中总的关系数量
        }
    },
    mounted() {
        document.title = "中国神话知识图谱";
        this.initGraph();
        this.loadAllNodes().then(() => {
            this.loadGraphData();
        });
    },
    methods: {
        initGraph() {
            if (this.$refs.graphCanvas) {
                this.myChart = this.$echarts.init(this.$refs.graphCanvas);
                this.handleResize = () => this.myChart?.resize();
                window.addEventListener('resize', this.handleResize);
            }
        },

        async loadAllNodes() {
            try {
                const neo4j = require('neo4j-driver');
                const driver = neo4j.driver(
                    process.env.VUE_APP_NEO4J_URI,
                    neo4j.auth.basic(process.env.VUE_APP_NEO4J_USER, process.env.VUE_APP_NEO4J_PASSWORD)
                );
                const session = driver.session({ database: process.env.VUE_APP_NEO4J_DATABASE });

                const allNodesResult = await session.run(`
                    MATCH (n) WHERE n:Person OR n:Place 
                    RETURN DISTINCT n.id as id, 
                           coalesce(n.name, '') as name,
                           CASE WHEN n:Person THEN 'person' ELSE 'place' END as type,
                           CASE WHEN n:Place THEN coalesce(n.place, n.name) ELSE n.name END as displayName
                    ORDER BY displayName LIMIT 10000
                `);

                const nodeMap = new Map();
                allNodesResult.records.forEach(record => {
                    const id = record.get('id');
                    const displayName = record.get('displayName') || record.get('name');
                    const type = record.get('type');
                    if (displayName && displayName.trim()) {
                        const key = `${displayName}-${type}`;
                        if (!nodeMap.has(key)) {
                            nodeMap.set(key, { id, name: displayName.trim(), type, uniqueKey: key });
                        }
                    }
                });
                this.allNodes = Array.from(nodeMap.values());
                await session.close();
                await driver.close();
            } catch (error) {
                console.error('加载节点列表失败:', error);
                alert('无法加载节点列表，请检查数据库连接和配置。');
            }
        },

        async loadGraphData(searchParams = null) {
            this.loading = true;
            this.graphData = { nodes: [], links: [] };
            this.searchMessage = '';

            // 重置关系计数
            this.relationCountMap.clear();
            this.nodePairRelations.clear();

            const isSearchMode = searchParams && searchParams.name && searchParams.type;

            if (isSearchMode) {
                this.currentSearchTarget = searchParams.name;
                this.searchTargetId = searchParams.id;
                this.searchTargetType = searchParams.type;
            } else {
                this.currentSearchTarget = null;
                this.searchTargetId = null;
                this.searchTargetType = null;
            }

            let driver;
            try {
                const neo4j = require('neo4j-driver');
                driver = neo4j.driver(
                    process.env.VUE_APP_NEO4J_URI,
                    neo4j.auth.basic(process.env.VUE_APP_NEO4J_USER, process.env.VUE_APP_NEO4J_PASSWORD)
                );
                const session = driver.session({ database: process.env.VUE_APP_NEO4J_DATABASE });

                const nodes = new Map();
                const links = [];

                if (isSearchMode) {
                    await this.loadRelatedNodes(session, nodes, links, searchParams);
                } else {
                    await this.loadAllRelations(session, nodes, links);
                }

                await session.close();

                this.graphData.nodes = Array.from(nodes.values());
                this.graphData.links = links;

                if (isSearchMode && this.graphData.nodes.length === 0) {
                    this.searchMessage = `未找到与 "${this.currentSearchTarget}" 相关的图谱关系。`;
                }

                this.updateStatistics();
                this.renderGraph();
            } catch (error) {
                console.error('加载图谱数据失败:', error);
                alert('图谱数据加载失败: ' + error.message);
            } finally {
                this.loading = false;
                if (driver) {
                    await driver.close();
                }
            }
        },

        async loadRelatedNodes(session, nodes, links, searchParams) {
            this.totalPersonRelations = 0;
            this.totalPlaceRelations = 0;
            const targetName = searchParams.name;
            const targetType = searchParams.type;

            if (this.showPersonRelations && targetType === 'person') {
                const personResult = await session.run(
                    `MATCH (target:Person {name: $targetName})-[r]-(related:Person) RETURN target, related, r`,
                    { targetName }
                );
                personResult.records.forEach(record => this.processRecord(record, nodes, links, 'person'));
                this.totalPersonRelations = personResult.records.length;
            }

            if (this.showPlaceRelations) {
                let placeQuery, queryParams;
                if (targetType === 'person') {
                    placeQuery = `MATCH (p:Person {name: $targetName})-[r]-(pl:Place) RETURN p as target, pl as related, r`;
                    queryParams = { targetName };
                } else {
                    placeQuery = `MATCH (pl:Place)-[r]-(p:Person) WHERE pl.name = $targetName OR pl.place = $targetName RETURN pl as target, p as related, r`;
                    queryParams = { targetName };
                }
                const placeResult = await session.run(placeQuery, queryParams);
                placeResult.records.forEach(record => this.processRecord(record, nodes, links, 'place'));
                this.totalPlaceRelations = placeResult.records.length;
            }
        },

        processRecord(record, nodes, links, relationType) {
            const targetNodeData = record.get('target');
            const relatedNodeData = record.get('related');
            const relationship = record.get('r');

            const addNode = (nodeData, isTarget = false) => {
                if (!nodes.has(nodeData.properties.id)) {
                    const isPlace = nodeData.labels.includes('Place');
                    const nodeName = isPlace ? (nodeData.properties.place || nodeData.properties.name) : nodeData.properties.name;
                    const node = {
                        id: nodeData.properties.id,
                        name: nodeName,
                        type: isPlace ? 'place' : 'person',
                        symbolSize: isTarget ? 60 : 35,
                        itemStyle: { color: isTarget ? '#FF6B6B' : (isPlace ? '#4682B4' : '#8B4513') },
                        label: { show: true, fontSize: 12, color: '#2c3e50', fontWeight: 'bold' }
                    };
                    if (isTarget) {
                        node.label.fontSize = 16;
                    }
                    nodes.set(node.id, node);
                }
            };

            addNode(targetNodeData, true);
            addNode(relatedNodeData, false);

            // 获取节点名称
            const targetNodeName = targetNodeData.labels.includes('Place') ?
                (targetNodeData.properties.place || targetNodeData.properties.name) :
                targetNodeData.properties.name;
            const relatedNodeName = relatedNodeData.labels.includes('Place') ?
                (relatedNodeData.properties.place || relatedNodeData.properties.name) :
                relatedNodeData.properties.name;

            // 多重关系处理逻辑
            const sourceId = targetNodeData.properties.id;
            const targetId = relatedNodeData.properties.id;
            const relationshipType = relationship.properties.type || (relationType === 'person' ? '人人关系' : '人地关系');

            // 创建节点对的唯一标识（确保一致的顺序）
            const nodePairKey = sourceId < targetId ? `${sourceId}-${targetId}` : `${targetId}-${sourceId}`;

            // 记录这对节点之间的关系
            if (!this.nodePairRelations.has(nodePairKey)) {
                this.nodePairRelations.set(nodePairKey, []);
            }

            const existingRelations = this.nodePairRelations.get(nodePairKey);
            const relationIndex = existingRelations.length;

            // 添加关系到记录中
            existingRelations.push({
                type: relationshipType,
                source: sourceId,
                target: targetId,
                sourceName: targetNodeName,
                targetName: relatedNodeName
            });

            // 计算曲线参数以避免重叠
            const curveness = this.calculateCurveness(relationIndex);

            // 创建关系链接，包含唯一ID
            const linkId = `${sourceId}-${targetId}-${relationshipType}-${relationIndex}`;
            links.push({
                id: linkId,
                source: sourceId,
                target: targetId,
                name: relationshipType,
                sourceName: targetNodeName,
                targetName: relatedNodeName,
                relationIndex: relationIndex,
                curveness: curveness,
                lineStyle: {
                    color: relationType === 'person' ? '#8B4513' : '#4682B4',
                    width: 2,
                    opacity: 0.8,
                    curveness: curveness
                }
            });
        },

        calculateCurveness(relationIndex) {
            if (relationIndex === 0) return 0; // 第一条关系保持直线

            // 根据关系序号计算不同的弯曲度
            const baseOffset = 0.15;
            const direction = relationIndex % 2 === 1 ? 1 : -1; // 交替方向弯曲
            const magnitude = Math.ceil(relationIndex / 2) * baseOffset;

            return direction * magnitude;
        },

        getNodePairRelations(sourceId, targetId) {
            const nodePairKey = sourceId < targetId ? `${sourceId}-${targetId}` : `${targetId}-${sourceId}`;
            return this.nodePairRelations.get(nodePairKey) || [];
        },

        // 🔧 修改：loadAllRelations方法，增加数据量控制
        async loadAllRelations(session, nodes, links) {
            this.totalPersonRelations = 0;
            this.totalPlaceRelations = 0;

            // 🆕 新增：首先获取总的关系数量
            await this.getTotalRelationCount(session);

            // 🆕 新增：根据设置决定查询的LIMIT
            const queryLimit = this.showAllData ? '' : `LIMIT ${this.dataLimit}`;

            const processAllRecords = (records, type) => {
                records.forEach(record => {
                    const sourceNode = record.get('m');
                    const targetNode = record.get('n');
                    const relationship = record.get('r');

                    // 添加节点
                    [sourceNode, targetNode].forEach(nodeData => {
                        if (!nodes.has(nodeData.properties.id)) {
                            const isPlace = nodeData.labels.includes('Place');
                            const nodeName = isPlace ? (nodeData.properties.place || nodeData.properties.name) : nodeData.properties.name;
                            nodes.set(nodeData.properties.id, {
                                id: nodeData.properties.id,
                                name: nodeName,
                                type: isPlace ? 'place' : 'person',
                                symbolSize: 20,
                                itemStyle: { color: isPlace ? '#4682B4' : '#8B4513' },
                                label: { show: true, fontSize: 12, color: '#2c3e50' }
                            });
                        }
                    });

                    // 使用新的多重关系处理逻辑
                    const sourceNodeName = sourceNode.labels.includes('Place') ?
                        (sourceNode.properties.place || sourceNode.properties.name) :
                        sourceNode.properties.name;
                    const targetNodeName = targetNode.labels.includes('Place') ?
                        (targetNode.properties.place || targetNode.properties.name) :
                        targetNode.properties.name;

                    const sourceId = sourceNode.properties.id;
                    const targetId = targetNode.properties.id;
                    const relationshipType = relationship.properties.type || (type === 'person' ? '人人关系' : '人地关系');

                    // 节点对的唯一标识
                    const nodePairKey = sourceId < targetId ? `${sourceId}-${targetId}` : `${targetId}-${sourceId}`;

                    if (!this.nodePairRelations.has(nodePairKey)) {
                        this.nodePairRelations.set(nodePairKey, []);
                    }

                    const existingRelations = this.nodePairRelations.get(nodePairKey);
                    const relationIndex = existingRelations.length;

                    existingRelations.push({
                        type: relationshipType,
                        source: sourceId,
                        target: targetId,
                        sourceName: sourceNodeName,
                        targetName: targetNodeName
                    });

                    const curveness = this.calculateCurveness(relationIndex);
                    const linkId = `${sourceId}-${targetId}-${relationshipType}-${relationIndex}`;

                    links.push({
                        id: linkId,
                        source: sourceId,
                        target: targetId,
                        name: relationshipType,
                        sourceName: sourceNodeName,
                        targetName: targetNodeName,
                        relationIndex: relationIndex,
                        curveness: curveness,
                        lineStyle: {
                            color: type === 'person' ? '#8B4513' : '#4682B4',
                            width: 1.5,
                            opacity: 0.7,
                            curveness: curveness
                        }
                    });
                });
            };

            if (this.showPersonRelations) {
                const personQuery = `MATCH (m:Person)-[r]->(n:Person) RETURN m, n, r ${queryLimit}`;
                const personResult = await session.run(personQuery);
                processAllRecords(personResult.records, 'person');
                this.totalPersonRelations = personResult.records.length;
            }
            if (this.showPlaceRelations) {
                const placeQuery = `MATCH (m:Person)-[r]->(n:Place) RETURN m, n, r ${queryLimit}`;
                const placeResult = await session.run(placeQuery);
                processAllRecords(placeResult.records, 'place');
                this.totalPlaceRelations = placeResult.records.length;
            }
        },

        // 🆕 新增：获取数据库中总的关系数量
        async getTotalRelationCount(session) {
            try {
                let totalCount = 0;

                if (this.showPersonRelations) {
                    const personCountResult = await session.run(`MATCH (m:Person)-[r]->(n:Person) RETURN count(r) as count`);
                    totalCount += personCountResult.records[0].get('count').toNumber();
                }

                if (this.showPlaceRelations) {
                    const placeCountResult = await session.run(`MATCH (m:Person)-[r]->(n:Place) RETURN count(r) as count`);
                    totalCount += placeCountResult.records[0].get('count').toNumber();
                }

                this.totalAvailableRelations = totalCount;
            } catch (error) {
                console.error('获取总关系数量失败:', error);
                this.totalAvailableRelations = 0;
            }
        },

        renderGraph() {
            if (!this.myChart) return;
            const isSearchMode = !!this.currentSearchTarget;

            const option = {
                backgroundColor: 'transparent',
                tooltip: {
                    show: true,
                    trigger: 'item',
                    triggerOn: 'mousemove',
                    formatter: (params) => {
                        if (params.dataType === 'edge') {
                            const sourceId = params.data.source;
                            const targetId = params.data.target;
                            const allRelations = this.getNodePairRelations(sourceId, targetId);

                            if (allRelations.length === 1) {
                                return `<span style="color: #333; font-size: 14px; font-weight: bold;">${params.data.name}</span>`;
                            } else if (allRelations.length > 1) {
                                const relationTypes = [...new Set(allRelations.map(r => r.type))];
                                return `
                                    <div style="max-width: 250px; line-height: 1.4;">
                                        <div style="color: #333; font-size: 14px; font-weight: bold; margin-bottom: 5px;">
                                            多重关系 (${allRelations.length}个)
                                        </div>
                                        <div style="color: #666; font-size: 12px;">
                                            ${relationTypes.map(type => {
                                    const count = allRelations.filter(r => r.type === type).length;
                                    return `• ${type} ${count > 1 ? `(${count}个)` : ''}`;
                                }).join('<br/>')}
                                        </div>
                                        <div style="color: #999; font-size: 11px; margin-top: 5px;">
                                            ${allRelations[0].sourceName} ↔ ${allRelations[0].targetName}
                                        </div>
                                    </div>
                                `;
                            }
                            return `<span style="color: #333; font-size: 14px; font-weight: bold;">${params.data.name}</span>`;
                        }
                        return null;
                    },
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    borderColor: '#ccc',
                    borderWidth: 1,
                    textStyle: {
                        color: '#333'
                    }
                },
                series: [{
                    type: 'graph',
                    layout: 'force',
                    data: this.graphData.nodes,
                    links: this.graphData.links,
                    roam: true,
                    focusNodeAdjacency: true,
                    draggable: true,

                    // 🔧 修改：根据数据量调整力导向参数
                    force: {
                        repulsion: isSearchMode ? 3000 : (this.showAllData ? 300 : 500),
                        gravity: 0.05,
                        edgeLength: isSearchMode ? 250 : (this.showAllData ? 100 : 150),
                        layoutAnimation: true
                    },
                    label: {
                        show: this.graphData.nodes.length < (this.showAllData ? 100 : 200),
                        position: 'right',
                        formatter: '{b}'
                    },
                    edgeSymbol: ['', 'arrow'],
                    edgeSymbolSize: 8,
                    lineStyle: {
                        opacity: 0.6,
                        curveness: 0.1
                    },
                    emphasis: {
                        lineStyle: {
                            width: 4,
                            opacity: 1
                        }
                    }
                }]
            };

            this.myChart.setOption(option, true);

            // 事件处理
            this.myChart.off('click').on('click', (params) => {
                if (params.dataType === 'node') {
                    this.selectNode(params.data);
                }
            });

            this.myChart.off('mouseover').on('mouseover', (params) => {
                if (params.dataType === 'node') {
                    this.showNodeTooltip(params.data, params.event);
                }
            });

            this.myChart.off('mouseout').on('mouseout', (params) => {
                if (params.dataType === 'node') {
                    this.hoveredNode = null;
                }
            });
        },

        // 🆕 新增：切换数据显示模式
        toggleDataLimit() {
            this.showAllData = !this.showAllData;

            // 显示加载提示
            if (this.showAllData) {
                const confirmResult = confirm(`即将加载全部数据，这可能需要较长时间并占用较多内存。是否继续？`);
                if (!confirmResult) {
                    this.showAllData = false;
                    return;
                }
            }

            // 重新加载数据
            this.loadGraphData(this.currentSearchTarget ?
                { name: this.currentSearchTarget, id: this.searchTargetId, type: this.searchTargetType } : null);
        },

        // 🆕 新增：数据限制改变处理
        onDataLimitChange() {
            if (!this.showAllData) {
                this.loadGraphData(this.currentSearchTarget ?
                    { name: this.currentSearchTarget, id: this.searchTargetId, type: this.searchTargetType } : null);
            }
        },

        toggleRelationType(type) {
            if (type === 'person') this.showPersonRelations = !this.showPersonRelations;
            else this.showPlaceRelations = !this.showPlaceRelations;
            this.loadGraphData(this.currentSearchTarget ? { name: this.currentSearchTarget, id: this.searchTargetId, type: this.searchTargetType } : null);
        },

        toggleSearchPanel() { this.showSearchPanel = !this.showSearchPanel; },

        resetGraph() {
            this.searchKeyword = '';
            this.searchSuggestions = [];
            this.selectedNode = null;
            this.loadGraphData(null);
        },

        performSearch() {
            const keyword = this.searchKeyword.trim();
            if (keyword) {
                let matchedNode = this.allNodes.find(node => node.name.toLowerCase() === keyword.toLowerCase());
                if (!matchedNode) {
                    matchedNode = this.allNodes.find(node => node.name.toLowerCase().includes(keyword.toLowerCase()));
                }

                if (matchedNode) {
                    this.searchKeyword = matchedNode.name;
                    this.searchSuggestions = [];
                    this.loadGraphData({ name: matchedNode.name, id: matchedNode.id, type: matchedNode.type });
                } else {
                    alert(`在知识库中未找到名为 "${keyword}" 的节点。`);
                }
            }
        },

        clearSearch() {
            this.resetGraph();
        },

        onSearchInput() {
            if (this.searchKeyword.length > 0) {
                this.searchSuggestions = this.allNodes
                    .filter(node => node.name.toLowerCase().includes(this.searchKeyword.toLowerCase()))
                    .slice(0, 8);
            } else {
                this.searchSuggestions = [];
            }
        },

        selectSuggestion(suggestion) {
            this.searchKeyword = suggestion.name;
            this.searchSuggestions = [];
            this.loadGraphData({ name: suggestion.name, id: suggestion.id, type: suggestion.type });
        },

        selectNode(nodeData) {
            const nodeLinks = this.graphData.links.filter(l => l.source === nodeData.id || l.target === nodeData.id);

            // 统计不同类型的关系
            const relationStats = new Map();
            nodeLinks.forEach(link => {
                const relationType = link.name;
                if (!relationStats.has(relationType)) {
                    relationStats.set(relationType, 0);
                }
                relationStats.set(relationType, relationStats.get(relationType) + 1);
            });

            const relations = Array.from(relationStats.entries()).map(([type, count]) => ({
                type: type,
                count: count,
                key: `${type}-${count}`
            }));

            this.selectedNode = {
                name: nodeData.name,
                type: nodeData.type,
                degree: nodeLinks.length,
                relations: relations
            };
        },

        showNodeTooltip(nodeData, event) {
            this.hoveredNode = {
                name: nodeData.name,
                type: nodeData.type,
                degree: this.graphData.links.filter(l => l.source === nodeData.id || l.target === nodeData.id).length
            };
            this.tooltipStyle = { left: `${event.offsetX + 15}px`, top: `${event.offsetY - 50}px` };
        },

        updateStatistics() {
            this.nodeCount = this.graphData.nodes.length;
            this.relationCount = this.graphData.links.length;
            this.totalPersons = this.graphData.nodes.filter(n => n.type === 'person').length;
            this.totalPlaces = this.graphData.nodes.filter(n => n.type === 'place').length;
        }
    },

    beforeDestroy() {
        this.myChart?.dispose();
        if (this.handleResize) {
            window.removeEventListener('resize', this.handleResize);
        }
    }
}
</script>

<style lang="less" scoped>
/* 原有样式保持不变，添加新的样式 */
.search-result-message {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(255, 255, 255, 0.9);
    padding: 20px 40px;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    color: #8B4513;
    font-size: 18px;
    font-weight: bold;
    z-index: 500;
    text-align: center;
}

.knowledge-graph-page {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}

.main-content {
    flex: 1;
    position: relative;
    background-image: url('../../images/mainbg.jpg');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
    overflow: hidden;
    min-height: calc(100vh - 160px);
}

.graph-canvas {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 1;
}

.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;

    .loading-spinner {
        text-align: center;

        .spinner {
            width: 60px;
            height: 60px;
            border: 6px solid rgba(243, 243, 243, 0.8);
            border-top: 6px solid #8B4513;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }

        p {
            color: #666;
            margin: 0;
            font-size: 18px;
            font-weight: 500;
        }
    }
}

.top-toolbar {
    position: absolute;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: 15px; // 🔧 减小间距以容纳更多按钮
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(15px);
    padding: 15px 30px;
    border-radius: 50px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    z-index: 100;
    max-width: 90vw; // 🆕 新增：防止工具栏过宽
    overflow-x: auto; // 🆕 新增：必要时允许水平滚动

    .graph-title {
        margin: 0;
        font-size: 24px;
        color: #8B4513;
        font-weight: bold;
        white-space: nowrap; // 🆕 新增：防止标题换行
    }

    .toolbar-buttons {
        display: flex;
        gap: 8px; // 🔧 减小按钮间距
        flex-wrap: nowrap; // 🆕 新增：防止按钮换行
    }

    .btn {
        padding: 8px 16px; // 🔧 略微减小按钮padding
        border: 2px solid transparent;
        background: rgba(255, 255, 255, 0.8);
        border-radius: 25px;
        cursor: pointer;
        transition: all 0.3s ease;
        font-weight: 500;
        white-space: nowrap;
        font-size: 14px; // 🔧 略微减小字体

        &:hover {
            background: rgba(248, 249, 250, 0.9);
            transform: translateY(-1px);
        }

        &.active {
            background: #8B4513;
            color: white;
            box-shadow: 0 4px 12px rgba(139, 69, 19, 0.3);
        }

        &.btn-reset {
            background: #6c757d;
            color: white;

            &:hover {
                background: #5a6268;
            }
        }

        &.btn-search {
            background: #DAA520;
            color: white;

            &:hover {
                background: #B8941F;
            }
        }

        // 🆕 新增：数据限制按钮样式
        &.btn-limit {
            background: #28a745;
            color: white;

            &:hover {
                background: #218838;
            }

            &.active {
                background: #dc3545;

                &:hover {
                    background: #c82333;
                }
            }

            .data-count {
                font-size: 12px;
                opacity: 0.9;
                margin-left: 4px;
            }
        }
    }
}

.side-legend {
    position: absolute;
    top: 100px;
    left: 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(15px);
    padding: 20px;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    min-width: 200px;
    z-index: 50;

    h4 {
        margin: 0 0 15px 0;
        color: #8B4513;
        font-size: 16px;
        font-weight: bold;
    }

    .legend-item {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        font-size: 13px;

        &.search-target {
            border-top: 1px solid #ddd;
            padding-top: 8px;
            margin-top: 10px;
            font-weight: bold;
        }

        // 🆕 新增：数据状态样式
        &.data-status {
            border-top: 1px solid #ddd;
            padding-top: 8px;
            margin-top: 10px;
            font-style: italic;
            color: #666;
        }

        .legend-dot {
            width: 14px;
            height: 14px;
            border-radius: 50%;
            margin-right: 10px;

            &.person-node {
                background-color: #8B4513;
            }

            &.place-node {
                background-color: #4682B4;
            }

            &.search-target-node {
                background-color: #FF6B6B;
            }
        }

        // 🆕 新增：图例信息图标
        .legend-info {
            margin-right: 10px;
            font-size: 14px;
        }
    }
}

.search-panel {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 350px;
    max-height: 80vh;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(15px);
    border-radius: 20px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    overflow-y: auto;
    z-index: 100;

    .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        border-bottom: 1px solid rgba(222, 226, 230, 0.5);

        h3 {
            margin: 0;
            color: #8B4513;
            font-size: 18px;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #666;
            padding: 0;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;

            &:hover {
                background: rgba(0, 0, 0, 0.1);
            }
        }
    }

    .search-section,
    .node-info-section,
    .data-control-section {
        // 🆕 新增：数据控制面板样式
        padding: 20px;
        border-bottom: 1px solid rgba(222, 226, 230, 0.3);

        h4 {
            margin: 0 0 15px 0;
            color: #8B4513;
            font-size: 14px;
            font-weight: bold;
        }

        .search-input-group {
            display: flex;
            gap: 8px;
            margin-bottom: 10px;

            .search-input {
                flex: 1;
                padding: 10px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 14px;

                &:focus {
                    outline: none;
                    border-color: #8B4513;
                }
            }

            .search-btn {
                padding: 10px 16px;
                background: #8B4513;
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;

                &:hover {
                    background: #654321;
                }
            }
        }

        .search-suggestions {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            max-height: 200px;
            overflow-y: auto;

            .suggestion-item {
                padding: 10px;
                cursor: pointer;
                border-bottom: 1px solid #f5f5f5;

                &:hover {
                    background: #f8f9fa;
                }

                &:last-child {
                    border-bottom: none;
                }

                .suggestion-type {
                    color: #666;
                    font-size: 12px;
                }
            }
        }

        .search-status {
            background: #e8f5e8;
            padding: 10px;
            border-radius: 8px;
            margin-top: 10px;

            p {
                margin: 0 0 5px 0;
                font-size: 14px;
            }

            .btn-clear-search {
                background: #dc3545;
                color: white;
                border: none;
                padding: 5px 10px;
                border-radius: 4px;
                cursor: pointer;
                font-size: 12px;
                margin-top: 10px;

                &:hover {
                    background: #c82333;
                }
            }
        }

        .node-info div {
            margin-bottom: 10px;
            font-size: 14px;

            strong {
                color: #8B4513;
            }
        }

        .relation-list {
            margin: 8px 0 0 20px;
            padding: 0;

            li {
                list-style: disc;
                margin: 4px 0;
                font-size: 13px;
                color: #666;
            }
        }

        // 🆕 新增：数据控制面板特定样式
        .control-group {
            margin-bottom: 15px;

            label {
                display: block;
                margin-bottom: 5px;
                font-size: 13px;
                color: #666;
                font-weight: 500;
            }

            select {
                width: 100%;
                padding: 8px;
                border: 2px solid #ddd;
                border-radius: 6px;
                font-size: 14px;

                &:focus {
                    outline: none;
                    border-color: #8B4513;
                }

                &:disabled {
                    background-color: #f5f5f5;
                    color: #999;
                    cursor: not-allowed;
                }
            }

            .btn-toggle-all {
                width: 100%;
                padding: 12px;
                border: none;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                background: #28a745;
                color: white;

                &:hover {
                    background: #218838;
                    transform: translateY(-1px);
                }

                &.active {
                    background: #dc3545;

                    &:hover {
                        background: #c82333;
                    }
                }
            }
        }

        .data-warning {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 6px;
            padding: 10px;
            margin-top: 10px;

            p {
                margin: 0;
                font-size: 12px;
                color: #856404;
                line-height: 1.4;
            }
        }
    }
}

.bottom-stats {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(15px);
    padding: 15px 25px;
    border-radius: 50px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    z-index: 50;

    .stat-item {
        text-align: center;

        &.stat-limited {

            // 🆕 新增：部分数据模式的视觉指示
            .stat-number {
                color: #dc3545;
            }
        }

        .stat-number {
            display: block;
            font-size: 20px;
            font-weight: bold;
            color: #8B4513;
            margin-bottom: 2px;
        }

        .stat-label {
            font-size: 12px;
            color: #666;
        }
    }
}

.node-tooltip {
    position: absolute;
    background: rgba(0, 0, 0, 0.85);
    backdrop-filter: blur(10px);
    color: white;
    padding: 12px 16px;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
    z-index: 200;
    pointer-events: none;

    h5 {
        margin: 0 0 8px 0;
        font-size: 16px;
        color: #DAA520;
    }

    p {
        margin: 4px 0;
        font-size: 14px;
    }
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>