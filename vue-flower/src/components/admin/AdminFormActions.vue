<template>
  <el-form-item class="admin-form-actions">
    <el-button @click="handleCancel">取消</el-button>
    <el-button type="primary" :loading="submitting" @click="handleSubmit(false)">
      保存
    </el-button>
    <el-button
      v-if="showContinue"
      type="primary"
      plain
      :loading="submitting"
      @click="handleSubmit(true)"
    >
      保存并继续添加
    </el-button>
  </el-form-item>
</template>

<script setup>
// 表单底部操作按钮：取消 / 保存 / 保存并继续添加
// submitting：保存按钮 loading 状态
// showContinue：是否显示「保存并继续添加」（编辑模式通常为 false，由调用方控制）
defineProps({
  submitting: {
    type: Boolean,
    default: false
  },
  showContinue: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['cancel', 'submit'])

const handleCancel = () => emit('cancel')
// submit 事件携带 continue 参数：true=保存并继续添加，false=保存后返回
const handleSubmit = (continueAdd) => emit('submit', continueAdd)
</script>

<style lang="scss" scoped>
.admin-form-actions {
  margin-top: 30px;
  padding-top: 20px;
  /* 顶部分隔线使用系统蓝半透明 */
  border-top: 1px solid rgba(10, 132, 255, 0.2);
}
</style>
