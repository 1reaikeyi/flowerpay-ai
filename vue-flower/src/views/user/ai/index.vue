<template>
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
              <span>🌸</span>
            </div>
            <div class="header-titles">
              <div class="header-title">鲜小花</div>
              <div class="header-sub">
                <span class="status-dot"></span>
                在线
              </div>
            </div>
          </div>
          <button class="ios-icon-btn" @click="sidebarOpen = !sidebarOpen">
            <el-icon v-if="sidebarOpen"><Back /></el-icon>
            <el-icon v-else><Aim /></el-icon>
          </button>
        </div>

        <!-- 消息区 -->
        <div ref="scrollRef" class="message-scroll">
          <!-- 欢迎页 -->
          <div v-if="messages.length === 0" class="welcome-page">
            <div class="welcome-logo">🌸</div>
            <div class="welcome-title">你好，我是鲜小花</div>
            <div class="welcome-desc">你的专属鲜花智能助手</div>
            <div class="hot-section">
              <div class="hot-header">
                <span>热门问题</span>
                <button class="ios-btn ios-btn-ghost ios-btn-sm" @click="shuffleHotQuestions">
                  换一换
                </button>
              </div>
              <div class="hot-grid">
                <div
                  v-for="(q, i) in hotQuestions"
                  :key="i"
                  class="hot-card"
                  @click="handleHotClick(q)"
                >
                  <div class="hot-icon">{{ q.icon }}</div>
                  <div class="hot-text">
                    <div class="hot-title">{{ q.title }}</div>
                    <div class="hot-desc">{{ q.desc }}</div>
                  </div>
                </div>
              </div>
            </div>
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
                <span>🌸</span>
              </div>
              <div class="msg-body">
                <div class="msg-bubble" :class="{ streaming: msg.streaming }">
                  <div class="msg-content" v-html="msg.content"></div>
                </div>
                <div v-if="msg.role === 'ai' && !msg.streaming && msg.content" class="msg-actions">
                  <span class="action-btn" :class="{ active: msg.liked }" @click="handleLike(msg)">
                    <el-icon><CaretTop /></el-icon>
                  </span>
                  <span class="action-btn" :class="{ active: msg.disliked }" @click="handleDislike(msg)">
                    <el-icon><CaretBottom /></el-icon>
                  </span>
                  <span class="action-btn" @click="handleCopy(msg)">
                    <el-icon><CopyDocument /></el-icon>
                  </span>
                  <span v-if="idx === messages.length - 1" class="action-btn" @click="handleRegenerate">
                    <el-icon><RefreshRight /></el-icon>
                  </span>
                </div>
              </div>
              <div v-if="msg.role === 'user'" class="msg-avatar user-avatar-sm">
                <el-icon><User /></el-icon>
              </div>
            </div>

            <!-- 加载中 -->
            <div v-if="loading" class="message-row is-ai">
              <div class="msg-avatar ai-avatar-sm"><span>🌸</span></div>
              <div class="msg-body">
                <div class="msg-bubble loading-bubble">
                  <span class="loading-dots"><i></i><i></i><i></i></span>
                </div>
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
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus, Delete, ChatDotRound, User, Promotion,
  CaretTop, CaretBottom, CopyDocument, RefreshRight,
  Back, Aim
} from '@element-plus/icons-vue'
// AI 接口（后端 branch-ai：ChatController + SessionController）
import {
  startSession,
  getHistorySessions,
  getSessionMessages,
  updateSessionTitle,
  deleteSession,
  stopChat,
  chatStream
} from '@/api/user/ai.js'

const sidebarOpen = ref(true)
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

const allHotQuestions = [
  { icon: '🎂', title: '推荐生日鲜花', desc: '帮我推荐几款适合生日送的花束' },
  { icon: '💝', title: '情人节选花', desc: '情人节送女朋友什么花比较好' },
  { icon: '📦', title: '查询订单', desc: '帮我查一下最近的订单状态' },
  { icon: '🎁', title: '优惠活动', desc: '今天有什么优惠活动吗' },
  { icon: '🌹', title: '了解花语', desc: '不同颜色玫瑰花的花语是什么' },
  { icon: '👩', title: '送妈妈的花', desc: '母亲节送什么花最合适' }
]
const hotQuestions = ref([])

function shuffleHotQuestions() {
  hotQuestions.value = [...allHotQuestions].sort(() => Math.random() - 0.5).slice(0, 4)
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

/* ================= 会话管理（对接 SessionController） ================= */

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

/* ================= 对话（对接 ChatController SSE） ================= */

function handleHotClick(q) {
  inputText.value = q.desc
  handleSend()
}
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

// 停止生成 - POST /chat/stop
function handleStop() {
  if (!activeSessionId.value) return
  streamStopped = true
  stopChat(activeSessionId.value).catch(() => {})
  loading.value = false
}

function handleLike(msg) {
  msg.liked = !msg.liked
  if (msg.liked) msg.disliked = false
}
function handleDislike(msg) {
  msg.disliked = !msg.disliked
  if (msg.disliked) msg.liked = false
}
function handleCopy(msg) {
  navigator.clipboard?.writeText(msg.content.replace(/<[^>]+>/g, ''))
  ElMessage.success('已复制')
}
// 重新生成：移除最后一条 AI 回复，以最近一条用户问题再次发送
function handleRegenerate() {
  if (messages.value.length < 2 || loading.value) return
  const lastUser = [...messages.value].reverse().find((m) => m.role === 'user')
  if (!lastUser) return
  const lastIdx = messages.value.length - 1
  if (messages.value[lastIdx].role === 'ai') messages.value.splice(lastIdx, 1)
  inputText.value = lastUser.content.replace(/<[^>]+>/g, '')
  handleSend()
}

onMounted(() => {
  shuffleHotQuestions()
  loadHistory()
})
</script>

<style lang="scss" scoped>
/* ========== Apple iOS 风格 ========== */
$ios-bg: #F2F2F7;
$ios-card: #FFFFFF;
$ios-label: #1C1C1E;
$ios-label-2: #3C3C43;
$ios-label-3: #8E8E93;
$ios-separator: rgba(60, 60, 67, 0.12);
$ios-fill: rgba(120, 120, 128, 0.12);
$ios-blue: #0A84FF;
$ios-green: #30D158;
$ios-red: #FF3B30;
$radius-lg: 18px;
$radius-md: 12px;
$radius-sm: 8px;

.ai-page {
  height: calc(100vh - 120px);
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
    background: rgba($ios-blue, 0.1);
    color: $ios-blue;
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
    &:hover { color: $ios-red; }
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

  .ai-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, #FF9F0A, #FF375F);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
  }

  .header-title {
    font-size: 16px;
    font-weight: 600;
    color: $ios-label;
    letter-spacing: -0.2px;
  }

  .header-sub {
    font-size: 12px;
    color: $ios-label-3;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 1px;

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: $ios-green;
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

  .welcome-logo {
    font-size: 52px;
    margin-bottom: 12px;
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

.hot-section {
  text-align: left;

  .hot-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
    font-size: 13px;
    font-weight: 600;
    color: $ios-label-2;
  }
}

.hot-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.hot-card {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  background: $ios-card;
  border-radius: $radius-md;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  }

  &:active {
    transform: scale(0.98);
  }

  .hot-icon {
    font-size: 26px;
    flex-shrink: 0;
  }

  .hot-title {
    font-size: 14px;
    font-weight: 600;
    color: $ios-label;
    margin-bottom: 2px;
  }

  .hot-desc {
    font-size: 12px;
    color: $ios-label-3;
    line-height: 1.4;
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

.msg-avatar {
  flex-shrink: 0;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;

  &.ai-avatar-sm {
    background: linear-gradient(135deg, #FF9F0A, #FF375F);
  }

  &.user-avatar-sm {
    background: $ios-blue;
    color: #fff;
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
  background: $ios-blue;
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

/* ===== 消息操作栏 ===== */
.msg-actions {
  display: flex;
  gap: 2px;
  margin-top: 4px;
  padding: 0 4px;
  opacity: 0;
  transition: opacity 0.15s;

  .message-row:hover & { opacity: 1; }
}

.is-user .msg-actions { display: none; }

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  cursor: pointer;
  color: $ios-label-3;
  font-size: 12px;
  transition: all 0.15s;

  &:hover {
    background: $ios-fill;
    color: $ios-blue;
  }

  &.active {
    color: $ios-blue;
  }
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
    background: rgba($ios-blue, 0.08);
    color: $ios-blue;
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
    box-shadow: 0 2px 16px rgba($ios-blue, 0.15);
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
    background: $ios-blue;
    color: #fff;
  }

  &.ios-btn-danger {
    background: $ios-red;
    color: #fff;
  }

  &.ios-btn-ghost {
    background: transparent;
    color: $ios-blue;
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
