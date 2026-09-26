<template>
  <el-pagination
    v-if="total > 0"
    class="admin-pagination"
    v-model:current-page="currentPage"
    v-model:page-size="pageSizeModel"
    :page-sizes="pageSizes"
    layout="total, sizes, prev, pager, next, jumper"
    :total="total"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
  />
</template>

<script setup>
import { computed } from 'vue'

// 统一分页组件：内置布局、页码尺寸与激活色样式
// 支持 v-model:page / v-model:page-size 双向绑定
const props = defineProps({
  page: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 30, 40]
  }
})

const emit = defineEmits([
  'update:page',
  'update:pageSize',
  'size-change',
  'current-change'
])

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const handleSizeChange = (val) => emit('size-change', val)
const handleCurrentChange = (val) => emit('current-change', val)
</script>

<style lang="scss" scoped>
/* 系统色板变量已全局注入，可直接使用 $primary 等 */

.admin-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;

  :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
    background-color: $primary;
  }

  :deep(.el-pagination.is-background .btn-prev:hover),
  :deep(.el-pagination.is-background .btn-next:hover) {
    color: $primary;
  }
}
</style>
