<template>
  <view class="category-page">
    <!-- 搜索筛选 -->
    <view class="filter-bar">
      <picker :range="typeLabels" :value="typeIndex" @change="onTypeChange">
        <view class="filter-picker">
          <text>{{ typeLabels[typeIndex] }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
      <button class="add-btn" @click="goAdd">+ 新增</button>
    </view>

    <view class="category-list">
      <view v-for="item in list" :key="item.id" class="category-card">
        <view class="card-main">
          <view class="card-info">
            <text class="card-name">{{ item.name }}</text>
            <view class="card-tags">
              <uni-tag :text="getTypeLabel(item.type)" :type="getTypeTagType(item.type)" size="small" />
              <uni-tag :text="item.status === 1 ? '启用' : '禁用'" :type="item.status === 1 ? 'success' : 'error'" size="small" />
            </view>
            <text class="card-time">更新：{{ formatTime(item.updateTime) }}</text>
          </view>
          <view class="card-actions">
            <text class="action-btn edit" @click.stop="goEdit(item)">编辑</text>
            <text class="action-btn" :class="item.status === 1 ? 'off' : 'on'" @click.stop="toggleStatus(item)">
              {{ item.status === 1 ? '禁用' : '启用' }}
            </text>
            <text class="action-btn delete" @click.stop="handleDelete(item)">删除</text>
          </view>
        </view>
      </view>

      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无分类数据</text>
      </view>
    </view>

    <uni-load-more v-if="list.length > 0" :status="loadMoreStatus" @clickLoadMore="loadMore" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { pageCategoryList, updateCategory, deleteCategories } from '@/api/admin/category.js'
import { formatTime, showToast, showConfirm } from '@/utils/index.js'

const typeOptions = [
  { value: null, label: '全部类型' },
  { value: 1, label: '鲜花商品' },
  { value: 2, label: '节日多花礼盒' },
  { value: 3, label: '礼品' }
]
const typeLabels = typeOptions.map((o) => o.label)
const typeIndex = ref(0)

const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const hasMore = ref(true)

const getTypeLabel = (type) => {
  const map = { 1: '鲜花商品', 2: '节日多花礼盒', 3: '礼品' }
  return map[type] || '其他'
}

const getTypeTagType = (type) => {
  const map = { 1: 'success', 2: 'primary', 3: 'warning' }
  return map[type] || 'default'
}

const loadMoreStatus = computed(() => {
  if (loading.value) return 'loading'
  return hasMore.value ? 'more' : 'noMore'
})

const fetchList = async (reset = false) => {
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  if (!hasMore.value || loading.value) return

  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (typeOptions[typeIndex.value].value) {
      params.type = typeOptions[typeIndex.value].value
    }
    const res = await pageCategoryList(params)
    const data = res?.data || {}
    const newList = data.list || []
    if (reset) {
      list.value = newList
    } else {
      list.value = [...list.value, ...newList]
    }
    hasMore.value = list.value.length < (data.total || 0)
    page.value++
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const onTypeChange = (e) => {
  typeIndex.value = e.detail.value
  fetchList(true)
}

const loadMore = () => {
  fetchList(false)
}

const goAdd = () => {
  uni.navigateTo({ url: '/pages/admin/category-edit' })
}

const goEdit = (item) => {
  uni.navigateTo({
    url: `/pages/admin/category-edit?id=${item.id}&data=${encodeURIComponent(JSON.stringify(item))}`
  })
}

const toggleStatus = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1
  try {
    await updateCategory({ ...item, status: newStatus })
    showToast(newStatus === 1 ? '已启用' : '已禁用', 'success')
    fetchList(true)
  } catch (e) { /* ignore */ }
}

const handleDelete = async (item) => {
  const ok = await showConfirm(`确认删除分类「${item.name}」吗？`)
  if (!ok) return
  try {
    await deleteCategories([item.id])
    showToast('删除成功', 'success')
    fetchList(true)
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  fetchList(true)
})

// 页面显示时刷新（编辑返回后）
import { onShow } from '@dcloudio/uni-app'
onShow(() => {
  if (list.value.length > 0) {
    fetchList(true)
  }
})
</script>

<style lang="scss" scoped>
.category-page {
  min-height: 100vh;
  background: #f2f2f7;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.filter-picker {
  display: flex;
  align-items: center;
  font-size: 28rpx;
  color: #3c3c43;
  padding: 12rpx 24rpx;
  background: #f2f2f7;
  border-radius: 8rpx;

  .picker-arrow {
    font-size: 20rpx;
    margin-left: 8rpx;
    color: #8e8e93;
  }
}

.add-btn {
  background: #0a84ff;
  color: #fff;
  font-size: 26rpx;
  padding: 12rpx 32rpx;
  border-radius: 8rpx;
  border: none;
  line-height: 1.5;
  margin: 0;
}

.category-list {
  padding: 16rpx 24rpx;
}

.category-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  overflow: hidden;
}

.card-main {
  padding: 24rpx;
}

.card-info {
  margin-bottom: 16rpx;

  .card-name {
    font-size: 32rpx;
    font-weight: 600;
    color: #1c1c1e;
    display: block;
    margin-bottom: 12rpx;
  }

  .card-tags {
    display: flex;
    gap: 12rpx;
    margin-bottom: 12rpx;
  }

  .card-time {
    font-size: 22rpx;
    color: #8e8e93;
  }
}

.card-actions {
  display: flex;
  gap: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  font-size: 26rpx;
  padding: 10rpx 28rpx;
  border-radius: 8rpx;
  background: #f2f2f7;
  color: #3c3c43;

  &.edit {
    color: #0a84ff;
  }
  &.on {
    color: #30d158;
  }
  &.off {
    color: #ff9f0a;
  }
  &.delete {
    color: #ff453a;
  }

  &:active {
    opacity: 0.6;
  }
}
</style>
