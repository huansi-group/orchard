import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('dataagent.token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      const url = error.config?.url ?? ''
      const isAuthRequest = url.includes('/auth/login') || url.includes('/auth/register')
      if (!isAuthRequest) {
        localStorage.removeItem('dataagent.token')
        localStorage.removeItem('dataagent.user')
        if (!window.location.pathname.startsWith('/login')) {
          window.location.assign('/login?expired=1')
        }
      }
    }
    return Promise.reject(error)
  },
)

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const payload = error.response?.data as { message?: string } | undefined
    if (payload?.message) {
      return payload.message
    }
    if (error.code === 'ECONNABORTED') {
      return '请求超时，请稍后重试'
    }
    if (!error.response) {
      return '无法连接到服务，请确认后端已启动'
    }
  }
  return error instanceof Error ? error.message : '请求失败，请稍后重试'
}
