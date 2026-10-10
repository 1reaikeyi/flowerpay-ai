<template>
  <view class="login-page">
    <view class="login-bg">
      <image class="bg-img" src="/static/login/login1.png" mode="aspectFill" />
    </view>

    <view class="login-card">
      <view class="card-header">
        <text class="card-title">花店管理</text>
        <text class="card-sub">登录后管理订单、商品与顾客消息</text>
      </view>

      <view class="form-group">
        <text class="form-label">账号</text>
        <input
          v-model="form.username"
          class="form-input"
          placeholder="请输入账号"
          placeholder-class="ph"
        />
      </view>

      <view class="form-group">
        <text class="form-label">密码</text>
        <input
          v-model="form.password"
          class="form-input"
          password
          placeholder="请输入密码"
          placeholder-class="ph"
          @confirm="handleLogin"
        />
      </view>

      <button class="login-btn" :loading="loading" :disabled="loading" @click="handleLogin">
        登录
      </button>

      <text class="forgot-link">忘记密码？</text>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAdminStore } from '@/stores/index.js'
import { showToast } from '@/utils/index.js'

const adminStore = useAdminStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username.trim()) {
    showToast('请输入账号')
    return
  }
  if (!form.password.trim()) {
    showToast('请输入密码')
    return
  }

  loading.value = true
  try {
    const res = await adminStore.login(form)
    adminStore.setToken(res.data)
    showToast('登录成功', 'success')
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/admin/home' })
    }, 500)
  } catch (e) {
    // 错误已在请求拦截器中提示
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  background: #f2f2f7;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 60vh;
  overflow: hidden;

  .bg-img {
    width: 100%;
    height: 100%;
    opacity: 0.35;
  }
}

.login-card {
  position: relative;
  z-index: 1;
  margin: 200rpx 48rpx 0;
  background: #fff;
  border-radius: 28rpx;
  padding: 56rpx 44rpx 48rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  margin-bottom: 48rpx;

  .card-title {
    display: block;
    font-size: 44rpx;
    font-weight: 700;
    color: #1c1c1e;
    margin-bottom: 12rpx;
  }

  .card-sub {
    display: block;
    font-size: 26rpx;
    color: #8e8e93;
  }
}

.form-group {
  margin-bottom: 36rpx;

  .form-label {
    display: block;
    font-size: 26rpx;
    font-weight: 600;
    color: #3c3c43;
    margin-bottom: 14rpx;
  }

  .form-input {
    width: 100%;
    height: 92rpx;
    background: #f2f2f7;
    border-radius: 16rpx;
    padding: 0 28rpx;
    font-size: 30rpx;
    color: #1c1c1e;
    box-sizing: border-box;
  }
}

.ph {
  color: #c7c7cc;
}

.login-btn {
  width: 100%;
  height: 92rpx;
  background: #0a84ff;
  color: #fff;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  margin-top: 16rpx;

  &[disabled] {
    opacity: 0.5;
  }
}

.forgot-link {
  display: block;
  text-align: center;
  font-size: 26rpx;
  color: #0a84ff;
  margin-top: 28rpx;
}
</style>
