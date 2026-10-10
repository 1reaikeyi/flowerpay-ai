<template>
  <view class="detail-page">
    <view class="flower-header">
      <image class="flower-img" :src="resolveImageUrl(flower.image)" mode="aspectFill" />
      <view class="flower-meta">
        <text class="flower-name">{{ flower.name || '-' }}</text>
        <text class="flower-price">¥{{ Number(flower.price || 0).toFixed(2) }}</text>
        <uni-tag :text="flower.status === 1 ? '在售' : '下架'" :type="flower.status === 1 ? 'success' : 'error'" size="small" />
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <text class="section-title">规格明细</text>
        <text class="add-link" @click="showAddDialog = true">+ 添加</text>
      </view>

      <view v-for="item in detailList" :key="item.id" class="detail-card">
        <view class="detail-row">
          <text class="detail-label">送人对象</text>
          <text class="detail-value">{{ item.specObject || '-' }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">用途场景</text>
          <text class="detail-value">{{ item.specOption || '-' }}</text>
        </view>
        <view class="detail-actions">
          <text class="action-btn delete" @click="handleDeleteDetail(item)">删除</text>
        </view>
      </view>

      <view v-if="detailList.length === 0" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无明细</text>
      </view>
    </view>

    <!-- 添加明细弹窗 -->
    <view v-if="showAddDialog" class="dialog-mask" @click="showAddDialog = false">
      <view class="dialog-content" @click.stop>
        <text class="dialog-title">添加规格明细</text>
        <view class="dialog-item">
          <text class="dialog-label">送人对象</text>
          <input v-model="detailForm.specObject" class="dialog-input" placeholder="如：女友、母亲" placeholder-class="ph" />
        </view>
        <view class="dialog-item">
          <text class="dialog-label">用途场景</text>
          <input v-model="detailForm.specOption" class="dialog-input" placeholder="如：生日、表白" placeholder-class="ph" />
        </view>
        <view class="dialog-btns">
          <button class="dialog-btn cancel" @click="showAddDialog = false">取消</button>
          <button class="dialog-btn confirm" @click="handleAddDetail">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFlowerById } from '@/api/admin/flower.js'
import { getFlowerDetailsByFlowerId, createFlowerDetail, deleteFlowerDetails } from '@/api/admin/flowerDetail.js'
import { resolveImageUrl, showToast, showConfirm } from '@/utils/index.js'

const flowerId = ref(null)
const flower = ref({})
const detailList = ref([])
const showAddDialog = ref(false)
const detailForm = reactive({ specObject: '', specOption: '' })

const fetchFlower = async () => {
  try {
    const res = await getFlowerById(flowerId.value)
    flower.value = res?.data || {}
  } catch (e) { /* ignore */ }
}

const fetchDetails = async () => {
  try {
    const res = await getFlowerDetailsByFlowerId(flowerId.value)
    detailList.value = res?.data || []
  } catch (e) { /* ignore */ }
}

const handleAddDetail = async () => {
  if (!detailForm.specObject.trim() && !detailForm.specOption.trim()) {
    showToast('请至少填写一项')
    return
  }
  try {
    await createFlowerDetail({
      flowerId: flowerId.value,
      specObject: detailForm.specObject,
      specOption: detailForm.specOption
    })
    showToast('添加成功', 'success')
    detailForm.specObject = ''
    detailForm.specOption = ''
    showAddDialog.value = false
    fetchDetails()
  } catch (e) { /* ignore */ }
}

const handleDeleteDetail = async (item) => {
  const ok = await showConfirm('确认删除该明细吗？')
  if (!ok) return
  try {
    await deleteFlowerDetails([item.id])
    showToast('删除成功', 'success')
    fetchDetails()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  flowerId.value = currentPage.options?.id
  if (flowerId.value) {
    fetchFlower()
    fetchDetails()
  }
})
</script>

<style lang="scss" scoped>
.detail-page { min-height: 100vh; background: #f2f2f7; }

.flower-header {
  display: flex; background: #fff; padding: 24rpx; gap: 20rpx; align-items: center;
  .flower-img { width: 180rpx; height: 180rpx; border-radius: 12rpx; background: #f0f0f0; }
  .flower-meta { flex: 1; display: flex; flex-direction: column; gap: 12rpx; }
  .flower-name { font-size: 34rpx; font-weight: 600; color: #1c1c1e; }
  .flower-price { font-size: 36rpx; font-weight: 700; color: #ff453a; }
}

.section { margin: 20rpx 24rpx; background: #fff; border-radius: 16rpx; padding: 24rpx; }

.section-head {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx;
  .section-title { font-size: 30rpx; font-weight: 600; color: #1c1c1e; }
  .add-link { font-size: 26rpx; color: #0a84ff; }
}

.detail-card {
  background: #f9f9fb; border-radius: 12rpx; padding: 20rpx; margin-bottom: 16rpx;
  .detail-row { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
  .detail-label { font-size: 26rpx; color: #8e8e93; }
  .detail-value { font-size: 26rpx; color: #1c1c1e; }
  .detail-actions { margin-top: 12rpx; text-align: right; }
}

.action-btn {
  font-size: 24rpx; padding: 8rpx 20rpx; border-radius: 8rpx;
  &.delete { color: #ff453a; background: rgba(255, 69, 58, 0.1); }
}

.dialog-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5); z-index: 100;
  display: flex; align-items: center; justify-content: center;
}
.dialog-content {
  width: 600rpx; background: #fff; border-radius: 20rpx; padding: 40rpx;
  .dialog-title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 30rpx; text-align: center; }
  .dialog-item { margin-bottom: 24rpx; }
  .dialog-label { font-size: 26rpx; color: #3c3c43; display: block; margin-bottom: 12rpx; }
  .dialog-input {
    width: 100%; height: 80rpx; border: 1rpx solid #e5e5ea; border-radius: 8rpx;
    padding: 0 20rpx; font-size: 28rpx; box-sizing: border-box;
  }
  .dialog-btns { display: flex; gap: 20rpx; margin-top: 30rpx; }
  .dialog-btn {
    flex: 1; height: 76rpx; border-radius: 38rpx; font-size: 28rpx; border: none;
    &.cancel { background: #f2f2f7; color: #3c3c43; }
    &.confirm { background: #0a84ff; color: #fff; }
  }
}
.ph { color: #c7c7cc; }
</style>
