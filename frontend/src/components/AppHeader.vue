<template>
    <header>
        <div class="container">
            <div id="branding"></div>
            <nav>
                <ul>
                    <li>
                        <router-link to="/">主页</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/characters' }">
                        <router-link to="/characters">人物关系</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/places' }">
                        <router-link to="/places">地点关系</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/events' }">
                        <router-link to="/events">事件关系</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/books' }">
                        <router-link to="/books">涉及典籍</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/AIchat' }">
                        <router-link to="/AIchat">AI对话</router-link>
                    </li>
                    <li :class="{ current: $route.path === '/advice' }">
                        <router-link to="/advice">联系我们</router-link>
                    </li>
                    <!-- 登录状态相关 -->
                    <li v-if="!isLoggedIn">
                        <router-link to="/login">登录</router-link>
                    </li>
                    <li v-if="!isLoggedIn">
                        <router-link to="/register">注册</router-link>
                    </li>
                    <li v-if="isLoggedIn" class="user-info">
                        <span class="username">{{ username }}</span>
                        <button @click="logout" class="logout-btn">退出</button>
                    </li>
                </ul>
            </nav>
        </div>
    </header>
</template>

<script>
export default {
    name: "AppHeader",
    data() {
        return {
            isLoggedIn: false,
            username: ''
        }
    },
    mounted() {
        this.checkLoginStatus()
    },
    methods: {
        checkLoginStatus() {
            const token = localStorage.getItem('token')
            const username = localStorage.getItem('username')

            if (token && username) {
                this.isLoggedIn = true
                this.username = username
            }
        },
        logout() {
            // 清除本地存储
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            localStorage.removeItem('role')

            // 更新状态
            this.isLoggedIn = false
            this.username = ''

            // 跳转到登录页
            this.$router.push('/login')
        }
    },
    watch: {
        '$route'() {
            // 路由变化时重新检查登录状态
            this.checkLoginStatus()
        }
    }
}
</script>

<style scoped>
.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.username {
    color: #fff;
    font-weight: 500;
}

.logout-btn {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
    padding: 5px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: background 0.3s;
}

.logout-btn:hover {
    background: rgba(255, 255, 255, 0.3);
}
</style>