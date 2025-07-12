import Vue from "vue";
import VueRouter from 'vue-router'
import IndexPage from "../components/Index.vue"
// import CharactersPage from "../components/Characters.vue"
// import PlacesPage from "../components/Places.vue"
import AdvicePage from "../components/Advice.vue"
import BooksPage from "../components/Books.vue"
// import EventsPage from "../components/Events.vue"
// import CharacterPage from "@/components/CharacterPage.vue";
import LoginPage from "@/components/LoginPage.vue";
import RegisterPage from "@/components/RegisterPage.vue";

Vue.use(VueRouter);

//3.创建VueRouter的实例
const router = new VueRouter({
    //tips:不想要 #（锚点）就添加下面代码
    mode: 'history',
    //4.配置路由信息
    routes: [
        {
            path: "/",
            name: 'home',
            component: IndexPage,
            meta: { requiresAuth: true }
        },
        {
            path: "/login",
            name: 'login',
            component: LoginPage,
            meta: { requiresAuth: false }
        },
        {
            path: "/register",
            name: 'register',
            component: RegisterPage,
            meta: { requiresAuth: false }
        },
        {
            path: "/AIchat",
            name: 'aichat',
            component: () => import('../components/AIchat.vue'),
            meta: { requiresAuth: true }
        },
        // {
        //     path: "/characters",
        //     name: 'characters',
        //     component: CharactersPage,
        //     meta: { requiresAuth: true }
        // },
        // {
        //     path: "/places",
        //     name: 'places',
        //     component: PlacesPage,
        //     meta: { requiresAuth: true }
        // },
        {
            path: "/advice",
            name: 'advice',
            component: AdvicePage,
            meta: { requiresAuth: true }
        },
        {
            path: "/books",
            name: 'books',
            component: BooksPage,
            meta: { requiresAuth: true }
        },
        // {
        //     path: "/events",
        //     name: 'events',
        //     component: EventsPage,
        //     meta: { requiresAuth: true }
        // },
        // {
        //     path: "/onecharacter",
        //     name: 'onecharacter',
        //     component: CharacterPage,
        //     meta: { requiresAuth: true }
        // },
        {
            path: "/all",
            name: 'all',
            component: () => import('../components/all.vue'),
            meta: { requiresAuth: true }
        }
    ]
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 如果路由需要认证但用户未登录
    if (to.meta.requiresAuth && !token) {
        next({
            path: '/login',
            query: { redirect: to.fullPath } // 保存用户想要访问的页面
        })
    }
    // 如果用户已登录但想访问登录/注册页面，重定向到首页
    else if ((to.path === '/login' || to.path === '/register') && token) {
        next('/')
    }
    // 其他情况正常跳转
    else {
        next()
    }
})

//5.导出路由实例
export default router