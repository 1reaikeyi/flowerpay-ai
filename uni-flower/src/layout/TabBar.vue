<template>
  <view class="tabbar">
    <view
      v-for="(item, idx) in tabs"
      :key="idx"
      class="tab-item"
      :class="{ active: current === item.path }"
      @click="switchTo(item)"
    >
      <text class="tab-icon">{{ item.icon }}</text>
      <text class="tab-text">{{ item.text }}</text>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  current: {
    type: String,
    default: ''
  }
})

const tabs = [
  { path: '/pages/admin/home', text: '首页', icon: '🌸' },
  { path: '/pages/admin/goods', text: '商品', icon: '🛍️' },
  { path: '/pages/admin/mine', text: '我的', icon: '👤' }
]

const switchTo = (item) => {
  if (item.path === props.current) return
  uni.redirectTo({ url: item.path })
}
</script>

<style lang="scss" scoped>
.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100rpx;
  padding-bottom: env(safe-area-inset-bottom);
  background: #fff;
  display: flex;
  border-top: 1rpx solid #e5e5ea;
  z-index: 999;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;

  .tab-icon {
    font-size: 40rpx;
    line-height: 1;
    filter: grayscale(100%);
    opacity: 0.5;
  }

  .tab-text {
    font-size: 22rpx;
    color: #8e8e93;
  }

  &.active {
    .tab-icon {
      filter: none;
      opacity: 1;
    }

    .tab-text {
      color: #0a84ff;
      font-weight: 600;
    }
  }
}
</style>
