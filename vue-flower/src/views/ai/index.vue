<template>
  <!-- 右下角悬浮球：未打开对话时显示 -->
  <div v-if="!dialogVisible" class="ai-float-ball" @click="dialogVisible = true">
    <img :src="picHello" alt="ai" class="ball-img" />
  </div>


  <!-- 悬浮窗口宽度：900px（见下方 width 属性） -->
  <el-dialog
    v-model="dialogVisible"
    :modal="false"
    :lock-scroll="false"
    :close-on-click-modal="false"
    :show-close="false"
    :append-to-body="true"
    custom-class="ai-chat-dialog"
    width="900px"
  >
    <div class="ai-page">
    <div class="ai-layout">
      <!-- 左侧历史记录 -->
      <div class="sidebar" :class="{ collapsed: !sidebarOpen }">
        <div class="sidebar-header">
          <span class="sidebar-title">对话记录</span>
          <button class="ios-btn ios-btn-primary ios-btn-sm" @click="handleNewChat">
            <el-icon><Plus /></el-icon>
            新对话
          </button>
        </div>
        <div class="history-list">
          <div v-for="group in historyGroups" :key="group.title" class="history-group">
            <div class="group-title">{{ group.title }}</div>
            <div
              v-for="item in group.list"
              :key="item.sessionId"
              class="history-item"
              :class="{ active: activeSessionId === item.sessionId }"
              @click="handleSelectHistory(item)"
            >
              <el-icon class="item-icon"><ChatDotRound /></el-icon>
              <span class="item-text">{{ item.title }}</span>
              <el-icon
                v-if="activeSessionId === item.sessionId"
                class="item-delete"
                @click.stop="handleDeleteHistory(item.sessionId)"
              >
                <Delete />
              </el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天区 -->
      <div class="chat-main">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <div class="ai-avatar">
              <img :src="logoImg" alt="鲜小花" class="avatar-img" />
            </div>
            <div class="header-titles">
              <div class="header-title">鲜小花</div>
              <div class="header-sub">
                <span class="status-dot"></span>
                在线
              </div>
            </div>
          </div>
          <div class="header-actions">
            <button class="ios-icon-btn" @click="sidebarOpen = !sidebarOpen">
              <el-icon v-if="sidebarOpen"><Back /></el-icon>
              <el-icon v-else><Aim /></el-icon>
            </button>
            <button class="ios-icon-btn" @click="dialogVisible = false">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>

        <!-- 消息区 -->
        <div ref="scrollRef" class="message-scroll">
          <!-- 欢迎页 -->
          <div v-if="messages.length === 0" class="welcome-page">
            <div class="welcome-logo"><img :src="logoImg" alt="鲜小花" class="welcome-logo-img" /></div>
            <div class="welcome-title">你好，我是鲜小花</div>
            <div class="welcome-desc">你的专属鲜花智能助手</div>
          </div>

          <!-- 消息列表 -->
          <div v-else class="message-list">
            <div
              v-for="(msg, idx) in messages"
              :key="idx"
              class="message-row"
              :class="msg.role === 'user' ? 'is-user' : 'is-ai'"
            >
              <div v-if="msg.role === 'ai'" class="msg-avatar ai-avatar-sm">
                <img :src="logoImg" alt="鲜小花" class="avatar-img" />
              </div>
              <div class="msg-body">
                <div
                  class="msg-bubble"
                  :class="{ streaming: msg.streaming, 'loading-bubble': msg.streaming && !msg.content }"
                >
                  <span v-if="msg.streaming && !msg.content" class="loading-dots"><i></i><i></i><i></i></span>
                  <div v-else class="msg-content" v-html="msg.content"></div>
                </div>
              </div>
              <div v-if="msg.role === 'user'" class="msg-avatar user-avatar-sm">
                <img :src="userAvatar" alt="用户" class="avatar-img" />
              </div>
            </div>
          </div>
        </div>

        <!-- 联想词 -->
        <div v-if="suggestions.length > 0" class="suggestions">
          <span
            v-for="(s, i) in suggestions"
            :key="i"
            class="suggestion-tag"
            @click="handleSuggestionClick(s)"
          >
            {{ s }}
          </span>
        </div>

        <!-- 输入区 -->
        <div class="input-area">
          <div class="input-wrapper" :class="{ focused: inputFocused }">
            <textarea
              ref="textareaRef"
              v-model="inputText"
              class="chat-textarea"
              placeholder="输入消息…"
              rows="1"
              @input="handleInput"
              @keydown="handleKeydown"
              @focus="inputFocused = true"
              @blur="inputFocused = false"
            ></textarea>
            <div class="input-footer">
              <span class="char-count">{{ inputText.length }}/2000</span>
              <div class="input-actions">
                <button
                  v-if="loading"
                  class="ios-btn ios-btn-danger ios-btn-sm"
                  @click="handleStop"
                >
                  停止
                </button>
                <button
                  v-else
                  class="ios-btn ios-btn-primary ios-btn-sm send-btn"
                  :disabled="!inputText.trim()"
                  @click="handleSend"
                >
                  <el-icon><Promotion /></el-icon>
                  发送
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus, Delete, ChatDotRound, Promotion,
  Back, Aim, Close
} from '@element-plus/icons-vue'
// AI 接口（后端 branch-ai：ChatController + SessionController）
import {
  startSession,
  getHistorySessions,
  getSessionMessages,
  updateSessionTitle,
  deleteSession,
  abortChat,
  chatStream
} from '@/api/ai/ai.js'
import picHello from '@/assets/image/pic-hello.png'
import logoImg from '@/assets/image/logo1.png'
import userAvatar from '@/assets/image/logo2.png'

const sidebarOpen = ref(true)
const dialogVisible = ref(false) // 悬浮对话弹窗显隐
const scrollRef = ref(null)
const textareaRef = ref(null)
const inputText = ref('')
const inputFocused = ref(false)
const loading = ref(false)
const activeSessionId = ref('')
const messages = ref([])
const suggestions = ref([])
// 历史会话分组（来自 GET /session/history，后端按时间分组返回 Map）
const historyGroups = ref([])
// 标记当前流式是否已被用户停止
let streamStopped = false

// 新建会话时，直接写入一条 AI 问候消息："你好，我是鲜小花" + 后端随机返回的 1 个问题
function applyGreeting(session) {
  const example = session?.examples?.[0]
  const question = example?.describe || example?.title || ''
  const text = question
    ? `你好，我是鲜小花。${question}`
    : '你好，我是鲜小花，请问有什么可以帮您？'
  messages.value.push({
    role: 'ai',
    content: formatContent(text),
    streaming: false,
    liked: false,
    disliked: false
  })
}

function scrollToBottom() {
  nextTick(() => {
    if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
  })
}

// HTML 转义，防止 XSS
function escapeHtml(text) {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

// 纯文本 → 展示 HTML：转义后将换行转为 <br>
function formatContent(text) {
  return escapeHtml(String(text ?? '')).replace(/\n/g, '<br>')
}

/**
 * 会话管理（对接 SessionController）
 */

// 加载历史会话分组
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

// 新建会话 - POST /session
async function handleNewChat() {
  try {
    const session = await startSession()
    activeSessionId.value = session?.sessionId || ''
    messages.value = []
    suggestions.value = []
    applyGreeting(session)
    scrollToBottom()
    loadHistory()
  } catch (e) {
    ElMessage.error('新建会话失败')
  }
}

// 选择历史会话 - GET /session/{sessionId} 加载历史消息
async function handleSelectHistory(item) {
  if (loading.value) return
  activeSessionId.value = item.sessionId
  messages.value = []
  suggestions.value = []
  try {
    const list = (await getSessionMessages(item.sessionId)) || []
    // 后端 MessageVO.type: USER / ASSISTANT
    messages.value = list.map((m) => ({
      role: m.type === 'USER' ? 'user' : 'ai',
      content: formatContent(m.content || ''),
      streaming: false,
      liked: false,
      disliked: false
    }))
  } catch (e) {
    ElMessage.error('加载对话失败')
  }
  scrollToBottom()
}

// 删除历史会话 - DELETE /session?sessionId=
async function handleDeleteHistory(sessionId) {
  try {
    await deleteSession(sessionId)
    historyGroups.value.forEach((g) => {
      g.list = g.list.filter((i) => i.sessionId !== sessionId)
    })
    if (activeSessionId.value === sessionId) {
      messages.value = []
      activeSessionId.value = ''
    }
    ElMessage.success('已删除')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

/**
 * 对话（对接 ChatController SSE）
 */

function handleSuggestionClick(s) {
  inputText.value = s
  suggestions.value = []
  handleSend()
}

function handleInput() {
  nextTick(() => {
    const ta = textareaRef.value
    if (ta) {
      ta.style.height = 'auto'
      ta.style.height = Math.min(ta.scrollHeight, 120) + 'px'
    }
  })
  if (inputText.value.length > 2000) inputText.value = inputText.value.slice(0, 2000)
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

// 发送消息：确保存在会话 → POST /chat 走 SSE 流式
async function handleSend() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 没有会话时先创建（POST /session）
  if (!activeSessionId.value) {
    try {
      const session = await startSession()
      activeSessionId.value = session?.sessionId || ''
      if (messages.value.length === 0) applyGreeting(session)
      loadHistory()
    } catch (e) {
      ElMessage.error('创建会话失败')
      return
    }
  }
  const sessionId = activeSessionId.value
  const isFirstUserMsg = messages.value.filter((m) => m.role === 'user').length === 0

  messages.value.push({ role: 'user', content: escapeHtml(text) })
  inputText.value = ''
  suggestions.value = []
  handleInput()
  scrollToBottom()

  loading.value = true
  streamStopped = false
  const aiMsg = { role: 'ai', content: '', streaming: true, liked: false, disliked: false }
  messages.value.push(aiMsg)
  let rawContent = ''

  await chatStream(
    { question: text, sessionId },
    {
      // 1001 文本数据：后端按增量片段推送，累积后统一格式化（避免拆分导致转义/换行异常）
      onMessage: (chunk) => {
        if (streamStopped) return
        rawContent += chunk
        aiMsg.content = formatContent(rawContent)
        scrollToBottom()
      },
      // 1002 停止事件
      onStop: () => {
        streamStopped = true
      },
      onDone: () => {
        aiMsg.streaming = false
        loading.value = false
        // 首条对话后用用户问题更新会话标题，并刷新历史列表
        if (isFirstUserMsg) {
          updateSessionTitle(sessionId, text.slice(0, 20)).catch(() => {})
          loadHistory()
        }
        scrollToBottom()
      },
      onError: () => {
        aiMsg.streaming = false
        if (!aiMsg.content) aiMsg.content = '（服务异常，请稍后重试）'
        loading.value = false
        ElMessage.error('对话出错了')
      }
    }
  )
}

// 停止生成：前端本地中止流式请求（不依赖后端 stop 接口）
function handleStop() {
  if (!activeSessionId.value) return
  streamStopped = true
  abortChat()
  loading.value = false
}

onMounted(async () => {
  // 启动初始会话，把后端随机返回的 1 条 example 作为 AI 首条消息写入
  try {
    const session = await startSession()
    activeSessionId.value = session?.sessionId || ''
    applyGreeting(session)
    scrollToBottom()
  } catch (e) {
    // 获取失败时不阻塞历史加载
  }
  loadHistory()
})
</script>

<style lang="scss" scoped>

/* ===== 右下角悬浮球 ===== */
.ai-float-ball {
  position: fixed;
  right: 256px;
  bottom: 128px;
  z-index: 2000;
  cursor: pointer;
  user-select: none;
  transition: transform 0.15s ease;

  &:hover {
    transform: translateY(-2px) scale(1.04);
  }
  &:active {
    transform: scale(0.96);
  }

  .ball-img {
    display: block;
    width: 200px;
    height: 200px;
    object-fit: contain;
    filter: drop-shadow(0 6px 16px rgba(255, 55, 95, 0.35));
  }
}

/* ===== 头部操作按钮组 ===== */
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-page {
  height: 600px; /* 悬浮窗口高度：600px */
  background: $ios-bg;
  border-radius: $radius-lg;
  overflow: hidden;
}

.ai-layout {
  display: flex;
  height: 100%;
}

/* ===== 侧边栏 ===== */
.sidebar {
  width: 240px;
  background: $ios-card;
  border-right: 0.5px solid $ios-separator;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;

  &.collapsed {
    width: 0;
    overflow: hidden;
    border-right: none;
  }
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 0.5px solid $ios-separator;

  .sidebar-title {
    font-size: 17px;
    font-weight: 600;
    color: $ios-label;
    letter-spacing: -0.2px;
  }
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 10px;
}

.history-group {
  margin-bottom: 16px;

  .group-title {
    font-size: 11px;
    font-weight: 600;
    color: $ios-label-3;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    padding: 6px 8px;
  }
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: $radius-md;
  cursor: pointer;
  font-size: 14px;
  color: $ios-label-2;
  transition: all 0.15s ease;

  &:hover {
    background: $ios-fill;
  }

  &.active {
    background: rgba($sys-blue, 0.1);
    color: $sys-blue;
  }

  .item-icon {
    font-size: 14px;
    flex-shrink: 0;
    opacity: 0.6;
  }

  .item-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .item-delete {
    font-size: 12px;
    opacity: 0;
    transition: opacity 0.15s;
    &:hover { color: $sys-red; }
  }

  &.active .item-delete { opacity: 1; }
}

/* ===== 聊天主区 ===== */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: $ios-bg;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid $ios-separator;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  /* ===== logo1.png 显示尺寸①：头部头像 ===== */
  .ai-avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }

  .header-title {
    font-size: 16px;
    font-weight: 600;
    color: $ios-label;
    letter-spacing: -0.2px;
    white-space: nowrap;
  }

  .header-sub {
    font-size: 12px;
    color: $ios-label-3;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 1px;
    white-space: nowrap;

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: $sys-green;
    }
  }
}

/* ===== 消息滚动区 ===== */
.message-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  &::-webkit-scrollbar { width: 0; }
}

/* ===== 欢迎页 ===== */
.welcome-page {
  max-width: 640px;
  margin: 48px auto 0;
  text-align: center;

  /* ===== logo1.png 显示尺寸②：消息/加载头像 ===== */
  .welcome-logo {
    margin-bottom: 12px;

    .welcome-logo-img {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      object-fit: cover;
      display: block;
    }
  }

  .welcome-title {
    font-size: 26px;
    font-weight: 700;
    color: $ios-label;
    letter-spacing: -0.5px;
    margin-bottom: 6px;
  }

  .welcome-desc {
    font-size: 15px;
    color: $ios-label-3;
    margin-bottom: 40px;
  }
}

/* ===== 消息列表 ===== */
.message-list {
  max-width: 760px;
  margin: 0 auto;
}

.message-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  align-items: flex-end;

  &.is-user {
    flex-direction: row-reverse;
  }
}

/* ===== logo1.png 显示尺寸③：欢迎页大 logo ===== */
.msg-avatar {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  overflow: hidden;

  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  &.ai-avatar-sm {
    background: transparent;
  }

  &.user-avatar-sm {
    background: transparent;

    .avatar-img {
      object-fit: contain;
      transform: scale(1.0);
      transform-origin: center;
    }
  }
}

.msg-body {
  max-width: 72%;
  display: flex;
  flex-direction: column;
}

.is-user .msg-body { align-items: flex-end; }

.msg-bubble {
  padding: 10px 15px;
  border-radius: 20px;
  font-size: 15px;
  line-height: 1.55;
  word-break: break-word;
  letter-spacing: -0.1px;

  .msg-content {
    :deep(b) { font-weight: 600; }
  }
}

.is-ai .msg-bubble {
  background: $ios-card;
  color: $ios-label;
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.is-user .msg-bubble {
  background: $sys-blue;
  color: #fff;
  border-bottom-right-radius: 6px;
}

.loading-bubble {
  display: flex;
  align-items: center;
  padding: 14px 18px;

  .loading-dots {
    display: inline-flex;
    gap: 4px;

    i {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: $ios-label-3;
      animation: ios-blink 1.4s infinite both;

      &:nth-child(2) { animation-delay: 0.2s; }
      &:nth-child(3) { animation-delay: 0.4s; }
    }
  }
}

@keyframes ios-blink {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1); }
}

/* ===== 联想词 ===== */
.suggestions {
  max-width: 760px;
  margin: 0 auto;
  padding: 0 20px 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-tag {
  padding: 6px 14px;
  background: $ios-card;
  border-radius: 20px;
  font-size: 13px;
  color: $ios-label-2;
  cursor: pointer;
  transition: all 0.15s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);

  &:hover {
    background: rgba($sys-blue, 0.08);
    color: $sys-blue;
  }
}

/* ===== 输入区 ===== */
.input-area {
  padding: 0 20px 20px;
}

.input-wrapper {
  max-width: 760px;
  margin: 0 auto;
  background: $ios-card;
  border-radius: $radius-lg;
  padding: 10px 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s;

  &.focused {
    box-shadow: 0 2px 16px rgba($sys-blue, 0.15);
  }
}

.chat-textarea {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  font-size: 15px;
  line-height: 1.5;
  color: $ios-label;
  background: transparent;
  min-height: 24px;
  max-height: 120px;
  font-family: inherit;

  &::placeholder {
    color: $ios-label-3;
  }
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;

  .char-count {
    font-size: 11px;
    color: $ios-label-3;
  }

  .input-actions {
    display: flex;
    gap: 8px;
  }
}

/* ===== iOS 风格按钮 ===== */
.ios-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  border: none;
  border-radius: $radius-sm;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.15s, transform 0.1s;
  font-family: inherit;
  letter-spacing: -0.1px;

  &:active {
    transform: scale(0.96);
    opacity: 0.7;
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  &.ios-btn-sm {
    padding: 6px 14px;
    font-size: 13px;
  }

  &.ios-btn-primary {
    background: $sys-blue;
    color: #fff;
  }

  &.ios-btn-danger {
    background: $sys-red;
    color: #fff;
  }

  &.ios-btn-ghost {
    background: transparent;
    color: $sys-blue;
    font-weight: 400;
  }
}

.ios-icon-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: $ios-fill;
  color: $ios-label-2;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;

  &:hover {
    background: rgba(120, 120, 128, 0.2);
  }

  &:active {
    transform: scale(0.92);
  }
}

.send-btn {
  .el-icon {
    font-size: 12px;
  }
}
</style>

<!-- 全局样式：el-dialog 经 append-to-body 挂到 body，scoped 作用不到，需非 scoped 覆盖 -->
<style lang="scss">
/* ===== AI 悬浮对话窗口（点击悬浮球后弹出，无遮罩、吸附右下角） ===== */
.ai-chat-dialog {
  // 去除默认内边距与圆角，交由内部 .ai-page 控制
  .el-dialog {
    margin: 0 !important;
    padding: 0;
    border-radius: 18px;
    overflow: hidden;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  }
  .el-dialog__header {
    display: none; // 自定义头部，隐藏默认头部
  }
  .el-dialog__body {
    padding: 0;
  }
  // 无遮罩：让遮罩透明且不拦截页面点击
  &.el-overlay {
    background-color: transparent;
    pointer-events: none;
    .el-dialog {
      pointer-events: auto; // 弹窗本体仍可交互
    }
  }
  // 定位到右下角（吸附悬浮球上方）
  &.el-overlay-dialog {
    position: fixed;
    right: 24px;
    bottom: 110px;
    left: auto;
    top: auto;
    transform: none;
  }
}
</style>
