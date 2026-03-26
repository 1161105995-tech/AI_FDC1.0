import axios from 'axios'

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000
})

http.interceptors.response.use((response) => response, (error) => Promise.reject(error))

export async function apiRequest<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise
  if (!response.data.success) {
    throw new Error(response.data.message || 'Request failed')
  }
  return response.data.data
}

export default http
