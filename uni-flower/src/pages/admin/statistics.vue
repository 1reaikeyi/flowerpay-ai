<template>
  <view class="stats-page">
    <view class="empty-container">
      <text class="empty-icon">📊</text>
      <text class="empty-title">数据统计</text>
      <text class="empty-desc">功能开发中，后端尚未就绪</text>
      <text class="empty-hint">敬请期待</text>
    </view>
  </view>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.stats-page {
  min-height: 100vh;
  background: #f2f2f7;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
  opacity: 0.5;
}

.empty-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1c1c1e;
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 28rpx;
  color: #8e8e93;
  margin-bottom: 8rpx;
}

.empty-hint {
  font-size: 24rpx;
  color: #c7c7cc;
}
</style>
