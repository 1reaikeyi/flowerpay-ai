<template>
  <AdminListPage>
    <!-- 顶部工具栏：刷新列表 + 新增文章 -->
    <template #toolbar>
      <el-button @click="fetchList">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增文章
      </el-button>
    </template>

    <!-- 文章列表：ID / 封面 / 标题 / 分类 / 状态 / 更新时间 / 操作 -->
    <AdminTable :data="tableData" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <!-- 封面：coverImg 可能是文件名、/image/ 路径或外链，统一走 resolveImageUrl -->
      <el-table-column label="封面" width="110" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImg"
            :src="resolveImageUrl(row.coverImg)"
            :preview-src-list="[resolveImageUrl(row.coverImg)]"
            preview-teleported
            fit="contain"
            class="article-cover"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div v-else class="image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="文章标题" min-width="200" show-overflow-tooltip />
      <!-- 分类：列表无分类名时，用分类查询结果反查 categoryId -->
      <el-table-column label="分类" min-width="120">
        <template #default="{ row }">
          {{ row.categoryName || categoryNameMap[row.categoryId] || '-' }}
        </template>
      </el-table-column>
      <!-- 状态：后端 state 只能是「已发布」或「草稿」 -->
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.state === '已发布' ? 'success' : 'info'">
            {{ row.state || '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" min-width="170" />
      <!-- 操作：编辑 / 删除 -->
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-popconfirm
            title="确认删除该文章吗？"
            confirm-button-text="删除"
            cancel-button-text="取消"
            @confirm="handleDelete(row)"
          >
            <template #reference>
              <el-button type="danger" link size="small">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </AdminTable>

    <!-- 分页 -->
    <AdminPagination
      v-model:page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20]"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="fetchList"
    />
  </AdminListPage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Plus, Edit, Delete, Picture } from '@element-plus/icons-vue'
import AdminListPage from '@/components/admin/AdminListPage.vue'
import AdminTable from '@/components/admin/AdminTable.vue'
import AdminPagination from '@/components/admin/AdminPagination.vue'
// API 对齐 ArticleController（/article）与 ArticleDetailController（/article/detail）
import {
  pageArticleList,
  deleteArticles,
  pageArticleCategoryList
} from '@/api/admin/article.js'

const router = useRouter()

// 分页参数：对齐后端 ArticleDetailPageDTO 的 page / pageSize（pageSize 上限 20）
const pagination = reactive({
  page: 1,
  pageSize: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 分类名反查表：categoryId -> categoryName（数据来自 /article/all）
const categoryNameMap = ref({})

// 统一图片 URL 解析策略（本地文件优先，兼容多来源）
//   1. "/image/1.png"      → 直接走 vite /image 代理
//   2. "/img/xxx.png"      → 历史遗留，替换前缀为 /image/
//   3. "uuid-xxx.png"      → 上传接口返回的文件名，走 /api/local?fileName=
//   4. "https://xxx.com/xxx" → 外链直接返回
const resolveImageUrl = (image) => {
  if (!image) return ''
  if (/^https?:\/\//i.test(image)) return image
  if (image.startsWith('/image/')) return image
  if (image.startsWith('/img/')) return '/image/' + image.slice(5)
  if (image.startsWith('/')) return image
  return `/api/local?fileName=${encodeURIComponent(image)}`
}

// 拉取文章分类，用于列表分类名反查
// 后端 /article/all 为分页接口，取第一页最大容量（pageSize 上限 20）
const fetchCategoryMap = async () => {
  try {
    const res = await pageArticleCategoryList({ page: 1, pageSize: 20 })
    const list = res?.data?.list || []
    const map = {}
    list.forEach((item) => {
      map[item.id] = item.categoryName
    })
    categoryNameMap.value = map
  } catch (e) {
    console.error('获取文章分类失败:', e)
  }
}

// 分页查询文章列表
// 后端返回 Result<PageResult<ArticleVO>>：data = { total, list, pageNum, pageSize }
const fetchList = async () => {
  loading.value = true
  try {
    const res = await pageArticleList({
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    if (res?.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('获取文章列表失败:', e)
  } finally {
    loading.value = false
  }
}

// pageSize 变化：回到第一页
const handleSizeChange = () => {
  pagination.page = 1
  fetchList()
}

// 新增文章
const handleAdd = () => {
  router.push('/admin/blog/article/add')
}

// 编辑文章：后端详情接口未返回数据，故携带整行数据回显
const handleEdit = (row) => {
  router.push({
    path: '/admin/blog/article/add',
    query: {
      id: row.id,
      row: encodeURIComponent(JSON.stringify(row))
    }
  })
}

// 删除文章：deleteArticles 接收 id 数组
const handleDelete = async (row) => {
  try {
    await deleteArticles([row.id])
    ElMessage.success('删除成功')
    // 删除后若当前页只剩这一条且不是第一页，回退一页
    if (tableData.value.length === 1 && pagination.page > 1) {
      pagination.page -= 1
    }
    fetchList()
  } catch (e) {
    console.error('删除失败:', e)
  }
}

onMounted(() => {
  fetchCategoryMap()
  fetchList()
})
</script>

<style lang="scss" scoped>
/* ===== iOS 风格：浅灰页面底 + 白色大圆角卡片 + 柔和弥散阴影 ===== */

/* 页面容器：iOS 浅灰背景 + 大圆角 */
:deep(.admin-list-page) {
  background: $ios-bg;
  border-radius: $radius-lg;
}

/* 顶部工具栏：白色卡片化，按钮胶囊化 */
:deep(.admin-toolbar) {
  background: $ios-card;
  border-radius: $radius-lg;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  padding: 16px 20px;
}

:deep(.admin-toolbar .el-button) {
  border-radius: 999px;
  font-weight: 500;
}

/* 表格：白色圆角卡片，分隔线/悬停用 iOS 令牌 */
:deep(.admin-table) {
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  --el-table-border-color: $ios-separator;
  --el-table-tr-bg-color: $ios-card;
  --el-table-row-hover-bg-color: $primary-light;
}

:deep(.admin-table .el-table__body td) {
  color: $ios-label;
}

/* 状态标签 / 分页按钮胶囊化 */
:deep(.el-tag) {
  border-radius: 999px;
}

:deep(.admin-pagination .el-pager li),
:deep(.admin-pagination .btn-prev),
:deep(.admin-pagination .btn-next) {
  border-radius: 999px;
}

.article-cover {
  width: 72px;
  height: 54px;
  border-radius: $radius-sm;
}

.image-placeholder {
  width: 72px;
  height: 54px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $primary-light;
  border-radius: $radius-sm;
  color: rgba(94, 92, 230, 0.5);
}
</style>
