<template>
  <div class="Characters">
    <head>
      <meta charset="UTF-8">
      <title>中国神话人物介绍</title>
    </head>
    <body>
    <AppHeader/>
    <div class="main-col">
      <div class="cards">
        <div class="character-card" @click="gotocharacter('舜')">
          <img src="../../images/shun.jpg" alt="人物1" class="character-image">
          <h2>舜</h2>
        </div>
        <div class="character-card" @click="gotocharacter('女娲')">
          <img src="../../images/nvwa.jpg" alt="人物2" class="character-image">
          <h2>女娲</h2>
        </div>
        <div class="character-card" @click="gotocharacter('伏羲')">
          <img src="../../images/fuxi.jpg" alt="人物3" class="character-image">
          <h2>伏羲</h2>
        </div>
        <div class="character-card" @click="gotocharacter('盘古')">
          <img src="../../images/pangu.jpg" alt="人物4" class="character-image">
          <h2>盘古</h2>
        </div>
        <div class="character-card" @click="gotocharacter('仓颉')">
          <img src="../../images/cangxie.jpg" alt="人物5" class="character-image">
          <h2>仓颉</h2>
        </div>
        <div class="character-card" @click="gotocharacter('蚩尤')">
          <img src="../../images/chiyou.jpg" alt="人物6" class="character-image">
          <h2>蚩尤</h2>

        </div>
        <div class="character-card" @click="gotocharacter('风后')">
          <img src="../../images/fenghou.jpg" alt="人物7" class="character-image">
          <h2>风后</h2>
        </div>
        <div class="character-card" @click="gotocharacter('嫘祖')">
          <img src="../../images/leizu.jpg" alt="人物8" class="character-image">
          <h2>嫘祖</h2>
        </div>
        <div class="character-card" @click="gotocharacter('后稷')">
          <img src="../../images/houji.jpg" alt="人物9" class="character-image">
          <h2>后稷</h2>

        </div>
        <div class="character-card" @click="gotocharacter('嫦娥')">
          <img src="../../images/change.jpg" alt="人物10" class="character-image">
          <h2>嫦娥</h2>
        </div>
        <div class="character-card" @click="gotocharacter('羿')">
          <img src="../../images/houyi.jpg" alt="人物11" class="character-image">
          <h2>羿</h2>
        </div>
        <div class="character-card" @click="gotocharacter('织女')">
          <img src="../../images/zhinv.jpg" alt="人物12" class="character-image">
          <h2>织女</h2>
        </div>
      </div>
    </div>

    <AppFooter/>

    </body>
  </div>
</template>

<script>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";

export default {
  name: "CharactersPage",
  components: {
    AppHeader,
    AppFooter
  },
  methods: {
    gotocharacter(input) {
      this.$router.push({
        name: 'onecharacter',
        query: {
          c_name: input
        }
      })
    }
  },
  data() {
    return {};
  }
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

.banner, .main-col {
  background-image: url('../../images/mainbg.jpg');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center center;
  background-attachment: fixed;
  background-color: transparent;
}
</style>
