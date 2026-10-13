<template>
  <div class="dashboard-container">
    <!-- 顶部：鲜花热销榜条形统计图 -->
    <div class="chart-card">
      <header class="chart-header">
        <div class="header-title">
          <h3>鲜花热销榜 TopN</h3>
        </div>
        <span class="status-badge">鲜花</span>
      </header>
      <section class="chart-container">
        <div ref="flowerChartRef" class="chart-dom"></div>
      </section>
    </div>

    <!-- 底部：多花礼盒热销榜条形统计图 -->
    <div class="chart-card festival">
      <header class="chart-header">
        <div class="header-title">
          <h3>多花礼盒热销榜 TopN</h3>
        </div>
        <span class="status-badge festival-badge">礼盒</span>
      </header>
      <section class="chart-container">
        <div ref="festivalChartRef" class="chart-dom"></div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
// 引入 ECharts 核心
import * as echarts from 'echarts'
// 按任务要求使用的 API 函数
import { getTop1, getTop2 } from '@/api/admin/statistics.js'

// ===== 图表 DOM ref 与实例 =====
const flowerChartRef = ref(null)
const festivalChartRef = ref(null)
let flowerChart = null
let festivalChart = null

// ===== 通用条形统计图渲染 =====
const renderBarChart = (chart, list, color) => {
  if (!chart) return
  const names = list.map(d => d.name || '未知')
  const counts = list.map(d => Number(d.count) || 0)

  chart.setOption({
    animationDuration: 1000,
    animationEasing: 'cubicOut',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisTick: { alignWithLabel: true },
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#6b7280', rotate: names.length > 6 ? 30 : 0, interval: 0 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { type: 'dashed', color: '#f3f4f6' } }
    },
    series: [{
      name: '销量',
      type: 'bar',
      barWidth: '60%',
      data: counts,
      // 圆角柱
      itemStyle: { color, borderRadius: [4, 4, 0, 0] },
      showBackground: true,
      backgroundStyle: { color: 'rgba(180, 180, 180, 0.1)' }
    }]
  }, true)
}

// ===== 数据获取 =====
const fetchFlower = async () => {
  try {
    const res = await getTop1()
    if (Array.isArray(res?.data)) renderBarChart(flowerChart, res.data, '#667eea')
  } catch (e) {
    console.error('获取鲜花热销榜失败:', e)
  }
}

const fetchFestival = async () => {
  try {
    const res = await getTop2()
    if (Array.isArray(res?.data)) renderBarChart(festivalChart, res.data, '#5470c6')
  } catch (e) {
    console.error('获取礼盒热销榜失败:', e)
  }
}

// ===== 窗口自适应 =====
const handleResize = () => {
  flowerChart?.resize()
  festivalChart?.resize()
}

// ===== 生命周期 =====
onMounted(async () => {
  if (flowerChartRef.value) flowerChart = echarts.init(flowerChartRef.value)
  if (festivalChartRef.value) festivalChart = echarts.init(festivalChartRef.value)
  window.addEventListener('resize', handleResize)
  await Promise.all([fetchFlower(), fetchFestival()])
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  flowerChart?.dispose()
  festivalChart?.dispose()
})
</script>

<style lang="scss" scoped>
/* 系统色板 */
$sys-blue: #0A84FF;
$sys-indigo: #5E5CE6;

.dashboard-container {
  margin: 20px;
  background: linear-gradient(135deg, rgba(10, 132, 255, 0.08) 0%, rgba(10, 132, 255, 0.15) 100%);
  min-height: calc(100vh - 100px);
  padding: 20px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ECharts 图表卡片 */
.chart-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(10, 132, 255, 0.15);
  overflow: hidden;
  border-top: 4px solid #667eea; /* 鲜花柱状图卡片：紫色系主题 */

  &.festival {
    border-top-color: #5470c6; /* 礼盒柱状图卡片：蓝色系主题 */
  }
}

.chart-header {
  padding: 24px 32px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;

  @media (max-width: 600px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 16px;
  }

  .header-title {
    h3 {
      font-size: 1.5rem;
      font-weight: 700;
      color: $sys-indigo;
      margin: 0 0 4px 0;
    }

    p {
      color: rgba(94, 92, 230, 0.55);
      font-size: 0.875rem;
      margin: 0;
    }
  }

  .status-badge {
    background-color: #d1fae5;
    color: #065f46;
    padding: 4px 12px;
    border-radius: 9999px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;

    &.festival-badge {
      background-color: #dbeafe;
      color: #1e40af;
    }
  }
}

.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
  padding: 20px;
  background-color: #fff;

  @media (max-width: 600px) {
    height: 300px;
    padding: 10px;
  }
}

.chart-dom {
  width: 100%;
  height: 100%;
}
</style>
