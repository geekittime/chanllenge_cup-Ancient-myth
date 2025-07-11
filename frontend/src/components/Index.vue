<template>
    <div class="Index">

        <AppHeader />

        <!-- 【修改】将 banner 和 main-col 合并到一个 main 元素中，以实现更好的背景和布局控制 -->
        <main class="main-content">
            <div class="container">
                <div class="banner-text">
                    <h1>探索上古神话的奥秘</h1>
                </div>
                <div class="icons">
                    <router-link to="/all" class="circular-button button1">知识图谱</router-link>
                    <router-link to="/books" class="circular-button button2">涉及典籍</router-link>
                    <router-link to="/AIchat" class="circular-button button3">AI对话</router-link>
                    <router-link to="/advice" class="circular-button button4">联系我们</router-link>
                </div>
            </div>
        </main>

        <AppFooter />

    </div>
</template>

<script>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";

export default {
    name: "IndexPage",
    components: {
        AppHeader,
        AppFooter
    },
    data() {
        return {};
    },
    mounted() {
        document.title = "中国神话知识图谱";
    },
    methods: {
        goToCharactersPage() {
            this.$router.push({ name: 'characters' });
        },
    }
};
</script>

<style scoped>
@import '../assets/styles.css';

/* 【核心修改】 Flexbox 布局，解决底部空白问题 */
.Index {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    /* 确保根容器至少和视口一样高 */
}

.main-content {
    flex: 1;
    /* 让主内容区域自动伸展，填满剩余空间，将页脚推到底部 */
    display: flex;
    flex-direction: column;
    justify-content: center;
    /* 垂直居中内容 */
    align-items: center;
    /* 水平居中内容 */
    background-image: url('../../../images/mainbg.jpg');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
    padding: 40px 0;
    /* 增加一些内边距 */
}

.container {
    max-width: 1170px;
    margin: auto;
    overflow: hidden;
    padding: 0 20px;
    text-align: center;
    /* 让容器内的所有内容都居中 */
}

.banner-text h1 {
    font-size: 48px;
    /* 可以根据需要调整字体大小 */
    color: white;
    text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
    margin-bottom: 40px;
}

.icons .circular-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 200px;
    height: 200px;
    margin: 20px;
    /* 调整间距 */
    border-radius: 50%;
    text-decoration: none;
    border: none;
    cursor: pointer;

    text-align: center;
    font-family: 'KaiTi', '楷体', sans-serif;
    font-size: 30px;
    color: rgb(255, 255, 255);
    font-weight: bold;
    transition: all 0.3s ease;
    box-shadow: 0 0 10px 5px rgba(0, 0, 0, 0.5);

    background-size: cover;
    background-position: center;
}

.icons .circular-button:hover {
    transform: scale(1.1);
    box-shadow: 0 0 15px 7px rgba(255, 255, 255, 0.6);
    /* 悬浮时阴影更明显 */
}

.button1 {
    background-image: url('../../images/button1-bg.png');
}

.button2 {
    background-image: url('../../images/button2-bg.png');
}

.button3 {
    background-image: url('../../images/button3-bg.png');
}

.button4 {
    background-image: url('../../../images/button4-bg.png');
}

p {
    font-family: "STKaiti", "华文行楷", serif;
    font-size: 20px;
}
</style>