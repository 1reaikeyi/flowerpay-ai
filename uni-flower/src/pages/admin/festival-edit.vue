<template>
  <view class="edit-page">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">礼盒名称 <text class="required">*</text></text>
        <input v-model="form.name" class="form-input" placeholder="请输入礼盒名称" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">分类 <text class="required">*</text></text>
        <picker :range="categoryLabels" :value="catIndex" @change="onCatChange">
          <view class="form-picker">
            <text :class="{ 'ph': !form.categoryId }">{{ form.categoryId ? categoryLabels[catIndex] : '请选择分类' }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">价格 <text class="required">*</text></text>
        <input v-model="form.price" class="form-input" type="digit" placeholder="请输入价格" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">图片</text>
        <view class="upload-area" @click="chooseImage">
          <image v-if="imageUrl" :src="imageUrl" mode="aspectFill" class="preview-img" />
          <view v-else class="upload-placeholder"><text class="upload-icon">📷</text><text class="upload-text">点击上传</text></view>
        </view>
      </view>
      <view class="form-item column">
        <text class="form-label">描述</text>
        <textarea v-model="form.description" class="form-textarea" placeholder="请输入描述" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">状态</text>
        <switch :checked="form.status === 1" @change="onStatusChange" color="#0a84ff" />
      </view>
    </view>
    <view class="bottom-bar">
      <button class="save-btn" :loading="saving" :disabled="saving" @click="handleSave">{{ saving ? '保存中...' : '保存' }}</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { createFestival, updateFestival } from '@/api/admin/festival.js'
import { getCategoryByType } from '@/api/admin/category.js'
import { uploadFile } from '@/api/file/file.js'
import { resolveImageUrl, showToast, showLoading, hideLoading } from '@/utils/index.js'

const isEdit = ref(false)
const catIndex = ref(0)
const saving = ref(false)
const categoryList = ref([])
const categoryLabels = ref([])
const imageUrl = ref('')

const form = reactive({ id: null, name: '', categoryId: null, price: '', image: '', description: '', status: 1 })

const fetchCategories = async () => {
  try {
    const res = await getCategoryByType(2)
    categoryList.value = res?.data || []
    categoryLabels.value = categoryList.value.map((c) => c.name)
  } catch (e) { /* ignore */ }
}

const onCatChange = (e) => { catIndex.value = e.detail.value; form.categoryId = categoryList.value[catIndex.value]?.id }
const onStatusChange = (e) => { form.status = e.detail.value ? 1 : 0 }

const chooseImage = () => {
  uni.chooseImage({ count: 1, success: async (res) => {
    try {
      showLoading()
      const uploadRes = await uploadFile(res.tempFilePaths[0])
      form.image = uploadRes.data
      imageUrl.value = resolveImageUrl(uploadRes.data)
      hideLoading()
      showToast('上传成功', 'success')
    } catch (e) { hideLoading() }
  }})
}

const handleSave = async () => {
  if (!form.name.trim()) return showToast('请输入礼盒名称')
  if (!form.categoryId) return showToast('请选择分类')
  if (!form.price) return showToast('请输入价格')
  saving.value = true
  try {
    const payload = { ...form, price: Number(form.price) }
    isEdit.value ? await updateFestival(payload) : await createFestival(payload)
    showToast('保存成功', 'success')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) { /* ignore */ }
  finally { saving.value = false }
}

onMounted(async () => {
  await fetchCategories()
  const pages = getCurrentPages()
  const options = pages[pages.length - 1].options || {}
  if (options.id) {
    isEdit.value = true
    try {
      const data = JSON.parse(decodeURIComponent(options.data || '{}'))
      Object.assign(form, data)
      const idx = categoryList.value.findIndex((c) => c.id === form.categoryId)
      if (idx >= 0) catIndex.value = idx
      if (form.image) imageUrl.value = resolveImageUrl(form.image)
    } catch (e) { /* ignore */ }
  }
})
</script>

<style lang="scss" scoped>
.edit-page { min-height: 100vh; background: #f2f2f7; padding-bottom: 160rpx; }
.form-section { background: #fff; margin-top: 20rpx; padding: 0 32rpx; }
.form-item { display: flex; align-items: center; min-height: 100rpx; border-bottom: 1rpx solid #f0f0f0; padding: 20rpx 0; &:last-child { border-bottom: none; } &.column { flex-direction: column; align-items: flex-start; } }
.form-label { width: 200rpx; font-size: 28rpx; color: #3c3c43; .required { color: #ff453a; } }
.form-input { flex: 1; font-size: 28rpx; }
.form-textarea { width: 100%; height: 160rpx; font-size: 28rpx; margin-top: 16rpx; padding: 16rpx; background: #f9f9fb; border-radius: 8rpx; }
.form-picker { flex: 1; display: flex; justify-content: space-between; align-items: center; font-size: 28rpx; .picker-arrow { font-size: 20rpx; color: #c7c7cc; } }
.upload-area { width: 200rpx; height: 200rpx; border-radius: 12rpx; overflow: hidden; background: #f9f9fb; display: flex; align-items: center; justify-content: center; }
.preview-img { width: 100%; height: 100%; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.upload-icon { font-size: 48rpx; }
.upload-text { font-size: 22rpx; color: #8e8e93; }
.ph { color: #c7c7cc; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 32rpx; background: #fff; }
.save-btn { width: 100%; height: 88rpx; background: #0a84ff; color: #fff; border-radius: 44rpx; font-size: 32rpx; font-weight: 600; border: none; &[disabled] { opacity: 0.6; } }
</style>
