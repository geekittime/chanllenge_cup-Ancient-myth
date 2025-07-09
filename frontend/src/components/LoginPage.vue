<template>
    <div class="login-page">
        <AppHeader />
        <div class="login-container">
            <div class="login-form">
                <h2>登录</h2>
                <form @submit.prevent="handleLogin">
                    <div class="form-group">
                        <label for="username">用户名:</label>
                        <input type="text" id="username" v-model="loginForm.username" required placeholder="请输入用户名" />
                    </div>
                    <div class="form-group">
                        <label for="password">密码:</label>
                        <input type="password" id="password" v-model="loginForm.password" required
                            placeholder="请输入密码" />
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn-login" :disabled="loading">
                            {{ loading ? '登录中...' : '登录' }}
                        </button>
                        <router-link to="/register" class="link-register">
                            还没有账号？立即注册
                        </router-link>
                    </div>
                </form>
                <div v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                </div>
            </div>
        </div>
        <AppFooter />
    </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { login } from '@/api/auth.js'

export default {
    name: 'LoginPage',
    components: {
        AppHeader,
        AppFooter
    },
    data() {
        return {
            loginForm: {
                username: '',
                password: ''
            },
            loading: false,
            errorMessage: ''
        }
    },
    methods: {
        async handleLogin() {
            this.loading = true
            this.errorMessage = ''

            try {
                const response = await login(this.loginForm)

                if (response.data.code == 200) {
                    const { token, username, role } = response.data.data

                    // 存储登录信息
                    localStorage.setItem('token', token)
                    localStorage.setItem('username', username)
                    localStorage.setItem('role', role)

                    // 跳转到主页或之前的页面
                    const redirect = this.$route.query.redirect || '/'
                    this.$router.push(redirect)

                    this.$message.success('登录成功')
                } else {
                    this.errorMessage = response.data.msg || '登录失败'
                }
            } catch (error) {
                console.error('登录错误:', error)
                this.errorMessage = '网络错误，请稍后重试'
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped>
@import '../assets/styles.css';

.login-page {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}

.login-container {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem;

    /* 使用与其他页面相同的背景图片样式 */
    background-image: url('../../images/mainbg.jpg');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
    background-color: transparent;

    /* 添加半透明遮罩层以确保表单可读性 */
    position: relative;
}

.login-container::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    /* 半透明黑色遮罩 */
    z-index: 1;
}

.login-form {
    background: rgba(255, 255, 255, 0.95);
    /* 稍微透明的白色背景 */
    padding: 3rem;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    width: 100%;
    max-width: 400px;
    position: relative;
    z-index: 2;
    /* 确保表单在遮罩层之上 */
    backdrop-filter: blur(10px);
    /* 添加毛玻璃效果 */
}

.login-form h2 {
    text-align: center;
    margin-bottom: 2rem;
    color: #333;
    font-size: 2rem;
    font-family: "STXinwei", "华文新魏", sans-serif;
    /* 使用中国风字体 */
}

.form-group {
    margin-bottom: 1.5rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: #555;
    font-weight: 500;
}

.form-group input {
    width: 100%;
    padding: 12px;
    border: 2px solid #ddd;
    border-radius: 6px;
    font-size: 1rem;
    transition: border-color 0.3s;
    box-sizing: border-box;
}

.form-group input:focus {
    outline: none;
    border-color: #7c451b;
    /* 使用主题色 */
}

.form-actions {
    margin-top: 2rem;
    text-align: center;
}

.btn-login {
    width: 100%;
    padding: 12px;
    background: #7c451b;
    /* 使用主题色 */
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-bottom: 1rem;
}

.btn-login:hover:not(:disabled) {
    background: #5a3013;
    /* 深一点的主题色 */
}

.btn-login:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.link-register {
    color: #7c451b;
    /* 使用主题色 */
    text-decoration: none;
    font-size: 0.9rem;
}

.link-register:hover {
    text-decoration: underline;
    color: #e8491d;
    /* 使用强调色 */
}

.error-message {
    margin-top: 1rem;
    padding: 10px;
    background: rgba(255, 238, 238, 0.9);
    color: #c33;
    border: 1px solid #fcc;
    border-radius: 4px;
    text-align: center;
}
</style>