import Vue from "vue";
import VueRouter from 'vue-router'
import IndexPage from "../components/Index.vue"
import CharactersPage from "../components/Characters.vue"
import PlacesPage from "../components/Places.vue"
import AdvicePage from "../components/Advice.vue"
import BooksPage from "../components/Books.vue"
import EventsPage  from "../components/Events.vue"
import CharacterPage from "@/components/CharacterPage.vue";
Vue.use(VueRouter);
//3.创建VueRouter的实例
const router = new VueRouter({
    //tips:不想要 #（锚点）就添加下面代码
    mode:'history', 
    //4.配置路由信息
    routes :[
        {
          path: "/",
          name:'home',
          component: IndexPage,
        },
        {
            path: "/AIchat",
            name:'aichat',
            component: () => import('../components/AIchat.vue')
        },
        {
          path: "/characters",
          name:'characters',
          component: CharactersPage
        },
        {
            path: "/places",
            name:'places',
            component: PlacesPage
        },
        {
            path: "/advice",
            name:'advice',
            component: AdvicePage
        },
        {
            path: "/books",
            name:'books',
            component: BooksPage
        },
        {
            path: "/events",
            name:'events',
            component: EventsPage
        },
        {
          path: "/onecharacter",
          name:'onecharacter',
          component: CharacterPage
        }
      ]
})
//5.导出路由实例
export default router