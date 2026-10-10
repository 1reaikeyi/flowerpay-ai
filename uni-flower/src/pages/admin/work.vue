<template>
  <view class="work-page">
    <!-- 商品管理 -->
    <view class="section">
      <view class="section-title">
        <text class="title-bar" />
        <text class="title-text">商品管理</text>
      </view>
      <view class="grid-wrap">
        <uni-grid :column="4" :show-border="false" @change="onGridClick(goodsMenus, $event)">
          <uni-grid-item v-for="(item, idx) in goodsMenus" :key="idx">
            <view class="grid-item">
              <view class="grid-icon" :style="{ background: item.bg }">
                <text class="icon-emoji">{{ item.icon }}</text>
              </view>
              <text class="grid-text">{{ item.text }}</text>
            </view>
          </uni-grid-item>
        </uni-grid>
      </view>
    </view>

    <!-- 订单管理 -->
    <view class="section">
      <view class="section-title">
        <text class="title-bar" />
        <text class="title-text">订单管理</text>
      </view>
      <view class="grid-wrap">
        <uni-grid :column="4" :show-border="false" @change="onGridClick(orderMenus, $event)">
          <uni-grid-item v-for="(item, idx) in orderMenus" :key="idx">
            <view class="grid-item">
              <view class="grid-icon" :style="{ background: item.bg }">
                <text class="icon-emoji">{{ item.icon }}</text>
              </view>
              <text class="grid-text">{{ item.text }}</text>
            </view>
          </uni-grid-item>
        </uni-grid>
      </view>
    </view>

    <!-- 店铺与员工 -->
    <view class="section">
      <view class="section-title">
        <text class="title-bar" />
        <text class="title-text">店铺与员工</text>
      </view>
      <view class="grid-wrap">
        <uni-grid :column="4" :show-border="false" @change="onGridClick(shopMenus, $event)">
          <uni-grid-item v-for="(item, idx) in shopMenus" :key="idx">
            <view class="grid-item">
              <view class="grid-icon" :style="{ background: item.bg }">
                <text class="icon-emoji">{{ item.icon }}</text>
              </view>
              <text class="grid-text">{{ item.text }}</text>
            </view>
          </uni-grid-item>
        </uni-grid>
      </view>
    </view>

    <!-- 数据统计 -->
    <view class="section">
      <view class="section-title">
        <text class="title-bar" />
        <text class="title-text">数据统计</text>
      </view>
      <view class="grid-wrap">
        <uni-grid :column="4" :show-border="false" @change="onGridClick(statMenus, $event)">
          <uni-grid-item v-for="(item, idx) in statMenus" :key="idx">
            <view class="grid-item">
              <view class="grid-icon" :style="{ background: item.bg }">
                <text class="icon-emoji">{{ item.icon }}</text>
              </view>
              <text class="grid-text">{{ item.text }}</text>
            </view>
          </uni-grid-item>
        </uni-grid>
      </view>
    </view>

    <layout-tab-bar current="/pages/admin/work" />
  </view>
</template>

<script setup>
import { showToast } from '@/utils/index.js'

// 商品管理：鲜花单品、节日礼盒、菜单分类
const goodsMenus = [
  { icon: '🌸', text: '鲜花单品', url: '/pages/admin/goods?tab=0', bg: 'rgba(255,159,10,0.12)' },
  { icon: '🎁', text: '节日礼盒', url: '/pages/admin/goods?tab=1', bg: 'rgba(191,90,242,0.12)' },
  { icon: '🗂️', text: '菜单分类', url: '/pages/admin/category', bg: 'rgba(10,132,255,0.12)' }
]

// 订单管理：主要情况、退款情况
const orderMenus = [
  { icon: '📋', text: '主要情况', url: '/pages/admin/order', bg: 'rgba(10,132,255,0.12)' },
  { icon: '💰', text: '退款情况', url: '', bg: 'rgba(255,69,58,0.12)' }
]

// 店铺与员工
const shopMenus = [
  { icon: '🏪', text: '店铺管理', url: '/pages/admin/shop', bg: 'rgba(48,209,88,0.12)' },
  { icon: '👥', text: '员工管理', url: '/pages/admin/employee', bg: 'rgba(10,132,255,0.12)' }
]

// 数据统计
const statMenus = [
  { icon: '📊', text: '今日数据', url: '/pages/admin/statistics', bg: 'rgba(191,90,242,0.12)' }
]

const onGridClick = (menus, e) => {
  const item = menus[e.detail.index]
  if (!item) return
  if (item.url) {
    uni.navigateTo({ url: item.url })
  } else {
    showToast('功能开发中')
  }
}
</script>

<style lang="scss" scoped>
.work-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding: 20rpx 0 140rpx;
}

.section {
  background: #fff;
  margin: 0 24rpx 20rpx;
  border-radius: 20rpx;
  padding: 24rpx;

  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;

    .title-bar {
      width: 6rpx;
      height: 28rpx;
      border-radius: 3rpx;
      background: #0a84ff;
      margin-right: 14rpx;
    }

    .title-text {
      font-size: 30rpx;
      font-weight: 600;
      color: #1c1c1e;
    }
  }
}

.grid-wrap {
  :deep(.uni-grid) {
    background: transparent;
  }
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;

  .grid-icon {
    width: 88rpx;
    height: 88rpx;
    border-radius: 22rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;

    .icon-emoji {
      font-size: 40rpx;
    }
  }

  .grid-text {
    font-size: 24rpx;
    color: #3c3c43;
  }
}
</style>
