<template>
    <div class="Books">

        <head>
            <meta charset="UTF-8">
            <title>中国神话人物介绍</title>
        </head>

        <body>
            <AppHeader />

            <div class="main-col">
                <div class="book-container">
                    <div v-for="(book, index) in books" :key="index" class="book-item"
                        :class="{ 'blurred': book.blurred }" @mouseover="handleMouseOver(book)"
                        @mouseout="handleMouseOut">
                        <img class="book-image" :src=book.image alt="Book 1 Cover">
                        <p class="book-title">{{ book.title }}</p>
                        <p class="book-author">{{ book.author }}</p>
                        <p class="book-description">{{ book.description }}</p>
                    </div>
                </div>
            </div>

            <AppFooter />
        </body>
    </div>
</template>

<script>

import shanhaijing_pic from "../../images/Shanhaijing.jpg"
import huainanzig_pic from "../../images/huainanzi.jpg"
import liezi_pic from "../../images/Liezi.jpg"
import chinesemisstory_pic from "../../images/ChineseMisStory.jpg"
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

export default {
    name: "BooksPage",
    components: { AppHeader, AppFooter },
    data() {
        return {
            books: [
                {
                    title: '山海经',
                    author: '待考察',
                    description: '关于《山海经》一书的性质，众说纷纭。从《汉书》到《新唐书》的记载，《山海经》一直被视为具有实用价值的地理书。汉代刘歆在其《上<山海经>表》中他指出：“《山海经》内别五方之山，外分八方之海，纪其珍宝奇物，异方之所生，水土草木禽兽昆虫麟凤之所止，祯祥之所隐，及四海之外，绝域之国，殊类之人。禹别九州，任土作贡；而益等类物善恶，著《山海经》”；后世的《隋书·经籍志》以及不少史书，也把它列入地理类。',
                    image: shanhaijing_pic,
                    blurred: false
                },
                {
                    title: '淮南子',
                    author: '刘安及其门客',
                    description: '《淮南子》著录共分为内二十一篇、中八篇、外三十三篇，内篇论道，中篇养生，外篇杂说。以道家思想为主，糅合了儒法阴阳等家，一般列《淮南子》为杂家。实际上，该书是以道家思想为指导，吸收诸子百家学说，融会贯通而成，是战国至汉初黄老之学理论体系的代表作。《淮南子》在阐明哲理时，旁涉奇物异类、鬼神灵怪，保存了一部分神话材料，像“女娲补天”“后羿射日”“共工怒触不周山”“嫦娥奔月”“塞翁失马”等古代神话，主要靠本书得以流传。',
                    image: huainanzig_pic,
                    blurred: false
                },
                {
                    title: '列子',
                    author: '列子',
                    description: '列子是介于老子与庄子之间道家学派承前启后的重要人物，是老子和庄子之外的又一位道家学派代表人物。其学本于黄帝老子，主张清静无为，归同于老庄，被道家尊为前辈 [1]。创立了先秦哲学学派贵虚学派（列子学），对后世哲学、美学、文学、科技、养生、乐曲、宗教影响非常深远。有《列子》八卷，全书共载民间故事、寓言、神话传说等134则，题材广泛，有些颇富教育意义。',
                    image: liezi_pic,
                    blurred: false
                },
                {
                    title: '中国神话故事',
                    author: '袁珂',
                    description: '本书是中国第一部神话史。中国古代神话，一向以零散不成体系著称，中国神话故事体现了古人\n在对宇宙如何形成及进化发展的探索精神，象征着古代劳动人民与自然抗争的智慧、可觉察出其中既有天真的想像又有朴素科学\n的成分。从神话的内容，我们可据以了解先民的生活背景和人文反映，这就是神话的功用之一。',
                    image: chinesemisstory_pic,
                    blurred: false
                }
            ],
            activeIndex: null // 当前激活的book-item的索引
        };
    },
    methods: {
        handleMouseOver(book) {
            // 移除当前悬停元素的blurred类
            this.$set(book, 'blurred', false);
            // 为其他元素添加blurred类
            this.books.forEach((otherBook) => {
                if (otherBook !== book) {
                    this.$set(otherBook, 'blurred', true);
                }
            });
        },
        handleMouseOut() {
            // 移除所有元素的blurred类
            this.books.forEach((book) => {
                this.$set(book, 'blurred', false);
            });
        }
    }
};
</script>

<style scoped>
@import '../assets/styles.css';

.book-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
}

.book-container.blurred {
    filter: blur(5px);
    transition: filter 0.3s ease;
}

.book-item.blurred {
    filter: blur(5px);
    transition: filter 0.3s ease;
}

.book-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    width: 20%;
    margin: 10px;
    border: 1px solid #ccc;
    padding: 10px;
    box-sizing: border-box;
}

.book-item:hover {
    transform: scale(1.1);
    z-index: 1;
    transition: transform 0.3s ease;
}

.book-image {
    width: 100px;
    height: 150px;
    object-fit: cover;
}

.book-title,
.book-author,
.book-description {
    color: rgba(86, 43, 0, 0.942);
    font-weight: bold;
    margin-bottom: 10px;
}

.book-description {
    flex-grow: 1;
}
</style>
