<template>
  <div class="admin-form-page">
    <!-- 页面头部：返回 + 标题 -->
    <div class="admin-page-header">
      <el-page-header @back="handleBack">
        <template #content>
          <span class="admin-page-title">{{ title }}</span>
        </template>
      </el-page-header>
    </div>

    <!-- 表单区域：由调用方通过默认插槽传入 el-form -->
    <div class="admin-form-container">
      <slot />
    </div>
  </div>
</template>

<script setup>
// 表单页（新增/编辑）布局容器：统一页面头部与表单区域样式
// title：页面标题（如 "添加分类" / "修改分类"）
// 事件 back：点击返回时触发，由调用方决定跳转目标
defineProps({
  title: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['back'])

const handleBack = () => emit('back')
</script>

<style lang="scss" scoped>
/* 系统色板变量已全局注入，可直接使用 $sys-indigo 等 */

.admin-form-page {
  padding: 20px;
  /* 容器背景使用系统蓝极浅透明度 */
  background: rgba(10, 132, 255, 0.04);
  border-radius: 4px;
}

.admin-page-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  /* 分隔线使用系统蓝半透明 */
  border-bottom: 1px solid rgba(10, 132, 255, 0.2);
}

.admin-page-title {
  font-size: 16px;
  font-weight: 500;
  /* 标题文字使用系统靛蓝 */
  color: $sys-indigo;
}

.admin-form-container {
  max-width: 800px;
  padding: 20px 0;
}
</style>
