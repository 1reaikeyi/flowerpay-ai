<template>
  <view class="avatar-page">
    <!-- 预览区 -->
    <view class="preview-section">
      <image class="preview-avatar" :src="previewUrl" mode="aspectFill" />
      <text class="preview-tip">当前头像预览</text>
    </view>

    <!-- 操作区 -->
    <view class="action-section">
      <view class="action-tip">
        <text class="tip-title">更换头像</text>
        <text class="tip-desc">支持 JPG/PNG 格式，单张不超过 2MB</text>
      </view>

      <view class="upload-btn" @click="chooseAvatar">
        <text class="upload-icon">📷</text>
        <text class="upload-text">选择图片</text>
      </view>

      <view v-if="newAvatar" class="upload-hint">
        <text>已选择新头像，点击保存生效</text>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button
        class="save-btn"
        :loading="saving"
        :disabled="saving || !newAvatar"
        @click="handleSave"
      >
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/index.js'
import { uploadFile } from '@/api/file/file.js'
import { updateEmployeeInfo } from '@/api/admin/admin.js'
import { resolveImageUrl, showToast, showLoading, hideLoading } from '@/utils/index.js'

const adminStore = useAdminStore()
const saving = ref(false)
const newAvatar = ref('')

const previewUrl = computed(() => {
  const a = newAvatar.value || adminStore.user?.avatar
  if (!a) return '/static/login/avatar.png'
  return resolveImageUrl(a)
})

const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0]
      // 检查大小
      uni.getFileInfo({
        filePath,
        success: async (info) => {
          if (info.size > 2 * 1024 * 1024) {
            showToast('图片不能超过 2MB')
            return
          }
          try {
            showLoading('上传中...')
            const uploadRes = await uploadFile(filePath)
            const saved = String(uploadRes.data || '').split('::').pop()
            if (saved) {
              newAvatar.value = saved
              showToast('上传成功', 'success')
            }
            hideLoading()
          } catch (e) {
            hideLoading()
          }
        }
      })
    }
  })
}

const handleSave = async () => {
  if (!newAvatar.value) return
  if (!adminStore.user?.id) {
    showToast('缺少用户信息，请重新登录')
    return
  }
  saving.value = true
  try {
    await updateEmployeeInfo({
      id: adminStore.user.id,
      avatar: newAvatar.value
    })
    adminStore.setUser({ ...adminStore.user, avatar: newAvatar.value })
    showToast('头像已更新', 'success')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    // ignore
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  adminStore.restore()
})
</script>

<style lang="scss" scoped>
.avatar-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 160rpx;
}

.preview-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  background: #fff;
}

.preview-avatar {
  width: 200rpx;
  height: 200rpx;
  border-radius: 50%;
  background: #f0f0f0;
  margin-bottom: 16rpx;
  border: 4rpx solid #e5e5ea;
}

.preview-tip {
  font-size: 24rpx;
  color: #8e8e93;
}

.action-section {
  background: #fff;
  margin-top: 20rpx;
  padding: 32rpx;
}

.action-tip {
  margin-bottom: 24rpx;
  .tip-title {
    display: block;
    font-size: 30rpx;
    font-weight: 600;
    color: #1c1c1e;
    margin-bottom: 8rpx;
  }
  .tip-desc {
    font-size: 24rpx;
    color: #8e8e93;
  }
}

.upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200rpx;
  border: 2rpx dashed #0a84ff;
  border-radius: 16rpx;
  background: rgba(10, 132, 255, 0.04);

  .upload-icon {
    font-size: 56rpx;
    margin-bottom: 12rpx;
  }
  .upload-text {
    font-size: 28rpx;
    color: #0a84ff;
    font-weight: 500;
  }
}

.upload-hint {
  margin-top: 20rpx;
  text-align: center;
  font-size: 24rpx;
  color: #30d158;
}

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
