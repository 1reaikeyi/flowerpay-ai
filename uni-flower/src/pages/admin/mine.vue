<template>
  <view class="mine-page">
    <!-- 用户信息头部 -->
    <view class="my-info">
      <view class="head" @click="goPage('/pages/admin/employee-avatar')">
        <image class="head_image" :src="avatarUrl" mode="aspectFill" />
      </view>
      <view class="phone_name">
        <view class="name">
          <text class="name_text">{{ adminStore.user?.username || userName || '管理员' }}</text>
        </view>
        <view class="phone">
          <text class="phone_text">{{ adminStore.user?.work || '系统管理员' }}</text>
        </view>
      </view>
    </view>

    <scroll-view class="container" scroll-y>
      <!-- 个人管理 -->
      <view class="address_order">
        <view class="address" @click="goPage('/pages/admin/employee-profile')">
          <text class="address_word">个人信息</text>
          <text class="to_right">›</text>
        </view>
        <view class="order" @click="goPage('/pages/admin/employee-avatar')">
          <text class="order_word">头像设置</text>
          <text class="to_right">›</text>
        </view>
        <view class="order" @click="goPage('/pages/admin/employee-password')">
          <text class="order_word">修改密码</text>
          <text class="to_right">›</text>
        </view>
      </view>

      <!-- 管理入口 -->
      <view class="address_order">
        <view class="address" @click="goPage('/pages/admin/employee')">
          <text class="address_word">员工管理</text>
          <text class="to_right">›</text>
        </view>
        <view class="order" @click="goPage('/pages/admin/employee-add')">
          <text class="order_word">新增员工</text>
          <text class="to_right">›</text>
        </view>
        <view class="order" @click="goPage('/pages/admin/shop')">
          <text class="order_word">店铺管理</text>
          <text class="to_right">›</text>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="quit" @click="handleLogout">退出登录</view>
    </scroll-view>

    <layout-tab-bar current="/pages/admin/mine" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAdminStore } from '@/stores/index.js'
import { getUserNameFromToken } from '@/utils/jwt.js'
import { resolveImageUrl, showConfirm } from '@/utils/index.js'

const adminStore = useAdminStore()
const userName = ref('')

const avatarUrl = computed(() => {
  const a = adminStore.user?.avatar
  if (!a) return '/static/login/avatar.png'
  return resolveImageUrl(a)
})

const goPage = (url) => uni.navigateTo({ url })

const handleLogout = async () => {
  const ok = await showConfirm('确认退出登录吗？')
  if (!ok) return
  await adminStore.logout()
  uni.reLaunch({ url: '/pages/login/admin' })
}

onMounted(() => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
    return
  }
  userName.value = getUserNameFromToken(adminStore.token) || '管理员'
  if (!adminStore.user?.id) adminStore.getUser()
})

onShow(() => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
    return
  }
  if (!adminStore.user?.id) adminStore.getUser()
})
</script>

<style lang="scss" scoped>
.mine-page {
  background: #f6f6f6;
  min-height: 100vh;
  padding-bottom: 100rpx;
}

/* 用户信息头部 */
.my-info {
  height: 240rpx;
  width: 100%;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  display: flex;
  align-items: center;
  padding: 0 40rpx;
  box-sizing: border-box;

  .head {
    width: 140rpx;
    height: 140rpx;
    margin-right: 30rpx;
    flex-shrink: 0;

    .head_image {
      width: 140rpx;
      height: 140rpx;
      border-radius: 50%;
      background-color: #fff;
      border: 4rpx solid rgba(255, 255, 255, 0.4);
    }
  }

  .phone_name {
    flex: 1;

    .name {
      .name_text {
        font-size: 36rpx;
        font-weight: 600;
        color: #fff;
        line-height: 50rpx;
      }
    }

    .phone {
      margin-top: 8rpx;
      .phone_text {
        font-size: 26rpx;
        color: rgba(255, 255, 255, 0.8);
        line-height: 36rpx;
      }
    }
  }
}

/* 内容容器 */
.container {
  margin-top: 20rpx;
  height: calc(100vh - 260rpx);
  box-sizing: border-box;
}

/* 卡片列表 */
.address_order {
  width: 710rpx;
  border-radius: 16rpx;
  background-color: #fff;
  margin: 20rpx auto;
  overflow: hidden;

  .address {
    height: 100rpx;
    line-height: 100rpx;
    position: relative;
    padding: 0 30rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .address_word {
      font-size: 28rpx;
      font-weight: 400;
      color: #333333;
    }

    .to_right {
      font-size: 36rpx;
      color: #c7c7cc;
    }
  }

  .order {
    height: 100rpx;
    line-height: 100rpx;
    position: relative;
    padding: 0 30rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-top: 1rpx dashed #ebebeb;

    .order_word {
      font-size: 28rpx;
      font-weight: 400;
      color: #333333;
    }

    .to_right {
      font-size: 36rpx;
      color: #c7c7cc;
    }
  }
}

/* 退出按钮 */
.quit {
  width: 710rpx;
  height: 100rpx;
  background: #ffffff;
  border-radius: 16rpx;
  margin: 30rpx auto;
  font-size: 30rpx;
  font-weight: 500;
  text-align: center;
  color: #ff453a;
  line-height: 100rpx;
}
</style>
