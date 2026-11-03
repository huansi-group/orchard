import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'
import type { AuthResponse, LoginPayload, RegisterPayload, UserProfile } from '@/types'

const TOKEN_KEY = 'dataagent.token'
const USER_KEY = 'dataagent.user'

function readStoredUser(): UserProfile | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as UserProfile
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) ?? '')
  const user = ref<UserProfile | null>(readStoredUser())

  const isAuthenticated = computed(() => Boolean(token.value))
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const displayName = computed(() => user.value?.displayName || user.value?.username || '访客')

  function persist(auth: AuthResponse) {
    token.value = auth.token
    user.value = auth.user
    localStorage.setItem(TOKEN_KEY, auth.token)
    localStorage.setItem(USER_KEY, JSON.stringify(auth.user))
  }

  async function login(payload: LoginPayload) {
    persist(await authApi.login(payload))
  }

  async function register(payload: RegisterPayload) {
    persist(await authApi.register(payload))
  }

  async function refreshProfile() {
    const profile = await authApi.fetchCurrentUser()
    user.value = profile
    localStorage.setItem(USER_KEY, JSON.stringify(profile))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    displayName,
    login,
    register,
    refreshProfile,
    logout,
  }
})
