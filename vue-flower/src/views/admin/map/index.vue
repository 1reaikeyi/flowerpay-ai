<template>
  <div class="map-page">
    <div v-if="loading" class="map-loading">地图加载中...</div>
    <div ref="containerRef" class="map-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { searchHouse } from '@/api/admin/map.js'

// 高德地图 Key 与脚本地址（和 branch-navigation/static/search.html 保持一致）
const AMAP_KEY = '8504568070dfdad8a7c57fb56f2c20c4'
const AMAP_URL = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&plugin=AMap.Scale`

const containerRef = ref(null)
const loading = ref(true)

// 地图实例与当前覆盖物，组件卸载时统一销毁
let map = null
let markers = []
let refreshTimer = null

// 动态加载高德地图 JS API，避免在每个页面都引入脚本
function loadAMap() {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve(window.AMap)
      return
    }
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = AMAP_URL
    script.onload = () => resolve(window.AMap)
    script.onerror = () => reject(new Error('高德地图加载失败'))
    document.head.appendChild(script)
  })
}

// 显示当前可视范围内的行政区覆盖物
function showInfo() {
  if (!map) return
  const bound = map.getBounds()
  if (!bound) return
  const sw = bound.getSouthWest()
  const ne = bound.getNorthEast()
  const zoom = map.getZoom()

  searchHouse({
    maxLongitude: ne.getLng(),
    minLongitude: sw.getLng(),
    maxLatitude: ne.getLat(),
    minLatitude: sw.getLat(),
    zoom
  })
    .then((response) => {
      clearMapMarker()
      const data = response.data
      showMapMarker(Array.isArray(data) ? data : [])
    })
    .catch((error) => {
      console.warn('请求 /house/search 失败', error)
      ElMessage.error('房源搜索接口请求失败')
      clearMapMarker()
    })
}

// 显示覆盖物
function showMapMarker(data) {
  for (const vo of data) {
    const html = `<div class="district">${vo.name}<span>${vo.price}万</span><i>${vo.total}套</i></div>`
    const marker = new window.AMap.Marker({
      position: [vo.longitude, vo.latitude], // 高德坐标顺序为 [经度, 纬度]
      content: html,
      anchor: 'center',
      bubble: true,
      zIndex: 100
    })
    map.add(marker)
    markers.push(marker)
  }
}

// 清除覆盖物
function clearMapMarker() {
  if (markers.length > 0) {
    map.remove(markers)
    markers = []
  }
}

// 缩放时 moveend、zoomend 可能都触发，这里去抖，避免重复请求
function refresh() {
  clearTimeout(refreshTimer)
  refreshTimer = setTimeout(showInfo, 200)
}

onMounted(async () => {
  try {
    await loadAMap()
  } catch (error) {
    loading.value = false
    ElMessage.error(error.message)
    return
  }

  // 地图默认位置，上海市
  const defaultX = 121.48130241985999
  const defaultY = 31.235156971414239
  const defaultZoom = 12

  map = new window.AMap.Map(containerRef.value, {
    center: [defaultX, defaultY],
    zoom: defaultZoom,
    viewMode: '2D',
    resizeEnable: true,
    scrollWheel: true
  })

  // 比例尺插件，右下角
  map.addControl(new window.AMap.Scale({ position: 'RB' }))

  map.on('movestart', clearMapMarker)
  map.on('zoomstart', clearMapMarker)
  map.on('moveend', refresh)
  map.on('zoomend', refresh)
  map.on('complete', () => {
    loading.value = false
    showInfo()
  })
})

onBeforeUnmount(() => {
  clearTimeout(refreshTimer)
  if (map) {
    map.remove(markers)
    markers = []
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.map-page {
  position: relative;
  width: 100%;
  height: calc(100vh - 160px);
  min-height: 480px;
  background: #fff;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-loading {
  position: absolute;
  z-index: 10;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  color: #666;
}
</style>

<style>
/* 覆盖物内容是字符串注入到地图容器里的，不能用 scoped 样式 */
.district {
  width: 84px;
  height: 84px;
  line-height: 16px;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 1px solid transparent;
  border-radius: 50%;
  overflow: hidden;
  text-align: center;
  font-family: PingFangSC-Semibold, sans-serif;
  color: #fff;
  background: #00ae66;
  box-sizing: border-box;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.district i {
  font-size: 12px;
  color: hsla(0, 0%, 100%, 0.7);
  line-height: 12px;
  margin-top: 4px;
  font-style: normal;
}
</style>
