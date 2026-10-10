<template>
  <view class="detail-page">
    <view class="festival-header">
      <image class="festival-img" :src="resolveImageUrl(festival.image)" mode="aspectFill" />
      <view class="festival-meta">
        <text class="festival-name">{{ festival.name || '-' }}</text>
        <text class="festival-price">¥{{ Number(festival.price || 0).toFixed(2) }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <text class="section-title">包含鲜花</text>
        <text class="add-link" @click="showAddDialog = true">+ 添加</text>
      </view>
      <view v-for="item in detailList" :key="item.id" class="detail-card">
        <text class="detail-name">{{ item.flowerName || `鲜花#${item.flowerId}` }}</text>
        <view class="detail-row"><text class="detail-label">数量</text><text class="detail-value">{{ item.specNumber || '-' }}</text></view>
        <view class="detail-row"><text class="detail-label">送人对象</text><text class="detail-value">{{ item.specObject || '-' }}</text></view>
        <view class="detail-row"><text class="detail-label">用途场景</text><text class="detail-value">{{ item.specOption || '-' }}</text></view>
        <text class="action-btn delete" @click="handleDelete(item)">删除</text>
      </view>
      <view v-if="detailList.length === 0" class="empty-state"><text class="empty-icon">📋</text><text class="empty-text">暂无明细</text></view>
    </view>

    <view v-if="showAddDialog" class="dialog-mask" @click="showAddDialog = false">
      <view class="dialog-content" @click.stop>
        <text class="dialog-title">添加鲜花到礼盒</text>
        <view class="dialog-item">
          <text class="dialog-label">选择鲜花</text>
          <picker :range="flowerLabels" :value="flowerIdx" @change="onFlowerChange">
            <view class="form-picker"><text>{{ flowerLabels[flowerIdx] || '请选择' }}</text><text class="picker-arrow">▼</text></view>
          </picker>
        </view>
        <view class="dialog-item">
          <text class="dialog-label">数量</text>
          <input v-model="detailForm.specNumber" class="dialog-input" type="number" placeholder="请输入数量" placeholder-class="ph" />
        </view>
        <view class="dialog-item">
          <text class="dialog-label">送人对象</text>
          <input v-model="detailForm.specObject" class="dialog-input" placeholder="如：女友" placeholder-class="ph" />
        </view>
        <view class="dialog-item">
          <text class="dialog-label">用途场景</text>
          <input v-model="detailForm.specOption" class="dialog-input" placeholder="如：生日" placeholder-class="ph" />
        </view>
        <view class="dialog-btns">
          <button class="dialog-btn cancel" @click="showAddDialog = false">取消</button>
          <button class="dialog-btn confirm" @click="handleAdd">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFestivalById } from '@/api/admin/festival.js'
import { getFestivalDetailsByFestivalId, createFestivalDetail, deleteFestivalDetails } from '@/api/admin/festivalDetail.js'
import { getFlowersByCategoryId } from '@/api/admin/category.js'
import { resolveImageUrl, showToast, showConfirm } from '@/utils/index.js'

const festivalId = ref(null)
const festival = ref({})
const detailList = ref([])
const showAddDialog = ref(false)
const flowerList = ref([])
const flowerLabels = ref([])
const flowerIdx = ref(0)
const detailForm = reactive({ specNumber: '', specObject: '', specOption: '' })

const fetchFlowers = async () => {
  try {
    const res = await getFlowersByCategoryId(festival.value.categoryId)
    flowerList.value = res?.data || []
    flowerLabels.value = flowerList.value.map((f) => f.name)
  } catch (e) { /* ignore */ }
}

const onFlowerChange = (e) => { flowerIdx.value = e.detail.value }

const fetchFestival = async () => {
  try {
    const res = await getFestivalById(festivalId.value)
    festival.value = res?.data || {}
    if (festival.value.categoryId) fetchFlowers()
  } catch (e) { /* ignore */ }
}

const fetchDetails = async () => {
  try {
    const res = await getFestivalDetailsByFestivalId(festivalId.value)
    detailList.value = res?.data || []
  } catch (e) { /* ignore */ }
}

const handleAdd = async () => {
  if (!flowerList.value[flowerIdx.value]?.id) return showToast('请选择鲜花')
  try {
    await createFestivalDetail({
      festivalId: festivalId.value,
      flowerId: flowerList.value[flowerIdx.value].id,
      specNumber: Number(detailForm.specNumber) || 1,
      specObject: detailForm.specObject,
      specOption: detailForm.specOption
    })
    showToast('添加成功', 'success')
    Object.assign(detailForm, { specNumber: '', specObject: '', specOption: '' })
    showAddDialog.value = false
    fetchDetails()
  } catch (e) { /* ignore */ }
}

const handleDelete = async (item) => {
  const ok = await showConfirm('确认删除吗？')
  if (!ok) return
  try { await deleteFestivalDetails([item.id]); showToast('删除成功', 'success'); fetchDetails() } catch (e) { /* ignore */ }
}

onMounted(() => {
  const pages = getCurrentPages()
  festivalId.value = pages[pages.length - 1].options?.id
  if (festivalId.value) { fetchFestival(); fetchDetails() }
})
</script>

<style lang="scss" scoped>
.detail-page { min-height: 100vh; background: #f2f2f7; }
.festival-header { display: flex; background: #fff; padding: 24rpx; gap: 20rpx; align-items: center; }
.festival-img { width: 180rpx; height: 180rpx; border-radius: 12rpx; background: #f0f0f0; }
.festival-meta { flex: 1; display: flex; flex-direction: column; gap: 12rpx; }
.festival-name { font-size: 34rpx; font-weight: 600; }
.festival-price { font-size: 36rpx; font-weight: 700; color: #ff453a; }
.section { margin: 20rpx 24rpx; background: #fff; border-radius: 16rpx; padding: 24rpx; }
.section-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; }
.add-link { font-size: 26rpx; color: #0a84ff; }
.detail-card { background: #f9f9fb; border-radius: 12rpx; padding: 20rpx; margin-bottom: 16rpx; }
.detail-name { font-size: 28rpx; font-weight: 600; display: block; margin-bottom: 12rpx; }
.detail-row { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.detail-label { font-size: 26rpx; color: #8e8e93; }
.detail-value { font-size: 26rpx; }
.action-btn { font-size: 24rpx; padding: 8rpx 20rpx; border-radius: 8rpx; &.delete { color: #ff453a; background: rgba(255,69,58,0.1); } }
.dialog-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 100; display: flex; align-items: center; justify-content: center; }
.dialog-content { width: 600rpx; background: #fff; border-radius: 20rpx; padding: 40rpx; }
.dialog-title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 30rpx; text-align: center; }
.dialog-item { margin-bottom: 24rpx; }
.dialog-label { font-size: 26rpx; color: #3c3c43; display: block; margin-bottom: 12rpx; }
.dialog-input { width: 100%; height: 80rpx; border: 1rpx solid #e5e5ea; border-radius: 8rpx; padding: 0 20rpx; font-size: 28rpx; box-sizing: border-box; }
.form-picker { display: flex; justify-content: space-between; align-items: center; font-size: 28rpx; .picker-arrow { font-size: 20rpx; color: #c7c7cc; } }
.dialog-btns { display: flex; gap: 20rpx; margin-top: 30rpx; }
.dialog-btn { flex: 1; height: 76rpx; border-radius: 38rpx; font-size: 28rpx; border: none; &.cancel { background: #f2f2f7; } &.confirm { background: #0a84ff; color: #fff; } }
.ph { color: #c7c7cc; }
</style>
