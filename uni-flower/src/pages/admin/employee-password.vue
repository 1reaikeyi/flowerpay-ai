<template>
  <view class="password-page">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">新密码 <text class="required">*</text></text>
        <input v-model="form.newPassword" class="form-input" password placeholder="请输入新密码（至少6位）" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">确认密码 <text class="required">*</text></text>
        <input v-model="form.confirmPassword" class="form-input" password placeholder="请再次输入新密码" placeholder-class="ph" />
      </view>
    </view>
    <view class="bottom-bar">
      <button class="save-btn" :loading="saving" :disabled="saving" @click="handleSave">{{ saving ? '提交中...' : '确认修改' }}</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { updateEmployeePassword } from '@/api/admin/admin.js'
import { showToast } from '@/utils/index.js'

const saving = ref(false)
const form = reactive({ newPassword: '', confirmPassword: '' })

const handleSave = async () => {
  if (!form.newPassword.trim()) return showToast('请输入新密码')
  if (form.newPassword.length < 6) return showToast('密码至少6位')
  if (form.newPassword !== form.confirmPassword) return showToast('两次密码不一致')
  saving.value = true
  try {
    await updateEmployeePassword(form)
    showToast('修改成功，请重新登录', 'success')
    setTimeout(() => {
      uni.removeStorageSync('flower:admin')
      uni.reLaunch({ url: '/pages/login/admin' })
    }, 1000)
  } catch (e) { /* ignore */ }
  finally { saving.value = false }
}
</script>

<style lang="scss" scoped>
.password-page { min-height: 100vh; background: #f2f2f7; padding-bottom: 160rpx; }
.form-section { background: #fff; margin-top: 20rpx; padding: 0 32rpx; }
.form-item { display: flex; align-items: center; min-height: 100rpx; border-bottom: 1rpx solid #f0f0f0; padding: 20rpx 0; &:last-child { border-bottom: none; } }
.form-label { width: 200rpx; font-size: 28rpx; color: #3c3c43; .required { color: #ff453a; } }
.form-input { flex: 1; font-size: 28rpx; }
.ph { color: #c7c7cc; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 32rpx; background: #fff; }
.save-btn { width: 100%; height: 88rpx; background: #0a84ff; color: #fff; border-radius: 44rpx; font-size: 32rpx; font-weight: 600; border: none; &[disabled] { opacity: 0.6; } }
</style>
