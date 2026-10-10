<template>
  <view class="add-page">
    <!-- 头像上传 -->
    <view class="avatar-section">
      <image class="avatar" :src="avatarUrl" mode="aspectFill" @click="chooseAvatar" />
      <text class="avatar-tip">点击上传头像</text>
    </view>

    <!-- 表单 -->
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">用户名 <text class="required">*</text></text>
        <input v-model="form.username" class="form-input" placeholder="请输入用户名" placeholder-class="ph" maxlength="20" />
      </view>
      <view class="form-item">
        <text class="form-label">职位 <text class="required">*</text></text>
        <input v-model="form.work" class="form-input" placeholder="请输入职位" placeholder-class="ph" maxlength="32" />
      </view>
      <view class="form-item">
        <text class="form-label">密码 <text class="required">*</text></text>
        <input v-model="form.password" class="form-input" password placeholder="请输入密码（至少6位）" placeholder-class="ph" maxlength="20" />
      </view>
      <view class="form-item">
        <text class="form-label">手机号 <text class="required">*</text></text>
        <input v-model="form.phone" class="form-input" type="number" placeholder="请输入手机号" placeholder-class="ph" maxlength="11" />
      </view>
      <view class="form-item">
        <text class="form-label">邮箱</text>
        <input v-model="form.email" class="form-input" placeholder="请输入邮箱（选填）" placeholder-class="ph" maxlength="64" />
      </view>
      <view class="form-item">
        <text class="form-label">性别</text>
        <picker :range="['男', '女']" :value="sexIndex" @change="onSexChange">
          <view class="form-picker">
            <text>{{ sexIndex === 0 ? '男' : '女' }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">状态</text>
        <switch :checked="form.status === 1" @change="onStatusChange" color="#30d158" />
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button class="save-btn" :loading="saving" :disabled="saving" @click="handleSubmit">
        {{ saving ? '提交中...' : '确认新增' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { registerEmployee } from '@/api/employee/employee.js'
import { uploadFile } from '@/api/file/file.js'
import { resolveImageUrl, showToast, showLoading, hideLoading } from '@/utils/index.js'

const saving = ref(false)
const sexIndex = ref(0)

const form = reactive({
  username: '',
  work: '',
  password: '',
  avatar: '',
  email: '',
  phone: '',
  sex: '男',
  status: 1
})

const avatarUrl = computed(() => {
  if (!form.avatar) return '/static/login/avatar.png'
  return resolveImageUrl(form.avatar)
})

const onSexChange = (e) => {
  sexIndex.value = e.detail.value
  form.sex = sexIndex.value === 0 ? '男' : '女'
}

const onStatusChange = (e) => {
  form.status = e.detail.value ? 1 : 0
}

const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        showLoading('上传中...')
        const uploadRes = await uploadFile(res.tempFilePaths[0])
        // 后端返回 "{路径}::{文件名}"，取后半段
        const saved = String(uploadRes.data || '').split('::').pop()
        if (saved) {
          form.avatar = saved
          showToast('上传成功', 'success')
        }
        hideLoading()
      } catch (e) {
        hideLoading()
      }
    }
  })
}

const handleSubmit = async () => {
  if (!form.username.trim()) return showToast('请输入用户名')
  if (form.username.length < 3) return showToast('用户名至少3位')
  if (!form.work.trim()) return showToast('请输入职位')
  if (!form.password) return showToast('请输入密码')
  if (form.password.length < 6) return showToast('密码至少6位')
  if (!form.phone) return showToast('请输入手机号')
  if (!/^1[3-9]\d{9}$/.test(form.phone)) return showToast('请输入正确的手机号')
  if (form.email && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) return showToast('邮箱格式不正确')

  saving.value = true
  try {
    await registerEmployee(form)
    showToast('新增成功', 'success')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    // 错误已在请求拦截器提示
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.add-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 160rpx;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
  background: #fff;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: #f0f0f0;
  margin-bottom: 12rpx;
}

.avatar-tip {
  font-size: 24rpx;
  color: #8e8e93;
}

.form-section {
  background: #fff;
  margin-top: 20rpx;
  padding: 0 32rpx;
}

.form-item {
  display: flex;
  align-items: center;
  min-height: 100rpx;
  border-bottom: 1rpx solid #f0f0f0;
  padding: 20rpx 0;
  &:last-child { border-bottom: none; }
}

.form-label {
  width: 200rpx;
  font-size: 28rpx;
  color: #3c3c43;
  .required { color: #ff453a; }
}

.form-input {
  flex: 1;
  font-size: 28rpx;
}

.form-picker {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  .picker-arrow { font-size: 20rpx; color: #c7c7cc; }
}

.ph { color: #c7c7cc; }

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  background: #fff;
}

.save-btn {
  width: 100%;
  height: 88rpx;
  background: #0a84ff;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  &[disabled] { opacity: 0.6; }
}
</style>
