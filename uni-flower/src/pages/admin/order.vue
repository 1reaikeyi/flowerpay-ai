<template>
  <view class="order-page">
    <!-- 状态筛选 -->
    <scroll-view scroll-x class="status-tabs">
      <uni-segmented-control
        :values="tabLabels"
        :current="tabIndex"
        active-color="#0a84ff"
        style-type="button"
        @clickItem="onTabClick"
      />
    </scroll-view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view v-for="item in list" :key="item.id" class="order-card" @click="goDetail(item)">
        <view class="card-head">
          <text class="order-no">订单号 {{ item.id }}</text>
          <uni-tag :text="getStatusLabel(item.status)" :type="getStatusTagType(item.status)" size="small" />
        </view>

        <view class="card-body">
          <image
            v-if="item.flowerOrderDetailList && item.flowerOrderDetailList[0]?.flowerImage"
            class="order-img"
            :src="resolveImageUrl(item.flowerOrderDetailList[0].flowerImage)"
            mode="aspectFill"
          />
          <view v-else class="order-img placeholder">🌸</view>
          <view class="order-info">
            <text class="receiver">{{ item.receiverName || '收花人' }} · {{ item.receiverPhone || '-' }}</text>
            <text class="addr">{{ item.receiverAddress || '地址待填' }}</text>
            <view class="order-meta">
              <text class="amount">¥{{ Number(item.totalAmount || item.amount || 0).toFixed(2) }}</text>
              <text class="time">{{ formatDateTime(item.createTime) }}</text>
            </view>
          </view>
        </view>

        <view class="card-actions" @click.stop>
          <text v-if="item.status === 2" class="op-btn primary" @click.stop="updateStatus(item, 'cooking')">开始制作</text>
          <text v-if="item.status === 3" class="op-btn primary" @click.stop="updateStatus(item, 'go')">工作人员取货</text>
          <text v-if="item.status === 4" class="op-btn primary" @click.stop="updateStatus(item, 'delivering')">开始配送</text>
          <text v-if="item.status === 5" class="op-btn primary" @click.stop="updateStatus(item, 'arrived')">确认到达</text>
          <text v-if="item.status === 6" class="op-btn primary" @click.stop="updateStatus(item, 'complete')">完成订单</text>
          <text v-if="[1,2,3,4,5,6].includes(item.status)" class="op-btn danger" @click.stop="handleCancel(item)">取消订单</text>
        </view>
      </view>

      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无订单</text>
      </view>
    </view>

    <uni-load-more v-if="list.length > 0" :status="loadMoreStatus" @clickLoadMore="loadMore" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { pageOrderList, updateOrderToCooking, updateOrderToGo, updateOrderToDelivering, updateOrderToArrived, updateOrderToComplete, cancelOrder } from '@/api/admin/order.js'
import { formatDateTime, resolveImageUrl, showToast, showConfirm } from '@/utils/index.js'

const statusTabs = [
  { value: 1, label: '全部' },
  { value: 2, label: '待支付' },
  { value: 3, label: '制作中' },
  { value: 4, label: '待取货' },
  { value: 5, label: '配送中' },
  { value: 6, label: '已到达' },
  { value: 7, label: '已完成' },
  { value: 8, label: '已取消' }
]
const tabLabels = statusTabs.map((t) => t.label)

const currentStatus = ref(1)
const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const hasMore = ref(true)

const tabIndex = computed(() => statusTabs.findIndex((t) => t.value === currentStatus.value))
const loadMoreStatus = computed(() => loading.value ? 'loading' : (hasMore.value ? 'more' : 'noMore'))

const getStatusLabel = (s) => ({1:'待支付',2:'已支付',3:'制作中',4:'待取货',5:'配送中',6:'已到达',7:'已完成',8:'已取消'}[s] || '未知')
const getStatusTagType = (s) => s === 7 ? 'success' : s === 8 ? 'error' : 'primary'

const onTabClick = (e) => {
  currentStatus.value = statusTabs[e.currentIndex].value
  fetchList(true)
}

const fetchList = async (reset = false) => {
  if (reset) { page.value = 1; hasMore.value = true }
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    const res = await pageOrderList({ page: page.value, pageSize, status: currentStatus.value })
    const data = res?.data || {}
    list.value = reset ? (data.list || []) : [...list.value, ...(data.list || [])]
    hasMore.value = list.value.length < (data.total || 0)
    page.value++
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

const loadMore = () => fetchList(false)
const goDetail = (item) => uni.navigateTo({ url: `/pages/admin/order-detail?id=${item.id}` })

const updateStatus = async (item, action) => {
  const fnMap = { cooking: updateOrderToCooking, go: updateOrderToGo, delivering: updateOrderToDelivering, arrived: updateOrderToArrived, complete: updateOrderToComplete }
  try {
    await fnMap[action](item.id)
    showToast('操作成功', 'success')
    fetchList(true)
  } catch (e) { /* ignore */ }
}

const handleCancel = async (item) => {
  const ok = await showConfirm('确认取消该订单？将触发退款')
  if (!ok) return
  try { await cancelOrder(item.id); showToast('已取消', 'success'); fetchList(true) } catch (e) { /* ignore */ }
}

onMounted(() => fetchList(true))
onShow(() => { if (list.value.length > 0) fetchList(true) })
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background: #f2f2f7;
}

.status-tabs {
  white-space: nowrap;
  background: #fff;
  padding: 16rpx 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.order-list {
  padding: 16rpx 24rpx;
}

.order-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18rpx;

  .order-no {
    font-size: 26rpx;
    color: #8e8e93;
  }
}

.card-body {
  display: flex;
  gap: 18rpx;
  padding: 16rpx 0;
  border-top: 1rpx solid #f5f5f5;
  border-bottom: 1rpx solid #f5f5f5;
}

.order-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 14rpx;
  background: #f0f0f0;
  flex-shrink: 0;

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 48rpx;
  }
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;

  .receiver {
    font-size: 28rpx;
    font-weight: 600;
    color: #1c1c1e;
  }

  .addr {
    font-size: 24rpx;
    color: #8e8e93;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .order-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;

    .amount {
      font-size: 30rpx;
      font-weight: 700;
      color: #ff453a;
    }

    .time {
      font-size: 22rpx;
      color: #c7c7cc;
    }
  }
}

.card-actions {
  display: flex;
  gap: 16rpx;
  padding-top: 18rpx;
  flex-wrap: wrap;
}

.op-btn {
  font-size: 24rpx;
  padding: 12rpx 28rpx;
  border-radius: 10rpx;

  &.primary {
    background: #0a84ff;
    color: #fff;
  }

  &.danger {
    background: rgba(255,69,58,0.1);
    color: #ff453a;
  }

  &:active { opacity: 0.7; }
}
</style>
