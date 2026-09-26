<template>
  <div class="admin-list-page">
    <!-- 顶部工具栏：搜索表单（左） + 操作按钮（右） -->
    <div v-if="$slots.toolbar" class="admin-toolbar">
      <slot name="toolbar" />
    </div>
    <!-- 主内容区：通常为表格 -->
    <slot />
  </div>
</template>

<script setup>
// 列表页布局容器：统一外层背景、内边距与顶部工具栏样式
// 插槽：toolbar（顶部工具栏）、default（主内容，如 el-table）
</script>

<style lang="scss" scoped>
/* 系统色板变量已全局注入，可直接使用 $primary 等 */

.admin-list-page {
  padding: 20px;
  /* 容器背景使用系统蓝极浅透明度 */
  background: rgba(10, 132, 255, 0.04);
  border-radius: 4px;
  min-height: calc(100vh - 120px);
}

/* 顶部工具栏：左侧筛选 + 右侧操作 */
.admin-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  /* 工具栏背景使用主色浅背景 */
  background: $primary-light;
  border-radius: 4px;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}
</style>
