import axios from 'axios';

// 创建一个axios实例，可以配置基础URL和其他设置
const apiClient = axios.create({
    baseURL: '/api', // 代理或后端的根路径
    headers: {
        'Content-Type': 'application/json',
    },
});

// 登录请求
export const login = (username, password) => {
    return apiClient.post('/accounts/login', {
        username,
        password,
    });
};

// 注册请求
export const register = (userData) => {
    // userData应该是一个包含用户名、密码等信息的对象
    // 例如 { username: 'user', password: 'pw' }
    return apiClient.post('/accounts', userData);
};

// 如果需要，可以添加一个拦截器来附加token
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default {
    login,
    register,
};