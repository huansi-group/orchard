<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Compass } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<template>
  <div class="not-found">
    <div class="not-found-card">
      <span class="not-found-icon">
        <el-icon><Compass /></el-icon>
      </span>
      <p class="code">404</p>
      <h1>页面走丢了</h1>
      <p class="desc">你访问的页面不存在，或者链接已经失效。</p>
      <el-button type="primary" @click="router.push('/dashboard')">返回工作台</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: grid;
  min-height: 100vh;
  padding: 24px;
  place-items: center;
  background:
    radial-gradient(60% 50% at 50% 0%, #eef2ff, transparent 70%),
    var(--da-bg);
}

.not-found-card {
  width: 100%;
  max-width: 420px;
  padding: 44px 36px;
  text-align: center;
  background: #fff;
  border: 1px solid var(--da-border);
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);
}

.not-found-icon {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  font-size: 26px;
  color: #4338ca;
  background: #eef2ff;
  border-radius: 16px;
}

.code {
  font-size: 12px;
  font-weight: 600;
  color: var(--da-primary);
  letter-spacing: 0.2em;
}

h1 {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.desc {
  margin: 10px 0 24px;
  font-size: 13px;
  color: var(--da-muted);
}
</style>
