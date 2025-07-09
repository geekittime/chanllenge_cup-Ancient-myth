import axios from 'axios'

// 创建axios实例
const api = axios.create({
    baseURL: 'http://localhost:6060', // 根据您的后端地址调整
    timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
api.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.response?.status === 401) {
            // 清除本地存储的认证信息
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            localStorage.removeItem('role')
            // 跳转到登录页面
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

// 登录接口
export const login = (loginData) => {
    return api.post('/api/accounts/login', {
        username: loginData.username,
        password: loginData.password
    })
}

// 注册接口
export const register = (registerData) => {
    return api.post('/api/accounts', {
        username: registerData.username,
        password: registerData.password,
        email: registerData.email
    })
}

// 获取当前用户信息
export const getCurrentUser = (username) => {
    return api.get(`/api/accounts/${username}`)
}

// 获取当前用户ID
export const getCurrentUserId = () => {
    return api.get('/api/accounts/userId')
}

// 更新用户信息
export const updateUser = (userData) => {
    return api.put('/api/accounts', userData)
}

export default api