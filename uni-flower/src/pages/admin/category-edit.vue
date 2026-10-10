<template>
  <view class="edit-page">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">分类名称 <text class="required">*</text></text>
        <input v-model="form.name" class="form-input" placeholder="请输入分类名称" placeholder-class="ph" />
      </view>

      <view class="form-item">
        <text class="form-label">分类类型 <text class="required">*</text></text>
        <picker :range="typeLabels" :value="typeIndex" @change="onTypeChange">
          <view class="form-picker">
            <text :class="{ 'ph': !form.type }">{{ form.type ? typeLabels[typeIndex] : '请选择分类类型' }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">排序</text>
        <input v-model="form.sort" class="form-input" type="number" placeholder="数字越小越靠前" placeholder-class="ph" />
      </view>

      <view class="form-item">
        <text class="form-label">状态</text>
        <switch :checked="form.status === 1" @change="onStatusChange" color="#0a84ff" />
      </view>
    </view>

    <view class="bottom-bar">
      <button class="save-btn" :loading="saving" :disabled="saving" @click="handleSave">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { createCategory, updateCategory } from '@/api/admin/category.js'
import { showToast } from '@/utils/index.js'

const typeOptions = [
  { value: 1, label: '鲜花商品' },
  { value: 2, label: '节日多花礼盒' },
  { value: 3, label: '礼品' }
]
const typeLabels = typeOptions.map((o) => o.label)

const isEdit = ref(false)
const typeIndex = ref(0)
const saving = ref(false)

const form = reactive({
  id: null,
  name: '',
  type: null,
  sort: 0,
  status: 1
})

const onTypeChange = (e) => {
  typeIndex.value = e.detail.value
  form.type = typeOptions[typeIndex.value].value
}

const onStatusChange = (e) => {
  form.status = e.detail.value ? 1 : 0
}

const handleSave = async () => {
  if (!form.name.trim()) {
    showToast('请输入分类名称')
    return
  }
  if (!form.type) {
    showToast('请选择分类类型')
    return
  }

  saving.value = true
  try {
    const payload = { ...form, sort: Number(form.sort) || 0 }
    if (isEdit.value) {
      await updateCategory(payload)
      showToast('修改成功', 'success')
    } else {
      await createCategory(payload)
      showToast('新增成功', 'success')
    }
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    // ignore
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}
  if (options.id) {
    isEdit.value = true
    try {
      const data = JSON.parse(decodeURIComponent(options.data || '{}'))
      Object.assign(form, data)
      const idx = typeOptions.findIndex((o) => o.value === form.type)
      if (idx >= 0) typeIndex.value = idx
    } catch (e) { /* ignore */ }
  }
})
</script>

<style lang="scss" scoped>
.edit-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 160rpx;
}

.form-section {
  background: #fff;
  margin-top: 20rpx;
  padding: 0 32rpx;
}

.form-item {
  display: flex;
  align-items: center;
  min-height: 100rpx;
  border-bottom: 1rpx solid #f0f0f0;
  padding: 20rpx 0;

  &:last-child {
    border-bottom: none;
  }

  .form-label {
    width: 200rpx;
    font-size: 28rpx;
    color: #3c3c43;
    flex-shrink: 0;

    .required {
      color: #ff453a;
    }
  }

  .form-input {
    flex: 1;
    font-size: 28rpx;
    color: #1c1c1e;
  }

  .form-picker {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 28rpx;
    color: #1c1c1e;

    .picker-arrow {
      font-size: 20rpx;
      color: #c7c7cc;
    }
  }
}

.ph {
  color: #c7c7cc;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.save-btn {
  width: 100%;
  height: 88rpx;
  background: #0a84ff;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;

  &[disabled] {
    opacity: 0.6;
  }
}
</style>
