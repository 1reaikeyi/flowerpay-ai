<template>
  <div class="dashboard-container">
    <!-- 今日数据：订单状态分布扇形图 -->
    <div class="chart-card">
      <header class="chart-header">
        <div class="header-title">
          <h3>今日数据</h3>
        </div>
        <span class="status-badge">今日</span>
      </header>
      <section class="chart-container">
        <div ref="chartRef" class="chart-dom"></div>
      </section>
      <footer class="chart-footer info-panel">
        <div class="info-item">今日订单总数: <span>{{ totalOrders }}</span></div>
        <div class="info-item">状态种类: <span>{{ orderList.length }}</span></div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
// 引入 ECharts 核心
import * as echarts from 'echarts'
// 按任务要求使用的 API 函数
import { getTodayOrder } from '@/api/admin/statistics.js'

// orderList: 今日订单状态列表 OrderStatisticsVO { status, name, count }
const orderList = ref([])

// 今日订单总数：所有状态 count 之和
const totalOrders = computed(() =>
  orderList.value.reduce((sum, o) => sum + (Number(o.count) || 0), 0)
)

// ===== 图表 DOM ref 与实例 =====
const chartRef = ref(null)
let chart = null

// ===== 数据获取 =====
const fetchData = async () => {
  try {
    const res = await getTodayOrder()
    if (Array.isArray(res?.data)) orderList.value = res.data
    renderChart()
  } catch (e) {
    console.error('获取今日数据统计失败:', e)
  }
}

// ===== 扇形图渲染 =====
const renderChart = () => {
  if (!chart) return

  // 空数据兜底，避免扇形图空白
  if (orderList.value.length === 0) {
    chart.setOption({
      title: {
        text: '今日暂无订单',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999', fontSize: 18 }
      }
    }, true)
    return
  }

  const data = orderList.value.map(o => ({
    name: o.name || `状态${o.status}`,
    value: Number(o.count) || 0
  }))

  chart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151' },
      formatter: '{b}<br/>订单数: <b>{c}</b>（{d}%）'
    },
    legend: {
      bottom: 10,
      icon: 'circle',
      textStyle: { color: '#6b7280' }
    },
    series: [{
      name: '今日订单状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        color: '#374151',
        formatter: '{b}\n{c} 单'
      },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: 'bold' }
      },
      // 多色调色板
      color: ['#667eea', '#5470c6', '#0A84FF', '#FF9F0A', '#34c759', '#ff3b30', '#af52de'],
      data
    }]
  }, true)
}

// ===== 窗口自适应 =====
const handleResize = () => chart?.resize()

// ===== 生命周期 =====
onMounted(async () => {
  if (chartRef.value) chart = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
  await fetchData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
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
}

/* ECharts 图表卡片 */
.chart-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(10, 132, 255, 0.15);
  overflow: hidden;
  border-top: 4px solid $sys-blue; /* 今日数据卡片：蓝色系主题 */
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
  }
}

.chart-container {
  position: relative;
  height: 450px;
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

.chart-footer {
  padding: 16px 32px;
  background: #fafafa;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 24px;

  /* 底部信息面板 */
  &.info-panel {
    justify-content: flex-start;

    .info-item {
      font-size: 0.875rem;
      color: rgba(94, 92, 230, 0.55);

      span {
        font-weight: 600;
        color: $sys-indigo;
        margin-left: 4px;
      }
    }
  }

  @media (max-width: 600px) {
    flex-direction: column;
    gap: 12px;
    padding: 16px;
  }
}
</style>
