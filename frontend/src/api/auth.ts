import { http } from './http'
import type {
  ApiEnvelope,
  AuthResponse,
  LoginPayload,
  RegisterPayload,
  UserProfile,
} from '@/types'

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const response = await http.post<ApiEnvelope<AuthResponse>>('/auth/login', payload)
  return response.data.data
}

export async function register(payload: RegisterPayload): Promise<AuthResponse> {
  const response = await http.post<ApiEnvelope<AuthResponse>>('/auth/register', payload)
  return response.data.data
}

export async function fetchCurrentUser(): Promise<UserProfile> {
  const response = await http.get<ApiEnvelope<UserProfile>>('/auth/me')
  return response.data.data
}
