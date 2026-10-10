<template>
  <view class="festival-page">
    <view class="search-bar">
      <uni-search-bar
        v-model="searchName"
        placeholder="搜索礼盒名称"
        :radius="100"
        cancel-button="none"
        @confirm="handleSearch"
      />
      <button class="add-btn" @click="goAdd">+ 新增</button>
    </view>

    <view class="festival-list">
      <view v-for="item in list" :key="item.id" class="festival-card" @click="goDetail(item)">
        <image class="festival-img" :src="resolveImageUrl(item.image)" mode="aspectFill" />
        <view class="festival-info">
          <text class="festival-name">{{ item.name }}</text>
          <text class="festival-cat">{{ item.categoryName || '-' }}</text>
          <view class="festival-bottom">
            <text class="festival-price">¥{{ Number(item.price || 0).toFixed(2) }}</text>
            <uni-tag :text="item.status === 1 ? '在售' : '下架'" :type="item.status === 1 ? 'success' : 'error'" size="small" />
          </view>
        </view>
        <view class="festival-actions" @click.stop>
          <text class="action-btn edit" @click.stop="goEdit(item)">编辑</text>
          <text class="action-btn" :class="item.status === 1 ? 'off' : 'on'" @click.stop="toggleStatus(item)">
            {{ item.status === 1 ? '下架' : '在售' }}
          </text>
        </view>
      </view>

      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🎁</text>
        <text class="empty-text">暂无礼盒数据</text>
      </view>
    </view>

    <uni-load-more v-if="list.length > 0" :status="loadMoreStatus" @clickLoadMore="loadMore" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { pageFestivalList, updateFestival } from '@/api/admin/festival.js'
import { resolveImageUrl, showToast } from '@/utils/index.js'

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
    if (searchName.value.trim()) params.name = searchName.value.trim()
    const res = await pageFestivalList(params)
    const data = res?.data || {}
    const newList = data.list || []
    list.value = reset ? newList : [...list.value, ...newList]
    hasMore.value = list.value.length < (data.total || 0)
    page.value++
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

const handleSearch = () => fetchList(true)
const loadMore = () => fetchList(false)
const goAdd = () => uni.navigateTo({ url: '/pages/admin/festival-edit' })
const goEdit = (item) => uni.navigateTo({ url: `/pages/admin/festival-edit?id=${item.id}&data=${encodeURIComponent(JSON.stringify(item))}` })
const goDetail = (item) => uni.navigateTo({ url: `/pages/admin/festival-detail?id=${item.id}` })

const toggleStatus = async (item) => {
  try {
    await updateFestival({ ...item, status: item.status === 1 ? 0 : 1 })
    showToast('操作成功', 'success')
    fetchList(true)
  } catch (e) { /* ignore */ }
}

onMounted(() => fetchList(true))
onShow(() => { if (list.value.length > 0) fetchList(true) })
</script>

<style lang="scss" scoped>
.festival-page { min-height: 100vh; background: #f2f2f7; }
.search-bar { display: flex; align-items: center; padding: 16rpx 24rpx; background: #fff; gap: 16rpx; position: sticky; top: 0; z-index: 10;
  :deep(.uni-searchbar) { flex: 1; padding: 0; }
}
.add-btn { background: #0a84ff; color: #fff; font-size: 26rpx; padding: 0 28rpx; height: 72rpx; line-height: 72rpx; border-radius: 36rpx; border: none; margin: 0; }
.festival-list { padding: 16rpx 24rpx; }
.festival-card { display: flex; background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 16rpx; align-items: center; }
.festival-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; background: #f0f0f0; flex-shrink: 0; }
.festival-info { flex: 1; margin-left: 20rpx; display: flex; flex-direction: column; gap: 8rpx; }
.festival-name { font-size: 30rpx; font-weight: 600; color: #1c1c1e; }
.festival-cat { font-size: 24rpx; color: #8e8e93; }
.festival-bottom { display: flex; align-items: center; justify-content: space-between; margin-top: 8rpx; }
.festival-price { font-size: 32rpx; font-weight: 700; color: #ff453a; }
.festival-actions { display: flex; flex-direction: column; gap: 12rpx; margin-left: 12rpx; }
.action-btn { font-size: 24rpx; padding: 8rpx 20rpx; border-radius: 8rpx; background: #f2f2f7; color: #3c3c43; text-align: center; &.edit { color: #0a84ff; } &.on { color: #30d158; } &.off { color: #ff9f0a; } }
</style>
