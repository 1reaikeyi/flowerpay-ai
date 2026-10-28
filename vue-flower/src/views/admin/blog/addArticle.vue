<template>
  <AdminFormPage :title="isEdit ? '修改文章' : '添加文章'" @back="goBack">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="article-form"
    >
      <!-- 文章标题 -->
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请填写文章标题"
          maxlength="60"
          show-word-limit
        />
      </el-form-item>

      <!-- 文章分类：数据来自 /article/all（article 表的分类） -->
      <el-form-item label="文章分类" prop="categoryId">
        <el-select
          v-model="formData.categoryId"
          placeholder="请选择文章分类"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="item in categoryList"
            :key="item.id"
            :label="item.categoryName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <!-- 文章封面 -->
      <el-form-item label="文章封面" prop="coverImg">
        <el-upload
          class="cover-uploader"
          :show-file-list="false"
          :before-upload="beforeImageUpload"
          :http-request="handleImageUpload"
        >
          <el-image
            v-if="formData.coverImg"
            :src="resolveImageUrl(formData.coverImg)"
            class="cover-image"
            fit="cover"
          />
          <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">
          图片大小不超过2M<br>
          仅能上传 PNG JPEG JPG 类型图片
        </div>
      </el-form-item>

      <!-- 文章状态：后端只允许「已发布」或「草稿」 -->
      <el-form-item label="文章状态" prop="state">
        <el-radio-group v-model="formData.state">
          <el-radio
            v-for="item in stateOptions"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 文章内容 -->
      <el-form-item label="文章内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="12"
          maxlength="5000"
          placeholder="请输入文章内容"
          show-word-limit
        />
      </el-form-item>

      <!-- 操作按钮 -->
      <AdminFormActions
        :submitting="submitting"
        :show-continue="!isEdit"
        @cancel="goBack"
        @submit="handleSubmit"
      />
    </el-form>
  </AdminFormPage>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AdminFormPage from '@/components/admin/AdminFormPage.vue'
import AdminFormActions from '@/components/admin/AdminFormActions.vue'
// API 对齐 ArticleDetailController（/article/detail）与 ArticleController（/article）
import {
  createArticle,
  updateArticle,
  getArticleById,
  pageArticleCategoryList
} from '@/api/admin/article.js'
import { uploadFile } from '@/api/file/file.js'

const router = useRouter()
const route = useRoute()

const formRef = ref(null)
const submitting = ref(false)

// 是否为编辑模式：query.id 存在即编辑
const isEdit = computed(() => !!route.query.id)

// 文章状态选项：与后端 ArticleDetail.state 取值保持一致
const stateOptions = [
  { value: '已发布', label: '已发布' },
  { value: '草稿', label: '草稿' }
]

// 文章分类列表（article 表）
const categoryList = ref([])

// 表单数据 - 对齐后端 ArticleDetailDTO 字段
const formData = reactive({
  id: null,
  title: '',
  content: '',
  coverImg: '',
  state: '已发布',
  categoryId: null
})

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 60, message: '文章标题长度为2-60个字符', trigger: 'blur' }
  ],
  state: [
    { required: true, message: '请选择文章状态', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

// 返回文章列表页
const goBack = () => {
  router.push('/admin/blog/article')
}

// 统一图片 URL 解析策略（与列表页保持一致）
const resolveImageUrl = (image) => {
  if (!image) return ''
  if (/^https?:\/\//i.test(image)) return image
  if (image.startsWith('/image/')) return image
  if (image.startsWith('/img/')) return '/image/' + image.slice(5)
  if (image.startsWith('/')) return image
  return `/api/local?fileName=${encodeURIComponent(image)}`
}

// 获取文章分类（后端 /article/all 为分页接口，取第一页最大容量）
const fetchCategoryList = async () => {
  try {
    const res = await pageArticleCategoryList({ page: 1, pageSize: 20 })
    categoryList.value = res?.data?.list || []
  } catch (error) {
    console.error('获取文章分类失败:', error)
  }
}

// 编辑模式回显：
// 优先解析列表页携带的 query.row；缺失时再调详情接口兜底
const loadEditData = async () => {
  let record = null

  if (route.query.row) {
    try {
      record = JSON.parse(decodeURIComponent(route.query.row))
    } catch (e) {
      console.error('解析 row 参数失败:', e)
    }
  }

  if (!record) {
    try {
      const res = await getArticleById(route.query.id)
      record = res?.data || null
    } catch (e) {
      console.error('获取文章详情失败:', e)
    }
  }

  if (!record) {
    ElMessage.error('未找到该文章信息')
    goBack()
    return
  }

  formData.id = record.id
  formData.title = record.title ?? ''
  formData.content = record.content ?? ''
  formData.coverImg = record.coverImg ?? ''
  formData.state = record.state || '草稿'
  formData.categoryId = record.categoryId ?? null
}

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 封面图片上传
// 后端返回 data = "{绝对路径}::{保存文件名}"，取 split('::')[1] 作为文件名
const handleImageUpload = async (options) => {
  try {
    const uploadFormData = new FormData()
    uploadFormData.append('file', options.file)
    const res = await uploadFile(uploadFormData)
    if (res?.data) {
      formData.coverImg = String(res.data).split('::')[1] || res.data
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

// 提交表单：按 isEdit 决定 createArticle 或 updateArticle
const handleSubmit = async (continueAdd) => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true

    // 组装与后端 ArticleDetailDTO 对齐的载荷
    const payload = {
      title: formData.title,
      content: formData.content,
      coverImg: formData.coverImg,
      state: formData.state,
      categoryId: formData.categoryId
    }

    if (isEdit.value) {
      payload.id = formData.id
      await updateArticle(payload)
      ElMessage.success('文章修改成功')
      goBack()
    } else {
      await createArticle(payload)
      ElMessage.success('文章添加成功')

      if (continueAdd) {
        formRef.value.resetFields()
        formData.coverImg = ''
        formData.state = '已发布'
      } else {
        goBack()
      }
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCategoryList()
  if (isEdit.value) {
    loadEditData()
  }
})
</script>

<style lang="scss" scoped>
/* 系统色板变量已全局注入，可直接使用 $primary 等 */
/* 页面容器、头部、按钮区通用样式已抽离到公共组件 */

.article-form {
  :deep(.el-input),
  :deep(.el-textarea) {
    width: 100%;
    max-width: 600px;
  }
}

/* 封面上传样式 */
.cover-uploader {
  :deep(.el-upload) {
    border: 1px dashed rgba(10, 132, 255, 0.3);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;

    &:hover {
      border-color: $primary;
    }
  }

  .cover-image {
    width: 240px;
    height: 150px;
    display: block;
  }

  .cover-uploader-icon {
    font-size: 28px;
    color: rgba(94, 92, 230, 0.5);
    width: 240px;
    height: 150px;
    line-height: 150px;
    text-align: center;
  }
}

.upload-tip {
  margin-top: 10px;
  font-size: 12px;
  color: rgba(94, 92, 230, 0.55);
  line-height: 1.6;
}
</style>
