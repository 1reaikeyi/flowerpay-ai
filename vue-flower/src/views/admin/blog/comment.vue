<template>
  <div class="comment-page">
    <!-- ==================== 列表视图：分页博客 ==================== -->
    <AdminListPage v-if="view === 'list'">
      <template #toolbar>
        <el-button @click="fetchList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </template>

      <!-- 博客列表：ID / 标题 / 内容 / 操作 -->
      <AdminTable :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="博客标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容摘要" min-width="280" show-overflow-tooltip />
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">
              <el-icon><ChatDotRound /></el-icon>
              查看评论
            </el-button>
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

    <!-- ==================== 详情视图：博客卡片 + 评论区 ==================== -->
    <div v-else class="page-inner">
      <!-- 顶部：返回列表 -->
      <el-card class="selector-card" shadow="never">
        <el-button round @click="backToList">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </el-card>

      <!-- 博客卡片 + 评论区（iOS 风格） -->
      <el-card v-if="blog" class="blog-card" shadow="never">
        <h1 class="blog-title">{{ blog.title }}</h1>
        <div class="blog-meta">
          <span>作者：用户 #{{ blog.userId }}</span>
          <span>发布时间：{{ blog.createTime }}</span>
          <span>阅读：{{ viewCount }}</span>
        </div>

        <!-- 博客图片：多图逗号分隔，文件名走 /api/local?fileName= -->
        <div v-if="imageList.length" class="blog-images">
          <el-image
            v-for="(img, i) in imageList"
            :key="i"
            :src="resolveImageUrl(img)"
            :preview-src-list="imageList.map(resolveImageUrl)"
            :initial-index="i"
            preview-teleported
            fit="cover"
            class="blog-image"
          />
        </div>

        <div class="blog-content">
          <p v-for="(line, i) in contentLines" :key="i">{{ line }}</p>
        </div>

        <!-- 互动栏：iOS 胶囊样式 -->
        <div class="action-bar">
          <span class="action-item">👍 点赞 {{ blog.liked || 0 }}</span>
          <span class="action-item">💬 评论 {{ comments.length }}</span>
        </div>

        <!-- 评论区 -->
        <h3 class="comment-title">评论区</h3>
        <div class="comment-form">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="4"
            placeholder="写下你的评论..."
            class="comment-input"
          />
          <el-button
            type="primary"
            round
            class="submit-btn"
            :loading="submitting"
            @click="submitComment"
          >
            发表评论
          </el-button>
        </div>

        <div v-if="comments.length" class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-username">
                用户 #{{ c.userId }}
                <el-tag v-if="c.parentId === 1" size="small" type="info" round>
                  回复 评论 #{{ c.answerId }}
                </el-tag>
              </span>
              <span class="comment-time">{{ c.createTime }}</span>
            </div>
            <div class="comment-text">{{ c.content }}</div>
            <div class="comment-reply" @click="toggleReply(c.id)">回复</div>

            <!-- 内联回复框 -->
            <div v-if="replyToId === c.id" class="reply-form">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 评论 #${c.id}...`"
              />
              <div class="reply-actions">
                <el-button
                  type="primary"
                  size="small"
                  round
                  :loading="submitting"
                  @click="submitReply(c.id)"
                >
                  提交
                </el-button>
                <el-button size="small" round @click="replyToId = null">取消</el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无评论" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, ArrowLeft, ChatDotRound } from '@element-plus/icons-vue'
import AdminListPage from '@/components/admin/AdminListPage.vue'
import AdminTable from '@/components/admin/AdminTable.vue'
import AdminPagination from '@/components/admin/AdminPagination.vue'
import {
  pageBlogList,
  getBlogById,
  getBlogViewCount,
  listBlogComments,
  createBlogComment,
  replyBlogComment
} from '@/api/admin/comment.js'

const route = useRoute()

// 视图切换：list 博客分页列表 / detail 博客详情 + 评论区
const view = ref('list')

// ---------- 列表视图状态 ----------
// 分页参数：对齐后端 BlogPageDTO 的 page / pageSize（pageSize 上限 20）
const pagination = reactive({
  page: 1,
  pageSize: 10
})
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// ---------- 详情视图状态 ----------
const blog = ref(null)
const viewCount = ref(0)
const comments = ref([])
const submitting = ref(false)
const commentContent = ref('')
const replyToId = ref(null)
const replyContent = ref('')

// 图片 URL 解析：外链直接用，否则当作后端文件名拼接
const resolveImageUrl = (image) => {
  if (!image) return ''
  if (/^https?:\/\//i.test(image)) return image
  return `/api/local?fileName=${encodeURIComponent(image)}`
}

// 博客图片列表：images 为逗号分隔字符串
const imageList = computed(() =>
  (blog.value?.images || '').split(',').map((s) => s.trim()).filter(Boolean)
)

// 博客内容按换行拆成段落展示
const contentLines = computed(() =>
  (blog.value?.content || '').split('\n').filter((s) => s.trim())
)

// ==================== 列表视图 ====================
// 分页查询博客列表：GET /blog/all
// 后端返回 Result<PageResult<BlogVO>>：data = { total, list, pageNum, pageSize }
const fetchList = async () => {
  loading.value = true
  try {
    const res = await pageBlogList({
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
    console.error('获取博客列表失败:', e)
  } finally {
    loading.value = false
  }
}

// pageSize 变化：回到第一页
const handleSizeChange = () => {
  pagination.page = 1
  fetchList()
}

// ==================== 详情视图 ====================
// 打开详情：加载博客 + 浏览量 + 评论列表
const openDetail = async (id) => {
  loading.value = true
  try {
    const res = await getBlogById(id)
    if (!res?.data) {
      ElMessage.warning('博客不存在')
      return
    }
    blog.value = res.data
    view.value = 'detail'
    // 浏览量失败不阻塞主内容
    try {
      const viewRes = await getBlogViewCount(id)
      viewCount.value = viewRes?.data ?? 0
    } catch (e) {
      viewCount.value = 0
    }
    await fetchComments()
  } catch (e) {
    console.error('加载博客失败:', e)
  } finally {
    loading.value = false
  }
}

// 返回列表并重置详情状态
const backToList = () => {
  view.value = 'list'
  blog.value = null
  comments.value = []
  commentContent.value = ''
  replyToId.value = null
}

// 刷新评论列表
const fetchComments = async () => {
  if (!blog.value) return
  try {
    const res = await listBlogComments(blog.value.id)
    comments.value = res?.data || []
  } catch (e) {
    console.error('获取评论列表失败:', e)
  }
}

// 发表博客评论：POST /blog/comments/0/{blogId}
const submitComment = async () => {
  const content = commentContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    await createBlogComment(blog.value.id, content)
    ElMessage.success('评论成功')
    commentContent.value = ''
    await fetchComments()
  } catch (e) {
    console.error('评论失败:', e)
  } finally {
    submitting.value = false
  }
}

// 展开/收起回复框
const toggleReply = (id) => {
  replyToId.value = replyToId.value === id ? null : id
  replyContent.value = ''
}

// 回复评论：POST /blog/comments/1/{commentId}
const submitReply = async (id) => {
  const content = replyContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    await replyBlogComment(id, content)
    ElMessage.success('回复成功')
    replyToId.value = null
    replyContent.value = ''
    await fetchComments()
  } catch (e) {
    console.error('回复失败:', e)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 支持 ?id= 直达某博客的评论区，否则默认分页列表
  if (route.query.id) {
    openDetail(route.query.id)
  } else {
    fetchList()
  }
})
</script>

<style lang="scss" scoped>
/* ===== iOS 风格：浅灰页面底 + 白色大圆角卡片 + 柔和弥散阴影 ===== */
.comment-page {
  line-height: 1.7;
  color: $ios-label;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Helvetica Neue", "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* ---------- 列表视图：iOS 覆盖 ---------- */
:deep(.admin-list-page) {
  background: $ios-bg;
  border-radius: $radius-lg;
}

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

:deep(.admin-pagination .el-pager li),
:deep(.admin-pagination .btn-prev),
:deep(.admin-pagination .btn-next) {
  border-radius: 999px;
}

/* ---------- 详情视图 ---------- */
.page-inner {
  max-width: 800px;
  margin: 0 auto;
  background: $ios-bg;
  border-radius: $radius-lg;
  min-height: calc(100vh - 120px);
  padding: 20px;
}

/* 卡片统一：白色、18px 大圆角、弥散阴影、去边框 */
.selector-card,
.blog-card {
  border: none;
  border-radius: $radius-lg;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.selector-card {
  margin-bottom: 16px;
}

.blog-card {
  .blog-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 12px;
    color: $ios-label;
    letter-spacing: -0.5px;
  }

  .blog-meta {
    display: flex;
    gap: 16px;
    color: $ios-label-3;
    font-size: 13px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 0.5px solid $ios-separator;
  }

  .blog-images {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;

    .blog-image {
      width: 160px;
      height: 160px;
      border-radius: $radius-md;
    }
  }

  .blog-content {
    font-size: 16px;
    color: $ios-label-2;
    margin-bottom: 24px;

    p {
      margin-bottom: 16px;
    }
  }

  /* 互动栏：iOS 灰色填充胶囊 */
  .action-bar {
    display: flex;
    gap: 12px;
    padding-bottom: 24px;
    border-bottom: 0.5px solid $ios-separator;
    margin-bottom: 28px;

    .action-item {
      background: $ios-fill;
      border-radius: 999px;
      padding: 6px 16px;
      font-size: 13px;
      color: $ios-label-2;
    }
  }

  .comment-title {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 20px;
    color: $ios-label;
  }

  .comment-form {
    margin-bottom: 30px;

    .comment-input {
      :deep(.el-textarea__inner) {
        border-radius: $radius-md;
        padding: 14px;
        font-size: 15px;
        box-shadow: 0 0 0 0.5px $ios-separator;

        &:focus {
          box-shadow: 0 0 0 1px $primary;
        }
      }
    }

    .submit-btn {
      margin-top: 12px;
    }
  }

  .comment-item {
    padding: 18px 0;
    border-bottom: 0.5px solid $ios-separator;

    &:last-child {
      border-bottom: none;
    }

    .comment-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      .comment-username {
        font-weight: 600;
        color: $ios-label;
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .comment-time {
        font-size: 13px;
        color: $ios-label-3;
      }
    }

    .comment-text {
      color: $ios-label-2;
      font-size: 15px;
    }

    .comment-reply {
      margin-top: 10px;
      font-size: 14px;
      color: $primary;
      cursor: pointer;
      display: inline-block;

      &:hover {
        opacity: 0.7;
      }
    }

    .reply-form {
      margin-top: 10px;

      :deep(.el-textarea__inner) {
        border-radius: $radius-md;
      }

      .reply-actions {
        margin-top: 8px;
        display: flex;
        gap: 8px;
      }
    }
  }
}
</style>
