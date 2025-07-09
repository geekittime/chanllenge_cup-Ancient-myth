<template>
    <div class="register-page">
        <AppHeader />
        <div class="register-container">
            <div class="register-form">
                <h2>注册账号</h2>
                <form @submit.prevent="handleRegister">
                    <div class="form-group">
                        <label for="username">用户名:</label>
                        <input type="text" id="username" v-model="registerForm.username" required placeholder="请输入用户名"
                            minlength="3" />
                    </div>
                    <div class="form-group">
                        <label for="password">密码:</label>
                        <input type="password" id="password" v-model="registerForm.password" required
                            placeholder="请输入密码" minlength="6" />
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">确认密码:</label>
                        <input type="password" id="confirmPassword" v-model="confirmPassword" required
                            placeholder="请再次输入密码" />
                    </div>
                    <div class="form-group">
                        <label for="email">邮箱:</label>
                        <input type="email" id="email" v-model="registerForm.email" placeholder="请输入邮箱（可选）" />
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn-register" :disabled="loading">
                            {{ loading ? '注册中...' : '注册' }}
                        </button>
                        <router-link to="/login" class="link-login">
                            已有账号？立即登录
                        </router-link>
                    </div>
                </form>
                <div v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                </div>
                <div v-if="successMessage" class="success-message">
                    {{ successMessage }}
                </div>
            </div>
        </div>
        <AppFooter />
    </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { register } from '@/api/auth'

export default {
    name: 'RegisterPage',
    components: {
        AppHeader,
        AppFooter
    },
    data() {
        return {
            registerForm: {
                username: '',
                password: '',
                email: ''
            },
            confirmPassword: '',
            loading: false,
            errorMessage: '',
            successMessage: ''
        }
    },
    methods: {
        async handleRegister() {
            // 表单验证
            if (this.registerForm.password !== this.confirmPassword) {
                this.errorMessage = '两次输入的密码不一致'
                return
            }

            if (this.registerForm.password.length < 6) {
                this.errorMessage = '密码长度不能少于6位'
                return
            }

            this.loading = true
            this.errorMessage = ''
            this.successMessage = ''

            try {
                const response = await register(this.registerForm)

                if (response.data.code == 200) {
                    this.successMessage = '注册成功！正在跳转到登录页面...'

                    // 延迟跳转到登录页面
                    setTimeout(() => {
                        this.$router.push('/login')
                    }, 2000)
                } else {
                    this.errorMessage = response.data.msg
                }
            } catch (error) {
                console.error('注册错误:', error)
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

.register-page {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}

.register-container {
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

.register-container::before {
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

.register-form {
    background: rgba(255, 255, 255, 0.95);
    /* 稍微透明的白色背景 */
    padding: 3rem;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    width: 100%;
    max-width: 450px;
    position: relative;
    z-index: 2;
    /* 确保表单在遮罩层之上 */
    backdrop-filter: blur(10px);
    /* 添加毛玻璃效果 */
}

.register-form h2 {
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

.btn-register {
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

.btn-register:hover:not(:disabled) {
    background: #5a3013;
    /* 深一点的主题色 */
}

.btn-register:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.link-login {
    color: #7c451b;
    /* 使用主题色 */
    text-decoration: none;
    font-size: 0.9rem;
}

.link-login:hover {
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

.success-message {
    margin-top: 1rem;
    padding: 10px;
    background: rgba(238, 255, 238, 0.9);
    color: #393;
    border: 1px solid #cfc;
    border-radius: 4px;
    text-align: center;
}
</style>