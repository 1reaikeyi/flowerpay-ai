<template>
  <view class="detail-page">
    <view class="order-card" v-if="order.id">
      <view class="order-head">
        <text class="order-no">订单号：{{ order.id }}</text>
        <uni-tag :text="getStatusLabel(order.status)" :type="getStatusTagType(order.status)" size="small" />
      </view>
      <view class="info-row"><text class="label">下单时间</text><text class="value">{{ formatDateTime(order.createTime) }}</text></view>
      <view class="info-row"><text class="label">收货人</text><text class="value">{{ order.receiverName || '-' }}</text></view>
      <view class="info-row"><text class="label">联系电话</text><text class="value">{{ order.receiverPhone || '-' }}</text></view>
      <view class="info-row"><text class="label">收货地址</text><text class="value">{{ order.receiverAddress || '-' }}</text></view>
    </view>

    <view class="section" v-if="order.flowerOrderDetailList && order.flowerOrderDetailList.length">
      <text class="section-title">商品明细</text>
      <view v-for="item in order.flowerOrderDetailList" :key="item.id" class="detail-row">
        <text class="detail-name">{{ item.flowerName || '商品' }}</text>
        <text class="detail-qty">x{{ item.specNumber || 1 }}</text>
      </view>
    </view>

    <view class="section">
      <view class="info-row"><text class="label">订单金额</text><text class="value price">¥{{ Number(order.totalAmount || 0).toFixed(2) }}</text></view>
      <view class="info-row"><text class="label">支付状态</text><text class="value">{{ order.payStatus === 1 ? '已支付' : '未支付' }}</text></view>
    </view>

    <view class="bottom-bar" v-if="[1,2,3,4,5,6].includes(order.status)">
      <button v-if="order.status === 2" class="action-full" @click="updateStatus('cooking')">开始制作</button>
      <button v-if="order.status === 3" class="action-full" @click="updateStatus('go')">工作人员取货</button>
      <button v-if="order.status === 4" class="action-full" @click="updateStatus('delivering')">开始配送</button>
      <button v-if="order.status === 5" class="action-full" @click="updateStatus('arrived')">确认到达</button>
      <button v-if="order.status === 6" class="action-full" @click="updateStatus('complete')">完成订单</button>
      <button class="action-cancel" @click="handleCancel">取消订单</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderById, updateOrderToCooking, updateOrderToGo, updateOrderToDelivering, updateOrderToArrived, updateOrderToComplete, cancelOrder } from '@/api/admin/order.js'
import { formatDateTime, showToast, showConfirm } from '@/utils/index.js'

const order = ref({})

const getStatusLabel = (s) => ({1:'待支付',2:'已支付',3:'制作中',4:'待取货',5:'配送中',6:'已到达',7:'已完成',8:'已取消'}[s] || '未知')
const getStatusTagType = (s) => s === 7 ? 'success' : s === 8 ? 'error' : 'primary'

const fetchOrder = async () => {
  const pages = getCurrentPages()
  const id = pages[pages.length - 1].options?.id
  if (!id) return
  try {
    const res = await getOrderById(id)
    order.value = res?.data || {}
  } catch (e) { /* ignore */ }
}

const updateStatus = async (action) => {
  const fnMap = { cooking: updateOrderToCooking, go: updateOrderToGo, delivering: updateOrderToDelivering, arrived: updateOrderToArrived, complete: updateOrderToComplete }
  try { await fnMap[action](order.value.id); showToast('操作成功', 'success'); fetchOrder() } catch (e) { /* ignore */ }
}

const handleCancel = async () => {
  const ok = await showConfirm('确认取消？将触发退款')
  if (!ok) return
  try { await cancelOrder(order.value.id); showToast('已取消', 'success'); fetchOrder() } catch (e) { /* ignore */ }
}

onMounted(() => fetchOrder())
</script>

<style lang="scss" scoped>
.detail-page { min-height: 100vh; background: #f2f2f7; padding-bottom: 180rpx; }
.order-card { background: #fff; margin: 20rpx 24rpx; border-radius: 16rpx; padding: 24rpx; }
.order-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.order-no { font-size: 28rpx; font-weight: 600; }
.info-row { display: flex; justify-content: space-between; padding: 12rpx 0; .label { font-size: 26rpx; color: #8e8e93; } .value { font-size: 26rpx; color: #1c1c1e; } .price { color: #ff453a; font-weight: 600; } }
.section { background: #fff; margin: 0 24rpx 20rpx; border-radius: 16rpx; padding: 24rpx; }
.section-title { font-size: 30rpx; font-weight: 600; display: block; margin-bottom: 16rpx; }
.detail-row { display: flex; justify-content: space-between; padding: 8rpx 0; }
.detail-name { font-size: 28rpx; }
.detail-qty { font-size: 26rpx; color: #8e8e93; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 24rpx; background: #fff; display: flex; gap: 16rpx; }
.action-full { flex: 1; height: 80rpx; background: #0a84ff; color: #fff; border-radius: 40rpx; font-size: 28rpx; border: none; }
.action-cancel { height: 80rpx; padding: 0 32rpx; background: rgba(255,69,58,0.1); color: #ff453a; border-radius: 40rpx; font-size: 28rpx; border: none; }
</style>
