<template>
  <el-table
    :data="data"
    stripe
    v-loading="loading"
    class="admin-table"
    :header-cell-style="headerCellStyle"
  >
    <!-- 表格列由调用方通过默认插槽传入 -->
    <slot />
  </el-table>
</template>

<script setup>
// 统一数据表格：内置表头样式、斑马纹、loading
// 未显式声明的属性（如 border、height 等）会自动透传到 el-table 根元素
defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// 统一表头样式：系统蓝浅底 + 蓝色加粗文字
const headerCellStyle = {
  background: 'rgba(10, 132, 255, 0.1)',
  color: '#0A84FF',
  fontWeight: 'bold'
}
</script>

<style lang="scss" scoped>
.admin-table {
  width: 100%;
  margin-bottom: 20px;
}
</style>
