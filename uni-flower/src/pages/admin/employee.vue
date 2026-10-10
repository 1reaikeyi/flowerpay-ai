<template>
  <view class="employee-page">
    <view class="search-bar">
      <uni-search-bar
        v-model="searchName"
        placeholder="搜索员工姓名"
        :radius="100"
        cancel-button="none"
        @confirm="handleSearch"
      />
    </view>

    <view class="employee-list">
      <view v-for="item in list" :key="item.id" class="employee-card">
        <view class="emp-left">
          <image class="emp-avatar" :src="resolveImageUrl(item.avatar)" mode="aspectFill" />
          <view class="emp-info">
            <text class="emp-name">{{ item.username }}</text>
            <text class="emp-work">{{ item.work || '-' }}</text>
            <uni-tag :text="item.status === 1 ? '在职' : '离职'" :type="item.status === 1 ? 'success' : 'error'" size="small" />
          </view>
        </view>
        <text class="action-btn edit" @click="goEdit(item)">编辑</text>
      </view>
      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">👥</text>
        <text class="empty-text">暂无员工数据</text>
      </view>
    </view>

    <uni-load-more v-if="list.length > 0" :status="loadMoreStatus" @clickLoadMore="loadMore" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { pageEmployeeList } from '@/api/admin/admin.js'
import { resolveImageUrl } from '@/utils/index.js'

const loadMoreStatus = computed(() => {
  if (loading.value) return 'loading'
  return hasMore.value ? 'more' : 'noMore'
})

const searchName = ref('')
const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const hasMore = ref(true)

const fetchList = async (reset = false) => {
  if (reset) { page.value = 1; hasMore.value = true }
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (searchName.value.trim()) params.employeename = searchName.value.trim()
    const res = await pageEmployeeList(params)
    const data = res?.data || {}
    list.value = reset ? (data.list || []) : [...list.value, ...(data.list || [])]
    hasMore.value = list.value.length < (data.total || 0)
    page.value++
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

const handleSearch = () => fetchList(true)
const loadMore = () => fetchList(false)
const goEdit = (item) => {
  uni.navigateTo({
    url: `/pages/admin/employee-profile?id=${item.id}&data=${encodeURIComponent(JSON.stringify(item))}`
  })
}

onMounted(() => fetchList(true))
onShow(() => { if (list.value.length > 0) fetchList(true) })
</script>

<style lang="scss" scoped>
.employee-page { min-height: 100vh; background: #f2f2f7; }
.search-bar { padding: 16rpx 24rpx; background: #fff; position: sticky; top: 0; z-index: 10; }
.employee-list { padding: 16rpx 24rpx; }
.employee-card { display: flex; background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; align-items: center; justify-content: space-between; }
.emp-left { display: flex; align-items: center; gap: 20rpx; flex: 1; }
.emp-avatar { width: 96rpx; height: 96rpx; border-radius: 50%; background: #f0f0f0; }
.emp-info { display: flex; flex-direction: column; gap: 6rpx; }
.emp-name { font-size: 30rpx; font-weight: 600; }
.emp-work { font-size: 24rpx; color: #8e8e93; }
.action-btn { font-size: 24rpx; padding: 10rpx 24rpx; border-radius: 8rpx; &.edit { color: #0a84ff; background: rgba(10,132,255,0.1); } }
</style>
