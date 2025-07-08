<template>
  <div class="Places">
    <head>
      <meta charset="UTF-8">
      <title>中国神话人物介绍</title>
    </head>
    <body>
    <AppHeader/>

    <div class="main-col">
      <PlaceChat/>
    </div>


    <AppFooter/>
    </body>
  </div>
</template>

<script>
/*        <div class="btn-wrapper">
            <button class="giant-btn btn-bg1" data-target="menu1">山</button>
            <div class="additional-buttons" id="menu1">
                <button class="additional-btn">昆仑山</button>
                <button class="additional-btn">蓬莱山</button>
                <button class="additional-btn">嵩山</button>
                <button class="additional-btn">黄山</button>
                <button class="additional-btn">泰山</button>
                <button class="additional-btn">大梁山</button>
                <button class="additional-btn">五指岭</button>
            </div>
        </div>
        <div class="btn-wrapper">
            <button class="giant-btn btn-bg2" data-target="menu2">水</button>
            <div class="additional-buttons" id="menu2">
                <button class="additional-btn">长江</button>
                <button class="additional-btn">黄河</button>
                <button class="additional-btn">云梦泽</button>
                <button class="additional-btn">弱水</button>
                <button class="additional-btn">黑水</button>
                <button class="additional-btn">青邱泽</button>
                <button class="additional-btn">丹江</button>
            </div>
        </div>
        <div class="btn-wrapper">
            <button class="giant-btn btn-bg3" data-target="menu3">其他</button>
            <div class="additional-buttons" id="menu3">
                <button class="additional-btn">轩辕故里</button>
                <button class="additional-btn">云岩宫</button>
                <button class="additional-btn">修德观</button>
                <button class="additional-btn">铸鼎原</button>
                <button class="additional-btn">崆峒</button>
                <button class="additional-btn">钧台</button>
                <button class="additional-btn">天维</button>
            </div>
        </div>*/
import PlaceChat from './PlaceChat.vue';
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";

document.querySelectorAll('.giant-btn').forEach(function (btn) {
  btn.addEventListener('click', function () {
    document.querySelectorAll('.additional-buttons').forEach(function (menu) {
      menu.style.display = 'none';
    });
    var menuId = this.getAttribute('data-target');
    var menu = document.getElementById(menuId);
    if (menu) {
      menu.style.display = 'block';
    }
  });
});
export default {
  name: "PlacesPage",
  data() {
    return {
      test: 1111
    };
  },
  components: {
    PlaceChat,
    AppHeader,
    AppFooter
  }
};
</script>

<style scoped>
@import '../assets/styles.css';

.main-col {
  display: flex;
  flex-wrap: nowrap;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  width: 100%;
  font-size: 0;
}

.giant-btn {
  padding: 150px 400px;
  border: none;
  cursor: pointer;
  background-color: transparent;
  font-size: 80px;
  color: rgb(157, 108, 10);
  font-family: "STKaiti", "华文行楷", serif;
  font-weight: bold;

}

.btn-bg1 {
  background-image: url('../../images/shan.png');
  background-size: cover;
}

.btn-bg2 {
  background-image: url('../../images/shui.png');
  background-size: cover;
}

.btn-bg3 {
  background-image: url('../../images/qita.png');
  background-size: cover;
}
</style>
