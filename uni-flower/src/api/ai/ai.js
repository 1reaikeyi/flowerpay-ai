/**
 * AI 助手 API（uni-app 适配版）
 * 后端服务端口 8081，小程序不能走 vite 代理，需直连
 */
import { AI_BASE_URL } from '@/utils/request.js'

const BASE = AI_BASE_URL

// 当前请求 task（用于中断）
let currentTask = null

/**
 * 中止当前正在进行的流式对话
 */
export function abortChat() {
  if (currentTask) {
    currentTask.abort()
    currentTask = null
  }
}

/**
 * 通用请求（非流式）
 */
async function request(path, options = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE + path,
      method: options.method || 'GET',
      data: options.data || {},
      header: { 'Content-Type': 'application/json' },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const body = res.data
          if (body && typeof body === 'object' && 'code' in body) {
            if (body.code === 200) {
              resolve(body.data)
            } else {
              reject(new Error(body.msg || '请求失败'))
            }
          } else {
            resolve(body)
          }
        } else {
          reject(new Error('请求失败'))
        }
      },
      fail: (err) => reject(err)
    })
  })
}

/* ================= SessionController ================= */

export const startSession = () => request('/session', { method: 'POST' })
export const getHistorySessions = () => request('/session/history', { method: 'GET' })
export const getSessionMessages = (sessionId) =>
  request(`/session/${encodeURIComponent(sessionId)}`, { method: 'GET' })
export const updateSessionTitle = (sessionId, title) =>
  request(`/session/title?sessionId=${encodeURIComponent(sessionId)}&title=${encodeURIComponent(title)}`, { method: 'PUT' })
export const deleteSession = (sessionId) =>
  request(`/session?sessionId=${encodeURIComponent(sessionId)}`, { method: 'DELETE' })

/* ================= ChatController - SSE 流式 ================= */

/**
 * 流式对话 - POST /chat（SSE）
 * uni-app 不支持原生 SSE，使用 uni.request 配合 onChunkReceived 接收流式数据
 */
export const chatStream = async (payload, callbacks = {}) => {
  abortChat()

  const task = uni.request({
    url: BASE + '/chat',
    method: 'POST',
    data: { question: payload.question, sessionId: payload.sessionId },
    header: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream'
    },
    enableChunked: true,
    success: (res) => {
      // 请求完成
      if (res.statusCode >= 200 && res.statusCode < 300) {
        callbacks.onDone?.()
      } else {
        callbacks.onError?.(new Error('对话请求失败'))
      }
      currentTask = null
    },
    fail: (err) => {
      if (err.errMsg && err.errMsg.includes('abort')) return
      callbacks.onError?.(err)
      currentTask = null
    }
  })

  currentTask = task

  let buffer = ''

  task.onChunkReceived((res) => {
    // res.data 是 ArrayBuffer
    const chunk = arrayBufferToString(res.data)
    buffer += chunk

    // SSE 事件以空行分隔
    let idx
    while ((idx = buffer.indexOf('\n\n')) !== -1) {
      const rawEvent = buffer.slice(0, idx)
      buffer = buffer.slice(idx + 2)
      if (rawEvent) processEvent(rawEvent)
    }
  })

  function arrayBufferToString(buffer) {
    if (typeof buffer === 'string') return buffer
    const arr = new Uint8Array(buffer)
    let str = ''
    for (let i = 0; i < arr.length; i++) {
      str += String.fromCharCode(arr[i])
    }
    // 尝试 UTF-8 解码
    try {
      return decodeURIComponent(escape(str))
    } catch (e) {
      return str
    }
  }

  function processEvent(rawEvent) {
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
      return
    }
    switch (evt.eventType) {
      case 1001:
        callbacks.onMessage?.(evt.eventData == null ? '' : String(evt.eventData))
        break
      case 1002:
        callbacks.onStop?.()
        break
      case 1003:
        callbacks.onParams?.(evt.eventData)
        break
      default:
        break
    }
  }
}
