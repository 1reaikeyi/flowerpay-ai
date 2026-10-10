<script>
export default {
  onLaunch: function () {
    console.log('App Launch')
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #f2f2f7;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 通用容器 */
.container {
  padding: 20rpx;
}

/* 卡片 */
.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

/* 主色按钮 */
.btn-primary {
  background-color: #0a84ff;
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
  padding: 20rpx 0;
  text-align: center;
  border: none;
}

.btn-primary[disabled] {
  opacity: 0.5;
}

/* 危险按钮 */
.btn-danger {
  background-color: #ff453a;
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
  padding: 20rpx 0;
  text-align: center;
  border: none;
}

/* 次要按钮 */
.btn-default {
  background-color: #fff;
  color: #0a84ff;
  border: 1rpx solid #0a84ff;
  border-radius: 40rpx;
  font-size: 30rpx;
  padding: 20rpx 0;
  text-align: center;
}

/* 标签 */
.tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  line-height: 1.6;
}

.tag-success {
  background: rgba(48, 209, 88, 0.12);
  color: #30d158;
}

.tag-danger {
  background: rgba(255, 69, 58, 0.12);
  color: #ff453a;
}

.tag-primary {
  background: rgba(10, 132, 255, 0.12);
  color: #0a84ff;
}

.tag-warning {
  background: rgba(255, 159, 10, 0.12);
  color: #ff9f0a;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  color: #8e8e93;
}

.empty-state .empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-state .empty-text {
  font-size: 28rpx;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 30rpx 0;
  color: #8e8e93;
  font-size: 26rpx;
}
</style>
