<template>
  <view class="shop-page">
    <view class="shop-card">
      <text class="shop-label">店铺营业状态</text>
      <view class="shop-status">
        <text class="status-text" :class="shopStatus === 1 ? 'open' : 'closed'">
          {{ shopStatus === 1 ? '营业中' : '已打烊' }}
        </text>
        <switch :checked="shopStatus === 1" @change="handleToggle" color="#30d158" />
      </view>
    </view>

    <view class="tip-card">
      <text class="tip-title">温馨提示</text>
      <text class="tip-text">• 营业中：顾客可以正常下单</text>
      <text class="tip-text">• 已打烊：暂停接单，已有订单不受影响</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getShopStatus, setShopStatus } from '@/api/admin/shop.js'
import { showToast } from '@/utils/index.js'

const shopStatus = ref(0)

const fetchStatus = async () => {
  try {
    const res = await getShopStatus()
    shopStatus.value = res?.data ?? 0
  } catch (e) { /* ignore */ }
}

const handleToggle = async (e) => {
  const newStatus = e.detail.value ? 1 : 0
  try {
    await setShopStatus(newStatus)
    shopStatus.value = newStatus
    showToast(newStatus === 1 ? '已开始营业' : '已打烊', 'success')
  } catch (e) { /* ignore */ }
}

onMounted(() => fetchStatus())
</script>

<style lang="scss" scoped>
.shop-page { min-height: 100vh; background: #f2f2f7; padding: 24rpx; }
.shop-card { background: #fff; border-radius: 16rpx; padding: 32rpx; display: flex; justify-content: space-between; align-items: center; }
.shop-label { font-size: 30rpx; font-weight: 600; }
.shop-status { display: flex; align-items: center; gap: 16rpx; }
.status-text { font-size: 28rpx; font-weight: 600; &.open { color: #30d158; } &.closed { color: #8e8e93; } }
.tip-card { background: #fff; border-radius: 16rpx; padding: 32rpx; margin-top: 20rpx; }
.tip-title { font-size: 28rpx; font-weight: 600; display: block; margin-bottom: 16rpx; }
.tip-text { font-size: 26rpx; color: #8e8e93; display: block; padding: 4rpx 0; }
</style>
