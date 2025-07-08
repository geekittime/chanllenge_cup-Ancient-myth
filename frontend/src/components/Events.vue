<template>
  <div class="Events">
    <head>
      <meta charset="UTF-8">
      <title>中国神话人物介绍</title>
    </head>
    <body>
    <AppHeader/>

    <div class="main-col">
      <div class="circle-container">
        <div class="circle">
          <div class="circle-text">盘古开天地</div>
        </div>
        <div class="circle">
          <div class="circle-text">女娲补天</div>
        </div>
        <div class="circle">
          <div class="circle-text">嫦娥奔月</div>
        </div>
        <div class="circle">
          <div class="circle-text">愚公移山</div>
        </div>
        <div class="circle">
          <div class="circle-text">后羿射日</div>
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
  name: "EventsPage",
  components: {
    AppHeader,
    AppFooter
  },
  data() {
    return {};
  },
};
</script>

<style scoped>
@import '../assets/styles.css';

.circle-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: flex-start;
  height: 100vh;
  margin: 0 auto;
  max-width: 800px;
}

.circle {
  width: 250px;
  height: 250px;
  background-size: cover;
  border-radius: 50%;
  margin: 20px;
  position: relative;
}

.circle:nth-child(1) {
  background-image: url('../../images/e1.png');
  transform: translateY(20px) translateX(-280px) scale(1);
}

.circle:nth-child(1):hover {
  transform: translateY(20px) translateX(-280px) scale(1.1);
}

.circle:nth-child(2) {
  background-image: url('../../images/e2.png');
  transform: translateY(20px) translateX(250px) scale(1);
}

.circle:nth-child(2):hover {
  transform: translateY(20px) translateX(250px) scale(1.1);
}

.circle:nth-child(3) {
  background-image: url('../../images/e3.png');
  transform: translateY(20px) translateX(-50px) scale(1);
}

.circle:nth-child(3):hover {
  transform: translateY(20px) translateX(-50px) scale(1.1);
}

.circle:nth-child(4) {
  background-image: url('../../images/e4.png');
  transform: translateY(20px) translateX(20px) scale(1);
}

.circle:nth-child(4):hover {
  transform: translateY(20px) translateX(20px) scale(1.1);
}

.circle:nth-child(5) {
  background-image: url('../../images/e5.png');
  transform: translateY(-570px);
}

.circle:nth-child(5):hover {
  transform: translateY(-570px) scale(1.1);
}

.circle-text {
  position: absolute;
  text-align: center;
  width: 100%;
  left: 50%;
  transform: translateX(-50%);
  font-family: '华文行楷', serif;
  font-weight: bold;
  color: rgba(130, 73, 4, 0.622);
  transition: color 0.3s ease;
}

.circle:hover .circle-text {
  color: white;
}


.circle:nth-child(1) .circle-text {
  bottom: -90px;
  font-size: 40px;
}

.circle:nth-child(2) .circle-text {
  bottom: -90px;
  font-size: 40px;
}

.circle:nth-child(3) .circle-text {
  bottom: 270px;
  font-size: 40px;
}

.circle:nth-child(4) .circle-text {
  bottom: 270px;
  font-size: 40px;
}

.circle:nth-child(5) .circle-text {
  bottom: -90px;
  font-size: 40px;
}
</style>
