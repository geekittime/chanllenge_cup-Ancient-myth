<template>
    <!-- 模板部分保持不变 -->
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

                <div v-if="selectedNode" class="node-info-section">
                    <h4>节点信息</h4>
                    <div class="node-info">
                        <div><strong>名称：</strong>{{ selectedNode.name }}</div>
                        <div><strong>类型：</strong>{{ selectedNode.type === 'person' ? '人物' : '地点' }}</div>
                        <div><strong>连接数：</strong>{{ selectedNode.degree }}</div>
                    </div>
                </div>
            </div>

            <div class="bottom-stats">
                <div class="stat-item">
                    <span class="stat-number">{{ nodeCount }}</span>
                    <span class="stat-label">总节点</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ relationCount }}</span>
                    <span class="stat-label">总关系</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ totalPersons }}</span>
                    <span class="stat-label">人物</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ totalPlaces }}</span>
                    <span class="stat-label">地点</span>
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
            totalPlaceRelations: 0
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
                    const node = {
                        id: nodeData.properties.id,
                        name: isPlace ? (nodeData.properties.place || nodeData.properties.name) : nodeData.properties.name,
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

            links.push({
                source: targetNodeData.properties.id,
                target: relatedNodeData.properties.id,
                name: relationship.properties.type || (relationType === 'person' ? '人人关系' : '人地关系'),
                lineStyle: { color: relationType === 'person' ? '#8B4513' : '#4682B4', width: 2, opacity: 0.8 }
            });
        },

        async loadAllRelations(session, nodes, links) {
            this.totalPersonRelations = 0;
            this.totalPlaceRelations = 0;
            const processAllRecords = (records, type) => {
                records.forEach(record => {
                    const sourceNode = record.get('m');
                    const targetNode = record.get('n');
                    const relationship = record.get('r');
                    [sourceNode, targetNode].forEach(nodeData => {
                        if (!nodes.has(nodeData.properties.id)) {
                            const isPlace = nodeData.labels.includes('Place');
                            nodes.set(nodeData.properties.id, {
                                id: nodeData.properties.id,
                                name: isPlace ? (nodeData.properties.place || nodeData.properties.name) : nodeData.properties.name,
                                type: isPlace ? 'place' : 'person',
                                symbolSize: 20,
                                itemStyle: { color: isPlace ? '#4682B4' : '#8B4513' },
                                label: { show: true, fontSize: 12, color: '#2c3e50' }
                            });
                        }
                    });
                    links.push({
                        source: sourceNode.properties.id,
                        target: targetNode.properties.id,
                        name: relationship.properties.type || (type === 'person' ? '人人关系' : '人地关系'),
                        lineStyle: { color: type === 'person' ? '#8B4513' : '#4682B4', width: 1.5, opacity: 0.7 }
                    });
                });
            };

            // 【核心修改】大幅减少初始加载的数量，让图谱不再混乱。您可以根据需要调整这个 LIMIT 值。
            if (this.showPersonRelations) {
                const personResult = await session.run(`MATCH (m:Person)-[r]->(n:Person) RETURN m, n, r LIMIT 1000`);
                processAllRecords(personResult.records, 'person');
                this.totalPersonRelations = personResult.records.length;
            }
            if (this.showPlaceRelations) {
                const placeResult = await session.run(`MATCH (m:Person)-[r]->(n:Place) RETURN m, n, r LIMIT 1000`);
                processAllRecords(placeResult.records, 'place');
                this.totalPlaceRelations = placeResult.records.length;
            }
        },

        renderGraph() {
            if (!this.myChart) return;
            const isSearchMode = !!this.currentSearchTarget;

            const option = {
                backgroundColor: 'transparent',
                tooltip: { show: false },
                series: [{
                    type: 'graph',
                    layout: 'force', // 始终使用力导向布局
                    data: this.graphData.nodes,
                    links: this.graphData.links,
                    roam: true,
                    focusNodeAdjacency: true,
                    draggable: true,

                    // 【核心修改】为初始视图和搜索视图设置不同的力导向参数
                    force: {
                        repulsion: isSearchMode ? 3000 : 500, // 初始视图斥力减小，搜索视图斥力增大
                        gravity: 0.05,
                        edgeLength: isSearchMode ? 250 : 150, // 初始视图边长减小，搜索视图边长增大
                        layoutAnimation: true
                    },
                    label: {
                        show: this.graphData.nodes.length < 200, // 节点数量不多时显示标签
                        position: 'right',
                        formatter: '{b}'
                    },
                    edgeSymbol: ['', 'arrow'],
                    edgeSymbolSize: 8,
                    lineStyle: { opacity: 0.6, curveness: 0.1 }
                }]
            };

            this.myChart.setOption(option, true);
            this.myChart.off('click').on('click', (params) => params.dataType === 'node' && this.selectNode(params.data));
            this.myChart.off('mouseover').on('mouseover', (params) => params.dataType === 'node' && this.showNodeTooltip(params.data, params.event));
            this.myChart.off('mouseout').on('mouseout', () => this.hoveredNode = null);
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
            this.selectedNode = {
                name: nodeData.name,
                type: nodeData.type,
                degree: this.graphData.links.filter(l => l.source === nodeData.id || l.target === nodeData.id).length
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
/* 样式部分保持不变 */
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
    gap: 30px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(15px);
    padding: 15px 30px;
    border-radius: 50px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    z-index: 100;

    .graph-title {
        margin: 0;
        font-size: 24px;
        color: #8B4513;
        font-weight: bold;
    }

    .toolbar-buttons {
        display: flex;
        gap: 10px;
    }

    .btn {
        padding: 10px 20px;
        border: 2px solid transparent;
        background: rgba(255, 255, 255, 0.8);
        border-radius: 25px;
        cursor: pointer;
        transition: all 0.3s ease;
        font-weight: 500;
        white-space: nowrap;

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
    .node-info-section {
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