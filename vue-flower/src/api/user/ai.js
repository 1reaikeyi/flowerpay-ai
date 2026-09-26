/**
 * AI 助手 API（对齐后端 branch-ai 模块）
 * 后端服务端口 8081，经 vite 代理 /api/ai 转发（rewrite 去掉 /api/ai 前缀）
 *   - ChatController    @RequestMapping("chat")     → /chat/**
 *   - SessionController @RequestMapping("/session") → /session/**
 *
 * 后端统一返回 Result { code, msg, data }（成功 code=200）；
 * 但 GET /session/{sessionId} 直接返回 List<MessageVO>，不包 Result，需单独处理。
 */

const BASE = '/api/ai'

/**
 * 通用请求：自动解包 Result；非 Result 结构（如纯数组）原样返回。
 */
async function request(path, options = {}) {
  const resp = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  const text = await resp.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }
  if (!resp.ok) {
    throw new Error((data && data.msg) || '请求失败')
  }
  // Result 包装结构
  if (data && typeof data === 'object' && 'code' in data) {
    if (data.code !== 200) {
      throw new Error(data.msg || '请求失败')
    }
    return data.data
  }
  // 非 Result（如 List<MessageVO>）
  return data
}

/* ================= SessionController ================= */

/**
 * 新建会话 - POST /session
 * @returns {Promise<{sessionId:string, title:string, describe?:string, examples?:any[]}>}
 */
export const startSession = () => request('/session', { method: 'POST' })

/**
 * 历史会话列表（按分组） - GET /session/history
 * @returns {Promise<Record<string, Array<{sessionId:string, title:string, updateTime:string}>>>}
 */
export const getHistorySessions = () => request('/session/history', { method: 'GET' })

/**
 * 查询单个会话的历史消息 - GET /session/{sessionId}
 * 注意：后端直接返回 List<MessageVO>，不包 Result
 * @returns {Promise<Array<{type:'USER'|'ASSISTANT', content:string, params?:any}>>}
 */
export const getSessionMessages = (sessionId) =>
  request(`/session/${encodeURIComponent(sessionId)}`, { method: 'GET' })

/**
 * 更新会话标题 - PUT /session/title?sessionId=&title=
 */
export const updateSessionTitle = (sessionId, title) =>
  request(`/session/title?sessionId=${encodeURIComponent(sessionId)}&title=${encodeURIComponent(title)}`, {
    method: 'PUT'
  })

/**
 * 删除历史会话 - DELETE /session?sessionId=
 */
export const deleteSession = (sessionId) =>
  request(`/session?sessionId=${encodeURIComponent(sessionId)}`, { method: 'DELETE' })

/* ================= ChatController ================= */

/**
 * 停止生成 - POST /chat/stop?sessionId=
 */
export const stopChat = (sessionId) =>
  request(`/chat/stop?sessionId=${encodeURIComponent(sessionId)}`, { method: 'POST' })

/**
 * 流式对话 - POST /chat（SSE, text/event-stream）
 * 后端返回 Flux<ChatEventVO>，每个事件为 JSON：{ eventData, eventType }
 *   eventType: 1001=文本数据, 1002=停止事件, 1003=参数事件
 *
 * @param {{question:string, sessionId:string}} payload
 * @param {{
 *   onMessage?: (text:string)=>void,
 *   onStop?: ()=>void,
 *   onParams?: (data:any)=>void,
 *   onDone?: ()=>void,
 *   onError?: (err:Error)=>void
 * }} callbacks
 * @returns {Promise<void>}
 */
export const chatStream = async (payload, callbacks = {}) => {
  let resp
  try {
    resp = await fetch(BASE + '/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'text/event-stream' },
      body: JSON.stringify({ question: payload.question, sessionId: payload.sessionId })
    })
  } catch (e) {
    callbacks.onError?.(e)
    return
  }
  if (!resp.ok || !resp.body) {
    callbacks.onError?.(new Error('对话请求失败'))
    return
  }

  const reader = resp.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''

  const processEvent = (rawEvent) => {
    // SSE 事件可能含多行，取 data: 开头的行
    const dataLine = rawEvent
      .split('\n')
      .map((l) => l.trim())
      .find((l) => l.startsWith('data:'))
    if (!dataLine) return
    const json = dataLine.slice(5).trim()
    if (!json) return
    let evt
    try {
      evt = JSON.parse(json)
    } catch {
      // 非 JSON（如 Spring 的 "[DONE]" 等），忽略
      return
    }
    switch (evt.eventType) {
      case 1001: // 文本数据
        callbacks.onMessage?.(evt.eventData == null ? '' : String(evt.eventData))
        break
      case 1002: // 停止事件
        callbacks.onStop?.()
        break
      case 1003: // 参数事件
        callbacks.onParams?.(evt.eventData)
        break
      default:
        break
    }
  }

  try {
    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      // SSE 事件以空行（\n\n）分隔
      let idx
      while ((idx = buffer.indexOf('\n\n')) !== -1) {
        const rawEvent = buffer.slice(0, idx)
        buffer = buffer.slice(idx + 2)
        if (rawEvent) processEvent(rawEvent)
      }
    }
    // 处理缓冲区剩余
    if (buffer.trim()) processEvent(buffer)
    callbacks.onDone?.()
  } catch (e) {
    callbacks.onError?.(e)
  }
}
