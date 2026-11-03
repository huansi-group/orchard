<script setup lang="ts">
import { Connection, DataAnalysis, Lock, TrendCharts } from '@element-plus/icons-vue'
</script>

<template>
  <div class="auth-page">
    <section class="auth-brand">
      <div class="brand-orbit brand-orbit--one" />
      <div class="brand-orbit brand-orbit--two" />
      <div class="auth-brand-inner">
        <div class="auth-logo">
          <span class="auth-logo-mark">
            <el-icon><DataAnalysis /></el-icon>
          </span>
          <span>DataAgent</span>
        </div>

        <div class="auth-hero">
          <p class="eyebrow">数据智能体工作台</p>
          <h1>把数据洞察，<br />变成可持续的智能体</h1>
          <p class="lede">
            统一接入 SQL、API 与文件数据源，管理智能体从创建、调试到运行的完整生命周期。
          </p>
          <ul class="auth-features">
            <li>
              <span class="feature-icon"><el-icon><Connection /></el-icon></span>
              <div>
                <strong>多源数据接入</strong>
                <small>SQL、API、文件统一编排</small>
              </div>
            </li>
            <li>
              <span class="feature-icon"><el-icon><Lock /></el-icon></span>
              <div>
                <strong>安全访问控制</strong>
                <small>Spring Security + JWT 无状态认证</small>
              </div>
            </li>
            <li>
              <span class="feature-icon"><el-icon><TrendCharts /></el-icon></span>
              <div>
                <strong>运营概览</strong>
                <small>实时掌握智能体运行状态</small>
              </div>
            </li>
          </ul>
        </div>

        <p class="auth-foot">DataAgent · 数据智能体平台</p>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-card">
        <slot />
      </div>
    </section>
  </div>
</template>

<style>
.auth-page {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
  min-height: 100vh;
  background: #fff;
}

.auth-brand {
  position: relative;
  display: flex;
  overflow: hidden;
  color: #e2e8f0;
  background:
    radial-gradient(110% 90% at 10% 0%, rgba(99, 102, 241, 0.5), transparent 58%),
    radial-gradient(90% 70% at 100% 100%, rgba(14, 165, 233, 0.32), transparent 55%),
    #0b1224;
}

.auth-brand::before {
  position: absolute;
  inset: 0;
  content: '';
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px);
  background-size: 44px 44px;
  mask-image: radial-gradient(80% 70% at 30% 30%, #000 20%, transparent 80%);
}

.brand-orbit {
  position: absolute;
  border: 1px solid rgba(129, 140, 248, 0.22);
  border-radius: 50%;
}

.brand-orbit--one {
  top: -180px;
  right: -140px;
  width: 520px;
  height: 520px;
}

.brand-orbit--two {
  right: 60px;
  bottom: -260px;
  width: 460px;
  height: 460px;
  border-color: rgba(56, 189, 248, 0.18);
}

.auth-brand-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 620px;
  padding: 44px 56px 36px;
  margin: 0 auto;
}

.auth-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.auth-logo-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  font-size: 22px;
  color: #fff;
  background: linear-gradient(135deg, #6366f1, #0ea5e9);
  border-radius: 13px;
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.45);
}

.auth-hero {
  margin: auto 0;
  padding: 48px 0;
}

.eyebrow {
  display: inline-flex;
  padding: 5px 12px;
  margin-bottom: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #c7d2fe;
  letter-spacing: 0.1em;
  background: rgba(99, 102, 241, 0.16);
  border: 1px solid rgba(129, 140, 248, 0.3);
  border-radius: 999px;
}

.auth-hero h1 {
  font-size: clamp(32px, 3.2vw, 46px);
  font-weight: 700;
  line-height: 1.22;
  letter-spacing: -0.02em;
  color: #f8fafc;
}

.lede {
  max-width: 480px;
  margin-top: 20px;
  font-size: 15px;
  line-height: 1.8;
  color: #94a3b8;
}

.auth-features {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 0;
  margin: 40px 0 0;
  list-style: none;
}

.auth-features li {
  display: flex;
  gap: 14px;
  align-items: center;
}

.feature-icon {
  display: grid;
  flex-shrink: 0;
  place-items: center;
  width: 38px;
  height: 38px;
  font-size: 17px;
  color: #c7d2fe;
  background: rgba(99, 102, 241, 0.16);
  border: 1px solid rgba(129, 140, 248, 0.24);
  border-radius: 11px;
}

.auth-features strong {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
}

.auth-features small {
  font-size: 12px;
  color: #7c8ba1;
}

.auth-foot {
  font-size: 12px;
  color: #64748b;
  letter-spacing: 0.04em;
}

.auth-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  background:
    radial-gradient(70% 50% at 100% 0%, #eef2ff, transparent 70%),
    #fff;
}

.auth-card {
  width: 100%;
  max-width: 404px;
}

.auth-heading {
  margin-bottom: 28px;
}

.auth-heading h2 {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.auth-heading p {
  margin-top: 8px;
  font-size: 14px;
  color: var(--da-muted);
}

.auth-card .el-form-item {
  margin-bottom: 20px;
}

.auth-card .el-form-item__label {
  padding-bottom: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #334155;
}

.auth-card .el-input__wrapper {
  padding: 4px 14px;
  background: #f8fafc;
  border-radius: 11px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
}

.auth-card .el-input__wrapper.is-focus {
  background: #fff;
  box-shadow: 0 0 0 1px var(--da-primary) inset;
}

.auth-submit {
  width: 100%;
  height: 46px;
  margin-top: 4px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 11px;
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.24);
}

.auth-switch {
  margin-top: 24px;
  font-size: 13px;
  color: var(--da-muted);
  text-align: center;
}

.auth-switch a {
  font-weight: 500;
  color: var(--da-primary);
}

.demo-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  margin-top: 24px;
  font-size: 12px;
  color: #475569;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 11px;
}

.demo-box .demo-label {
  color: var(--da-muted);
}

.demo-box code {
  padding: 2px 7px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', monospace;
  font-size: 12px;
  color: #4338ca;
  background: #eef2ff;
  border-radius: 5px;
}

.demo-box .el-button {
  margin-left: auto;
}

.form-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--da-muted);
}

@media (max-width: 960px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-brand {
    min-height: auto;
  }

  .auth-brand-inner {
    padding: 28px 32px 34px;
  }

  .auth-hero {
    padding: 36px 0 0;
  }

  .auth-hero h1 {
    font-size: 30px;
  }

  .auth-features,
  .auth-foot {
    display: none;
  }

  .auth-panel {
    align-items: flex-start;
    padding: 36px 24px 56px;
  }
}

@media (max-width: 560px) {
  .auth-brand-inner {
    padding: 22px 20px 28px;
  }

  .auth-hero {
    padding: 28px 0 0;
  }

  .auth-hero h1 {
    font-size: 25px;
  }

  .lede {
    font-size: 13px;
  }

  .auth-panel {
    padding: 28px 18px 48px;
  }
}
</style>
