<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Cpu,
  DataAnalysis,
  Menu,
  Odometer,
  SwitchButton,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const sidebarOpen = ref(false)

const pageTitle = computed(() => String(route.meta.title ?? '工作台'))
const initial = computed(() => auth.displayName.slice(0, 1).toUpperCase())
const roleLabel = computed(() => (auth.isAdmin ? '管理员' : '成员'))

watch(
  () => route.fullPath,
  () => {
    sidebarOpen.value = false
  },
)

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出当前账号吗？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  auth.logout()
  await router.replace({ name: 'login' })
}

function handleCommand(command: string) {
  if (command === 'logout') {
    void handleLogout()
  }
}
</script>

<template>
  <div class="shell">
    <div v-if="sidebarOpen" class="sidebar-mask" @click="sidebarOpen = false" />
    <aside class="sidebar" :class="{ 'is-open': sidebarOpen }">
      <router-link to="/dashboard" class="brand">
        <span class="brand-mark">
          <el-icon><DataAnalysis /></el-icon>
        </span>
        <span class="brand-text">
          DataAgent
          <small>数据智能体工作台</small>
        </span>
      </router-link>

      <p class="nav-caption">工作台</p>
      <nav class="nav">
        <router-link to="/dashboard" class="nav-link">
          <el-icon><Odometer /></el-icon>
          <span>概览</span>
        </router-link>
        <router-link to="/agents" class="nav-link">
          <el-icon><Cpu /></el-icon>
          <span>智能体</span>
        </router-link>
      </nav>

      <div class="sidebar-spacer" />

      <div class="sidebar-tip">
        <span class="tip-dot" />
        <div>
          <strong>JWT 会话保护</strong>
          <small>令牌有效期 24 小时</small>
        </div>
      </div>

      <div class="account">
        <el-avatar :size="38" class="account-avatar">{{ initial }}</el-avatar>
        <div class="account-meta">
          <strong>{{ auth.displayName }}</strong>
          <span>{{ roleLabel }}</span>
        </div>
        <el-tooltip content="退出登录" placement="top">
          <el-button text circle class="logout-button" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </aside>

    <div class="main">
      <header class="topbar">
        <button type="button" class="menu-button" @click="sidebarOpen = true">
          <el-icon><Menu /></el-icon>
        </button>
        <div class="topbar-title">
          <span class="breadcrumb">DataAgent / {{ pageTitle }}</span>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="topbar-actions">
          <span class="health"><i />服务正常</span>
          <el-dropdown trigger="click" @command="handleCommand">
            <button type="button" class="profile-button">
              <el-avatar :size="32">{{ initial }}</el-avatar>
              <span>{{ auth.displayName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="page">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.shell {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 30;
  display: flex;
  flex-direction: column;
  width: 252px;
  padding: 24px 18px 20px;
  color: #e2e8f0;
  background:
    radial-gradient(120% 80% at 0% 0%, rgba(99, 102, 241, 0.32), transparent 55%),
    radial-gradient(90% 60% at 100% 100%, rgba(14, 165, 233, 0.18), transparent 60%),
    #0f172a;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 8px 24px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  font-size: 21px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1, #0ea5e9);
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.45);
}

.brand-text {
  display: flex;
  flex-direction: column;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.brand-text small {
  margin-top: 1px;
  font-size: 11px;
  font-weight: 400;
  color: #94a3b8;
  letter-spacing: 0.08em;
}

.nav-caption {
  padding: 0 10px;
  margin-bottom: 8px;
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 12px;
  font-size: 14px;
  color: #cbd5e1;
  border-radius: 10px;
  transition: all 0.18s ease;
}

.nav-link:hover {
  color: #fff;
  background: rgba(148, 163, 184, 0.12);
}

.nav-link.router-link-active {
  color: #fff;
  background: rgba(99, 102, 241, 0.26);
  box-shadow: inset 0 0 0 1px rgba(129, 140, 248, 0.35);
}

.nav-link.router-link-active::before {
  position: absolute;
  top: 50%;
  left: -18px;
  width: 3px;
  height: 18px;
  content: '';
  background: #818cf8;
  border-radius: 0 3px 3px 0;
  transform: translateY(-50%);
}

.nav-link .el-icon {
  font-size: 18px;
}

.sidebar-spacer {
  flex: 1;
}

.sidebar-tip {
  display: flex;
  gap: 10px;
  padding: 12px;
  margin-bottom: 14px;
  font-size: 12px;
  color: #94a3b8;
  background: rgba(148, 163, 184, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 12px;
}

.sidebar-tip strong {
  display: block;
  font-size: 12px;
  color: #e2e8f0;
}

.sidebar-tip small {
  font-size: 11px;
}

.tip-dot {
  width: 7px;
  height: 7px;
  margin-top: 7px;
  background: #34d399;
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(52, 211, 153, 0.14);
}

.account {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 10px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 14px;
}

.account-avatar {
  flex-shrink: 0;
  font-weight: 600;
  color: #eef2ff;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.account-meta {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
  line-height: 1.3;
}

.account-meta strong {
  overflow: hidden;
  font-size: 13px;
  color: #f1f5f9;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-meta span {
  font-size: 11px;
  color: #94a3b8;
}

.logout-button {
  color: #94a3b8;
}

.logout-button:hover {
  color: #f8fafc;
  background: rgba(148, 163, 184, 0.14);
}

.main {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
  margin-left: 252px;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 18px;
  height: 76px;
  padding: 0 36px;
  background: rgba(246, 247, 251, 0.86);
  border-bottom: 1px solid var(--da-border);
  backdrop-filter: blur(12px);
}

.menu-button {
  display: none;
  place-items: center;
  width: 38px;
  height: 38px;
  color: var(--da-ink);
  cursor: pointer;
  background: #fff;
  border: 1px solid var(--da-border);
  border-radius: 10px;
}

.topbar-title {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}

.breadcrumb {
  font-size: 11px;
  color: var(--da-muted);
  letter-spacing: 0.04em;
}

.topbar-title h1 {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
}

.health {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 5px 11px;
  font-size: 12px;
  color: #047857;
  background: #ecfdf5;
  border: 1px solid #a7f3d0;
  border-radius: 999px;
}

.health i {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
}

.profile-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 8px 5px 5px;
  font-size: 13px;
  font-weight: 500;
  color: var(--da-ink);
  cursor: pointer;
  background: #fff;
  border: 1px solid var(--da-border);
  border-radius: 999px;
}

.profile-button .el-avatar {
  font-size: 13px;
  font-weight: 600;
  color: #eef2ff;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.profile-button .el-icon {
  font-size: 12px;
  color: var(--da-muted);
}

.sidebar-mask {
  display: none;
}

@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.24s ease;
  }

  .sidebar.is-open {
    transform: translateX(0);
  }

  .sidebar-mask {
    position: fixed;
    inset: 0;
    z-index: 25;
    display: block;
    background: rgba(15, 23, 42, 0.45);
    backdrop-filter: blur(2px);
  }

  .main {
    margin-left: 0;
  }

  .menu-button {
    display: grid;
  }

  .topbar {
    padding: 0 20px;
  }
}

@media (max-width: 640px) {
  .health {
    display: none;
  }

  .profile-button span {
    display: none;
  }

  .topbar-title h1 {
    font-size: 16px;
  }
}
</style>
