<template>
  <view class="ai-page">
    <!-- 聊天头部 -->
    <view class="chat-header">
      <view class="header-left">
        <view class="ai-avatar">
          <image class="avatar-img" src="/static/logo1.png" mode="aspectFill" />
        </view>
        <view class="header-titles">
          <text class="header-title">鲜小花</text>
          <view class="header-sub">
            <text class="status-dot"></text>
            <text class="status-text">在线</text>
          </view>
        </view>
      </view>
      <view class="header-actions">
        <view class="icon-btn" @click="handleNewChat">
          <text class="icon-plus">+</text>
        </view>
      </view>
    </view>

    <!-- 历史会话侧边栏（可折叠） -->
    <view class="sidebar" :class="{ collapsed: !sidebarOpen }" v-if="sidebarOpen">
      <view class="sidebar-header">
        <text class="sidebar-title">对话记录</text>
      </view>
      <scroll-view class="history-list" scroll-y>
        <view v-for="group in historyGroups" :key="group.title" class="history-group">
          <text class="group-title">{{ group.title }}</text>
          <view
            v-for="item in group.list"
            :key="item.sessionId"
            class="history-item"
            :class="{ active: activeSessionId === item.sessionId }"
            @click="handleSelectHistory(item)"
          >
            <text class="item-text">{{ item.title }}</text>
          </view>
        </view>
        <view v-if="historyGroups.length === 0" class="history-empty">
          <text>暂无对话记录</text>
        </view>
      </scroll-view>
    </view>

    <!-- 消息区 -->
    <scroll-view
      class="message-scroll"
      scroll-y
      :scroll-into-view="scrollIntoId"
      :scroll-with-animation="true"
    >
      <!-- 欢迎页 -->
      <view v-if="messages.length === 0" class="welcome-page">
        <view class="welcome-logo">
          <image class="welcome-logo-img" src="/static/logo1.png" mode="aspectFill" />
        </view>
        <text class="welcome-title">你好，我是鲜小花</text>
        <text class="welcome-desc">你的专属鲜花智能助手</text>
      </view>

      <!-- 消息列表 -->
      <view v-else class="message-list">
        <view
          v-for="(msg, idx) in messages"
          :key="idx"
          :id="'msg-' + idx"
          class="message-row"
          :class="msg.role === 'user' ? 'is-user' : 'is-ai'"
        >
          <view v-if="msg.role === 'ai'" class="msg-avatar">
            <image class="avatar-img" src="/static/logo1.png" mode="aspectFill" />
          </view>
          <view class="msg-body">
            <view class="msg-bubble" :class="{ streaming: msg.streaming }">
              <view v-if="msg.streaming && !msg.content" class="loading-dots">
                <text class="dot"></text>
                <text class="dot"></text>
                <text class="dot"></text>
              </view>
              <rich-text v-else :nodes="msg.content"></rich-text>
            </view>
          </view>
          <view v-if="msg.role === 'user'" class="msg-avatar user-avatar">
            <image class="avatar-img" src="/static/logo2.png" mode="aspectFill" />
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区 -->
    <view class="input-area">
      <view class="input-wrapper">
        <textarea
          v-model="inputText"
          class="chat-textarea"
          placeholder="输入消息…"
          :auto-height="true"
          :maxlength="2000"
          :show-confirm-bar="false"
          :adjust-position="true"
          @confirm="handleSend"
        />
        <view class="input-footer">
          <text class="char-count">{{ inputText.length }}/2000</text>
          <view class="input-actions">
            <view
              v-if="loading"
              class="send-btn stop-btn"
              @click="handleStop"
            >
              <text>停止</text>
            </view>
            <view
              v-else
              class="send-btn"
              :class="{ disabled: !inputText.trim() }"
              @click="handleSend"
            >
              <text>发送</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <layout-tab-bar current="/pages/admin/home" />
  </view>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import {
  startSession,
  getHistorySessions,
  getSessionMessages,
  updateSessionTitle,
  deleteSession,
  abortChat,
  chatStream
} from '@/api/ai/ai.js'
import { useAdminStore } from '@/stores/index.js'
import { showToast } from '@/utils/index.js'

const adminStore = useAdminStore()
const sidebarOpen = ref(true)
const scrollIntoId = ref('')
const inputText = ref('')
const loading = ref(false)
const activeSessionId = ref('')
const messages = ref([])
const historyGroups = ref([])
let streamStopped = false

// HTML 转义
function escapeHtml(text) {
  return String(text ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function formatContent(text) {
  return escapeHtml(String(text ?? '')).replace(/\n/g, '<br>')
}

function scrollToBottom() {
  nextTick(() => {
    if (messages.value.length > 0) {
      scrollIntoId.value = 'msg-' + (messages.value.length - 1)
    }
  })
}

function applyGreeting(session) {
  const example = session?.examples?.[0]
  const question = example?.describe || example?.title || ''
  const text = question
    ? `你好，我是鲜小花。${question}`
    : '你好，我是鲜小花，请问有什么可以帮您？'
  messages.value.push({
    role: 'ai',
    content: formatContent(text),
    streaming: false
  })
}

async function loadHistory() {
  try {
    const map = await getHistorySessions()
    const groups = []
    if (map && typeof map === 'object') {
      for (const key of Object.keys(map)) {
        groups.push({ title: key, list: map[key] || [] })
      }
    }
    historyGroups.value = groups
  } catch (e) {
    historyGroups.value = []
  }
}

async function handleNewChat() {
  try {
    const session = await startSession()
    activeSessionId.value = session?.sessionId || ''
    messages.value = []
    applyGreeting(session)
    scrollToBottom()
    loadHistory()
  } catch (e) {
    showToast('新建会话失败')
  }
}

async function handleSelectHistory(item) {
  if (loading.value) return
  activeSessionId.value = item.sessionId
  messages.value = []
  try {
    const list = (await getSessionMessages(item.sessionId)) || []
    messages.value = list.map((m) => ({
      role: m.type === 'USER' ? 'user' : 'ai',
      content: formatContent(m.content || ''),
      streaming: false
    }))
  } catch (e) {
    showToast('加载对话失败')
  }
  scrollToBottom()
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  if (!activeSessionId.value) {
    try {
      const session = await startSession()
      activeSessionId.value = session?.sessionId || ''
      if (messages.value.length === 0) applyGreeting(session)
      loadHistory()
    } catch (e) {
      showToast('创建会话失败')
      return
    }
  }

  const sessionId = activeSessionId.value
  const isFirstUserMsg = messages.value.filter((m) => m.role === 'user').length === 0

  messages.value.push({ role: 'user', content: escapeHtml(text) })
  inputText.value = ''
  scrollToBottom()

  loading.value = true
  streamStopped = false
  const aiMsg = { role: 'ai', content: '', streaming: true }
  messages.value.push(aiMsg)
  let rawContent = ''

  await chatStream(
    { question: text, sessionId },
    {
      onMessage: (chunk) => {
        if (streamStopped) return
        rawContent += chunk
        aiMsg.content = formatContent(rawContent)
        scrollToBottom()
      },
      onStop: () => {
        streamStopped = true
      },
      onDone: () => {
        aiMsg.streaming = false
        loading.value = false
        if (isFirstUserMsg) {
          updateSessionTitle(sessionId, text.slice(0, 20)).catch(() => {})
          loadHistory()
        }
        scrollToBottom()
      },
      onError: () => {
        aiMsg.streaming = false
        if (!aiMsg.content) aiMsg.content = formatContent('（服务异常，请稍后重试）')
        loading.value = false
        showToast('对话出错了')
      }
    }
  )
}

function handleStop() {
  if (!activeSessionId.value) return
  streamStopped = true
  abortChat()
  loading.value = false
}

onMounted(async () => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
    return
  }
  try {
    const session = await startSession()
    activeSessionId.value = session?.sessionId || ''
    applyGreeting(session)
    scrollToBottom()
  } catch (e) {
    // ignore
  }
  loadHistory()
})

onShow(() => {
  adminStore.restore()
  if (!adminStore.token) {
    uni.reLaunch({ url: '/pages/login/admin' })
  }
})
</script>

<style lang="scss" scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f2f2f7;
  padding-bottom: 100rpx;
  box-sizing: border-box;
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #e5e5ea;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .ai-avatar {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    overflow: hidden;
    background: #f0f0f0;
    flex-shrink: 0;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
  }

  .header-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #1c1c1e;
  }

  .header-sub {
    display: flex;
    align-items: center;
    gap: 6rpx;
    margin-top: 4rpx;
  }

  .status-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background: #30d158;
  }

  .status-text {
    font-size: 22rpx;
    color: #8e8e93;
  }

  .icon-btn {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    background: #f2f2f7;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .icon-plus {
    font-size: 36rpx;
    color: #0a84ff;
    line-height: 1;
  }
}

/* 侧边栏 */
.sidebar {
  background: #fff;
  border-bottom: 1rpx solid #e5e5ea;
  max-height: 400rpx;
  display: flex;
  flex-direction: column;

  &.collapsed {
    display: none;
  }
}

.sidebar-header {
  padding: 16rpx 24rpx 8rpx;
  .sidebar-title {
    font-size: 26rpx;
    font-weight: 600;
    color: #8e8e93;
  }
}

.history-list {
  flex: 1;
  padding: 0 16rpx 16rpx;
}

.history-group {
  margin-bottom: 12rpx;
  .group-title {
    font-size: 20rpx;
    color: #c7c7cc;
    padding: 6rpx 8rpx;
  }
}

.history-item {
  padding: 14rpx 16rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #3c3c43;

  &.active {
    background: rgba(10, 132, 255, 0.1);
    color: #0a84ff;
  }

  .item-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.history-empty {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #c7c7cc;
}

/* 消息区 */
.message-scroll {
  flex: 1;
  padding: 20rpx 24rpx;
  overflow: hidden;
}

.welcome-page {
  text-align: center;
  padding-top: 120rpx;

  .welcome-logo {
    margin-bottom: 20rpx;
    .welcome-logo-img {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
    }
  }

  .welcome-title {
    display: block;
    font-size: 40rpx;
    font-weight: 700;
    color: #1c1c1e;
    margin-bottom: 10rpx;
  }

  .welcome-desc {
    font-size: 26rpx;
    color: #8e8e93;
  }
}

.message-list {
  padding-bottom: 20rpx;
}

.message-row {
  display: flex;
  gap: 12rpx;
  margin-bottom: 24rpx;
  align-items: flex-end;

  &.is-user {
    flex-direction: row-reverse;
  }
}

.msg-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: #f0f0f0;

  .avatar-img {
    width: 100%;
    height: 100%;
  }
}

.msg-body {
  max-width: 70%;
}

.is-user .msg-body {
  align-items: flex-end;
}

.msg-bubble {
  padding: 16rpx 24rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  line-height: 1.55;
  word-break: break-word;
}

.is-ai .msg-bubble {
  background: #fff;
  color: #1c1c1e;
  border-bottom-left-radius: 8rpx;
}

.is-user .msg-bubble {
  background: #0a84ff;
  color: #fff;
  border-bottom-right-radius: 8rpx;
}

.loading-dots {
  display: inline-flex;
  gap: 6rpx;
  padding: 6rpx 0;

  .dot {
    width: 10rpx;
    height: 10rpx;
    border-radius: 50%;
    background: #8e8e93;
    animation: blink 1.4s infinite both;

    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes blink {
  0%, 80%, 100% { opacity: 0.3; }
  40% { opacity: 1; }
}

/* 输入区 */
.input-area {
  padding: 16rpx 24rpx 24rpx;
  background: #fff;
  border-top: 1rpx solid #e5e5ea;
}

.input-wrapper {
  background: #f2f2f7;
  border-radius: 20rpx;
  padding: 16rpx 20rpx;
}

.chat-textarea {
  width: 100%;
  font-size: 28rpx;
  line-height: 1.5;
  color: #1c1c1e;
  min-height: 40rpx;
  max-height: 200rpx;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8rpx;

  .char-count {
    font-size: 20rpx;
    color: #c7c7cc;
  }

  .input-actions {
    display: flex;
  }
}

.send-btn {
  background: #0a84ff;
  color: #fff;
  padding: 10rpx 32rpx;
  border-radius: 30rpx;
  font-size: 26rpx;

  &.disabled {
    opacity: 0.4;
  }

  &.stop-btn {
    background: #ff453a;
  }
}
</style>
