<template>
  <el-card class="profile-card">
    <template #header>
      <span>当前员工信息</span>
    </template>

    <!-- 只读展示 -->
    <el-descriptions v-if="!editing" :column="2" border>
      <el-descriptions-item label="ID">{{ user.id }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
      <el-descriptions-item label="在职职位">{{ user.work }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ user.sex }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="user.status === 1 ? 'success' : 'danger'">
          {{ user.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="头像">
        <el-avatar :src="avatarUrl" :size="80" />
      </el-descriptions-item>
    </el-descriptions>

    <!-- 编辑模式：修改个人信息（职位 / 性别 / 手机号 / 邮箱） -->
    <el-form
      v-else
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="profile-form"
    >
      <el-form-item label="用户名">
        <el-input :model-value="user.username" disabled />
      </el-form-item>
      <el-form-item label="在职职位" prop="work">
        <el-input v-model="form.work" placeholder="请输入在职职位" maxlength="32" />
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="64" />
      </el-form-item>
    </el-form>

    <div class="actions">
      <template v-if="!editing">
        <el-button type="primary" @click="startEdit">编辑信息</el-button>
        <el-button @click="refresh">刷新</el-button>
        <el-button type="primary" @click="$router.push('/admin/employee/avatar')">更换头像</el-button>
        <el-button type="primary" plain @click="$router.push('/admin/employee/password')">重置密码</el-button>
      </template>
      <template v-else>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        <el-button @click="cancelEdit">取消</el-button>
      </template>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useEmployeeStore } from '@/stores/index.js'
import defaultAvatar from '@/assets/login/avatar.png'
// 1.6 修改个人信息 - PUT /admin/employee（RequestBody EmployeeDTO）
import { updateEmployeeInfo } from '@/api/admin/admin.js'

const employeeStore = useEmployeeStore()
const user = computed(() => employeeStore.user || {})

// 头像 URL：后端返回的是文件名，需要拼接 /api/local?fileName=
const avatarUrl = computed(() => {
  const a = user.value.avatar
  if (!a) return defaultAvatar
  return /^https?:\/\//.test(a) ? a : `/api/local?fileName=${a}`
})

// ---------- 个人信息编辑 ----------
const editing = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = reactive({
  work: '',
  sex: '男',
  phone: '',
  email: ''
})

// 手机号校验
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 邮箱校验（可空）
const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const rules = {
  work: [
    { required: true, message: '请输入在职职位', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

// 进入编辑：用当前用户数据回填表单
const startEdit = () => {
  form.work = user.value.work || ''
  form.sex = user.value.sex || '男'
  form.phone = user.value.phone || ''
  form.email = user.value.email || ''
  editing.value = true
}

const cancelEdit = () => {
  editing.value = false
}

// 保存：调用 PUT /admin/employee 修改个人信息
const handleSave = async () => {
  if (!formRef.value) return
  if (!user.value.id) {
    ElMessage.error('缺少员工 ID，请重新登录')
    return
  }
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  saving.value = true
  try {
    // 请求体 EmployeeDTO：带上当前用户的完整字段，避免后端必填缺失
    const payload = {
      id: user.value.id,
      username: user.value.username,
      avatar: user.value.avatar,
      status: user.value.status,
      work: form.work,
      sex: form.sex,
      phone: form.phone,
      email: form.email
    }
    await updateEmployeeInfo(payload)
    // 同步到 store，页面展示立即更新
    employeeStore.setUser({ ...user.value, ...payload })
    ElMessage.success('个人信息已更新')
    editing.value = false
  } catch (e) {
    // 响应拦截器已提示
  } finally {
    saving.value = false
  }
}

const refresh = () => employeeStore.getUser()
onMounted(() => {
  // 进入页面若 store 为空（如刷新），主动拉一次
  if (!user.value.id) employeeStore.getUser()
})
</script>

<style lang="scss" scoped>
.profile-card {
  max-width: 800px;
  margin: 20px auto;

  .profile-form {
    max-width: 500px;
  }

  .actions {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
