<template>
    <div class="CharacterPage">

        <AppHeader />

        <div class="main-col">
            <CharacterChat ref="characterchat"></CharacterChat>
        </div>


        <AppFooter />

    </div>
</template>

<script>
import CharacterChat from './CharacterChat.vue';
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";

export default {
    name: "CharacterPage",
    mounted() {
        this.init();
        document.title = "中国神话人物介绍";

    },
    components: {
        CharacterChat,
        AppHeader,
        AppFooter,
    },
    data() {
        return {
            input: '',
        };
    },

    methods: {
        init() {
            // 根据路由的参数查询子组件ref="characterchat"的部分 即CharacterChat
            this.input = this.$route.query.c_name;
            this.$refs.characterchat.updateMsg(this.input)
        },
    },
};
</script>

<style scoped>
@import '../assets/styles.css';

.cards {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 20px;
    padding: 20px;
}

.character-card {
    width: calc(20% - 20px);
    margin-bottom: 20px;
    box-sizing: border-box;
    border: 1px solid #ddd;
    border-radius: 4px;
    background-color: #efdeb0;
    text-align: center;
    margin: 10px;
    font-family: 'KaiTi', '楷体', sans-serif;
}

.character-image {
    width: 100%;
    height: auto;
    border-radius: 50%;
}

.banner,
.main-col {
    background-image: url('../../images/mainbg.jpg');
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
    background-color: transparent;
}
</style>
