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
                        <img class="book-image" :src="book.image" :alt="book.title + ' Cover'">
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
import chuci_pic from "../../images/Chuci.png"
import shiji_pic from "../../images/Shiji.png"
import chunqiu_pic from "../../images/Chunqiu.png"
import guoyu_pic from "../../images/Guoyu.png"
import hanfeizi_pic from "../../images/Hanfeizi.png"
import zhuangzi_pic from "../../images/Zhuangzi.png"
import xunzi_pic from "../../images/Xunzi.png"
import mozi_pic from "../../images/Mozi.png"
import guanzi_pic from "../../images/Guanzi.png"
import lvshichunqiu_pic from "../../images/Lvshichunqiu.png"
import mutianzi_pic from "../../images/Mutianzi.png"
import liexian_pic from "../../images/Liexian.png"
import zhushu_pic from "../../images/Zhushu.png"
import shijing_pic from "../../images/Shijing.png"
import shangshu_pic from "../../images/Shangshu.png"
import liji_pic from "../../images/Liji.png"
import lienuchuan_pic from "../../images/Lienuchuan.png"
import shuoyuan_pic from "../../images/Shuoyuan.png"
import xinshu_pic from "../../images/Xinshu.png"
import xinxu_pic from "../../images/Xinxu.png"
import chunqiufanlu_pic from "../../images/Chunqiufanlu.png"
import heguanzi_pic from "../../images/Heguanzi.png"
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
                    description: '关于《山海经》一书的性质，众说纷纭。从《汉书》到《新唐书》的记载，《山海经》一直被视为具有实用价值的地理书。汉代刘歆在其《上<山海经>表》中他指出："《山海经》内别五方之山，外分八方之海，纪其珍宝奇物，异方之所生，水土草木禽兽昆虫麟凤之所止，祯祥之所隐，及四海之外，绝域之国，殊类之人。禹别九州，任土作贡；而益等类物善恶，著《山海经》"；后世的《隋书·经籍志》以及不少史书，也把它列入地理类。',
                    image: shanhaijing_pic,
                    blurred: false
                },
                {
                    title: '淮南子',
                    author: '刘安及其门客',
                    description: '《淮南子》著录共分为内二十一篇、中八篇、外三十三篇，内篇论道，中篇养生，外篇杂说。以道家思想为主，糅合了儒法阴阳等家，一般列《淮南子》为杂家。实际上，该书是以道家思想为指导，吸收诸子百家学说，融会贯通而成，是战国至汉初黄老之学理论体系的代表作。《淮南子》在阐明哲理时，旁涉奇物异类、鬼神灵怪，保存了一部分神话材料，像"女娲补天""后羿射日""共工怒触不周山""嫦娥奔月""塞翁失马"等古代神话，主要靠本书得以流传。',
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
                },
                // 新增的古代典籍
                {
                    title: '楚辞',
                    author: '屈原等',
                    description: '中国文学史上第一部浪漫主义诗歌总集，以屈原作品为主，充满神话色彩和浪漫主义精神，对后世文学影响深远。',
                    image: chuci_pic,
                    blurred: false
                },
                {
                    title: '史记',
                    author: '司马迁',
                    description: '中国历史上第一部纪传体通史，记录了从黄帝到汉武帝时期的历史，包含大量神话传说和历史人物故事。',
                    image: shiji_pic,
                    blurred: false
                },
                {
                    title: '春秋左传',
                    author: '左丘明',
                    description: '《春秋》的重要注解，详细记录春秋时期的历史事件，是研究先秦历史的重要文献。',
                    image: chunqiu_pic,
                    blurred: false
                },
                {
                    title: '国语',
                    author: '左丘明',
                    description: '中国最早的国别史，记录了周王室和各诸侯国的历史，保存了许多珍贵的历史资料。',
                    image: guoyu_pic,
                    blurred: false
                },
                {
                    title: '韩非子',
                    author: '韩非',
                    description: '法家学派的重要著作，集法家思想之大成，对后世政治思想产生重大影响。',
                    image: hanfeizi_pic,
                    blurred: false
                },
                {
                    title: '庄子',
                    author: '庄周',
                    description: '道家经典著作，以寓言故事阐述哲理，文笔优美，想象奇特，是中国古代哲学和文学的瑰宝。',
                    image: zhuangzi_pic,
                    blurred: false
                },
                {
                    title: '荀子',
                    author: '荀况',
                    description: '先秦儒家重要著作，提出性恶论，强调教育和礼制的重要性，是儒家思想的重要组成部分。',
                    image: xunzi_pic,
                    blurred: false
                },
                {
                    title: '墨子',
                    author: '墨翟',
                    description: '墨家学派创始人的著作，提倡兼爱、非攻、节用等思想，是先秦诸子百家中的重要学派。',
                    image: mozi_pic,
                    blurred: false
                },
                {
                    title: '管子',
                    author: '管仲等',
                    description: '先秦政治经济学著作，涉及政治、经济、军事、哲学等多个领域，体现了丰富的治国理念。',
                    image: guanzi_pic,
                    blurred: false
                },
                {
                    title: '吕氏春秋',
                    author: '吕不韦等',
                    description: '战国末期的杂家代表作，集百家之长，内容丰富，涵盖哲学、政治、军事、农业等各个方面。',
                    image: lvshichunqiu_pic,
                    blurred: false
                },
                {
                    title: '穆天子传',
                    author: '不详',
                    description: '记录周穆王西征的神话传说，充满浪漫色彩，是研究古代神话和历史的重要资料。',
                    image: mutianzi_pic,
                    blurred: false
                },
                {
                    title: '列仙传',
                    author: '刘向',
                    description: '记录古代仙人传说的著作，保存了许多神话传说和民间故事，是道教文化的重要文献。',
                    image: liexian_pic,
                    blurred: false
                },
                {
                    title: '竹书纪年',
                    author: '不详',
                    description: '战国时期魏国史书，以编年体记述历史，是研究先秦历史的重要史料。',
                    image: zhushu_pic,
                    blurred: false
                },
                {
                    title: '诗经',
                    author: '不详',
                    description: '中国最早的诗歌总集，收录了西周到春秋时期的诗歌，反映了古代社会生活的方方面面。',
                    image: shijing_pic,
                    blurred: false
                },
                {
                    title: '尚书',
                    author: '不详',
                    description: '中国最古老的政治文献汇编，记录了尧舜禹到春秋时期的重要政治文献和历史资料。',
                    image: shangshu_pic,
                    blurred: false
                },
                {
                    title: '礼记',
                    author: '戴圣',
                    description: '儒家经典之一，记录了先秦到汉代的礼仪制度和政治思想，是研究古代文化的重要文献。',
                    image: liji_pic,
                    blurred: false
                },
                {
                    title: '列女传',
                    author: '刘向',
                    description: '记录古代女性事迹的著作，展现了不同类型女性的品德和才能，反映了古代的妇女观念。',
                    image: lienuchuan_pic,
                    blurred: false
                },
                {
                    title: '说苑',
                    author: '刘向',
                    description: '汉代刘向编纂的历史故事集，通过历史故事阐述政治和道德理念。',
                    image: shuoyuan_pic,
                    blurred: false
                },
                {
                    title: '新书',
                    author: '贾谊',
                    description: '西汉政论家贾谊的政治著作，提出了许多政治改革的主张和建议。',
                    image: xinshu_pic,
                    blurred: false
                },
                {
                    title: '新序',
                    author: '刘向',
                    description: '刘向编纂的历史故事集，通过历史事例说明政治和道德问题。',
                    image: xinxu_pic,
                    blurred: false
                },
                {
                    title: '春秋繁露',
                    author: '董仲舒',
                    description: '汉代儒学家董仲舒的重要著作，阐述了天人感应理论，对后世儒学发展影响深远。',
                    image: chunqiufanlu_pic,
                    blurred: false
                },
                {
                    title: '鶡冠子',
                    author: '鶡冠子',
                    description: '战国时期道家著作，内容涉及哲学、政治、军事等方面，体现了独特的思想观点。',
                    image: heguanzi_pic,
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