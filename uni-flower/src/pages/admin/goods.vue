<template>
  <view class="goods-page">
    <!-- 6个功能按钮 -->
    <view class="menu-grid">
      <view
        v-for="(item, idx) in menuList"
        :key="idx"
        class="menu-card"
        :class="{ disabled: item.disabled }"
        @click="goMenu(item)"
      >
        <view class="card-icon">
          <image class="icon-img" src="/static/logo.png" mode="aspectFit" />
        </view>
        <text class="card-text">{{ item.text }}</text>
        <view v-if="item.disabled" class="card-badge">开发中</view>
      </view>
    </view>

    <layout-tab-bar current="/pages/admin/goods" />
  </view>
</template>

<script setup>
import { onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAdminStore } from '@/stores/index.js'

const adminStore = useAdminStore()

const menuList = [
  { text: '鲜花管理', url: '/pages/admin/flower' },
  { text: '礼盒管理', url: '/pages/admin/festival' },
  { text: '分类管理', url: '/pages/admin/category' },
  { text: '员工管理', url: '/pages/admin/employee' },
  { text: '订单管理', url: '/pages/admin/order' },
  { text: '数据统计', url: '/pages/admin/statistics', disabled: true }
]

const goMenu = (item) => {
  if (item.disabled) {
    uni.showToast({ title: '功能开发中，后端尚未就绪', icon: 'none' })
    return
  }
  if (item.url) {
    uni.navigateTo({ url: item.url })
  }
}

onMounted(() => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
  }
})

onShow(() => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
  }
})
</script>

<style lang="scss" scoped>
.goods-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 30rpx;
  padding-bottom: 140rpx;
  box-sizing: border-box;
}

/* 2列网格布局 */
.menu-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}

/* 单个按钮卡片 */
.menu-card {
  width: calc(50% - 12rpx);
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  position: relative;
  box-sizing: border-box;

  &:active {
    transform: scale(0.97);
    opacity: 0.8;
  }

  &.disabled {
    opacity: 0.5;
  }
}

/* 图标容器 */
.card-icon {
  width: 100rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;

  .icon-img {
    width: 80rpx;
    height: 80rpx;
  }
}

/* 文字 */
.card-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
}

/* 开发中标记 */
.card-badge {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  font-size: 18rpx;
  color: #fff;
  background: #ff9500;
  border-radius: 16rpx;
  padding: 4rpx 14rpx;
  line-height: 1.4;
}
</style>
