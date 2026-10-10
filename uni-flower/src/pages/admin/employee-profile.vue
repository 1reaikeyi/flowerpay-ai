<template>
  <view class="profile-page">
    <view class="avatar-section">
      <image class="avatar" :src="avatarUrl" mode="aspectFill" @click="chooseAvatar" />
      <text class="avatar-tip">点击更换头像</text>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="form-label">用户名</text>
        <input v-model="form.username" class="form-input" placeholder="请输入用户名" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">职位</text>
        <input v-model="form.work" class="form-input" placeholder="请输入职位" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">性别</text>
        <picker :range="['男', '女']" :value="sexIndex" @change="onSexChange">
          <view class="form-picker"><text>{{ form.sex === 1 ? '男' : '女' }}</text><text class="picker-arrow">▼</text></view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">邮箱</text>
        <input v-model="form.email" class="form-input" placeholder="请输入邮箱" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input v-model="form.phone" class="form-input" type="number" placeholder="请输入手机号" placeholder-class="ph" />
      </view>
    </view>

    <view class="bottom-bar">
      <button class="save-btn" :loading="saving" :disabled="saving" @click="handleSave">{{ saving ? '保存中...' : '保存' }}</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { updateEmployeeInfo } from '@/api/admin/admin.js'
import { uploadFile } from '@/api/file/file.js'
import { resolveImageUrl, showToast, showLoading, hideLoading } from '@/utils/index.js'
import { useAdminStore } from '@/stores/index.js'

const adminStore = useAdminStore()
const saving = ref(false)
const sexIndex = ref(0)

const form = reactive({ id: null, username: '', avatar: '', work: '', sex: 1, email: '', phone: '', status: 1 })

const avatarUrl = computed(() => form.avatar ? resolveImageUrl(form.avatar) : '/static/logo.png')

const onSexChange = (e) => { sexIndex.value = e.detail.value; form.sex = sexIndex.value === 0 ? 1 : 2 }

const chooseAvatar = () => {
  uni.chooseImage({ count: 1, success: async (res) => {
    try {
      showLoading()
      const uploadRes = await uploadFile(res.tempFilePaths[0])
      form.avatar = uploadRes.data
      hideLoading()
      showToast('上传成功', 'success')
    } catch (e) { hideLoading() }
  }})
}

const handleSave = async () => {
  if (!form.username.trim()) return showToast('请输入用户名')
  saving.value = true
  try {
    await updateEmployeeInfo(form)
    adminStore.setUser({ ...adminStore.user, ...form })
    showToast('保存成功', 'success')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) { /* ignore */ }
  finally { saving.value = false }
}

onMounted(() => {
  const pages = getCurrentPages()
  const options = pages[pages.length - 1].options || {}
  if (options.id) {
    try {
      const data = JSON.parse(decodeURIComponent(options.data || '{}'))
      Object.assign(form, data)
      sexIndex.value = form.sex === 1 ? 0 : 1
    } catch (e) { /* ignore */ }
  } else if (adminStore.user?.id) {
    Object.assign(form, adminStore.user)
    sexIndex.value = form.sex === 1 ? 0 : 1
  }
})
</script>

<style lang="scss" scoped>
.profile-page { min-height: 100vh; background: #f2f2f7; padding-bottom: 160rpx; }
.avatar-section { display: flex; flex-direction: column; align-items: center; padding: 48rpx 0; background: #fff; }
.avatar { width: 160rpx; height: 160rpx; border-radius: 50%; background: #f0f0f0; margin-bottom: 12rpx; }
.avatar-tip { font-size: 24rpx; color: #8e8e93; }
.form-section { background: #fff; margin-top: 20rpx; padding: 0 32rpx; }
.form-item { display: flex; align-items: center; min-height: 100rpx; border-bottom: 1rpx solid #f0f0f0; padding: 20rpx 0; &:last-child { border-bottom: none; } }
.form-label { width: 200rpx; font-size: 28rpx; color: #3c3c43; }
.form-input { flex: 1; font-size: 28rpx; }
.form-picker { flex: 1; display: flex; justify-content: space-between; align-items: center; font-size: 28rpx; .picker-arrow { font-size: 20rpx; color: #c7c7cc; } }
.ph { color: #c7c7cc; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 32rpx; background: #fff; }
.save-btn { width: 100%; height: 88rpx; background: #0a84ff; color: #fff; border-radius: 44rpx; font-size: 32rpx; font-weight: 600; border: none; &[disabled] { opacity: 0.6; } }
</style>
