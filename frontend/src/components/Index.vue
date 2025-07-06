<template>
  <div class="Index">
    <head>
      <meta charset="UTF-8">
      <title>中国神话知识图谱</title>
    </head>
    <body>
      <header>
        <div class="container">
          <div id="branding">
          </div>      
          <nav>
            <ul>
              <li class="current"><router-link to="/">主页</router-link></li>
              <li><router-link to="/characters">人物关系</router-link></li>
              <li><router-link to="/places">地点关系</router-link></li>
              <li><router-link to="/events">事件关系</router-link></li>
              <li><router-link to="/books">涉及典籍</router-link></li>
              <li><router-link to="/advice">联系我们</router-link></li>
            </ul>
          </nav>
        </div>
      </header>

      <section class="banner">
        <div class="container">
            <h1>探索上古神话的奥秘</h1>
        </div>
      </section>

      <section class="main-col">
        <div class="container">
            <div class="icons">
                <router-link to="/characters" class="circular-button button1">人物关系</router-link>
                <router-link to="/places" class="circular-button button2">地点关系</router-link>
                <router-link to="/events" class="circular-button button3">事件关系</router-link>
                <router-link to="/books" class="circular-button button4">涉及典籍</router-link>
            </div>
        </div>
      </section>

      <footer>
        <div class="container">
            <p>&copy; 2024 中国神话知识图谱</p>
        </div>
      </footer>
    </body>
  </div>
</template>

<script>

export default {
  name: "IndexPage",
  data() {
    return {
    };
  },
  methods:{
    goToCharactersPage() {
      this.$router.push({ name: 'characters' });
      // 或者使用路径
      // this.$router.push('/about');
    },
  }
};
</script>

<style scoped>
  @import '../assets/styles.css';

  .container {
    max-width: 1170px;
    margin: auto;
    overflow: hidden;
    padding: 0 20px;
  }
  .icons .circular-button {    
    display: inline-flex; 
    align-items: center;    
    justify-content: center;    
    width: 200px;  
    height: 200px;    
    margin: 35px;   
    border-radius: 50%;    
    background-size: contain;    
    text-decoration: none;     
    border: none;    
    cursor: pointer;    
    line-height: 150px; 
    
    text-align: center;  
    transition: background-color 0.3s ease;  
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
    box-shadow: 0 0 10px 5px rgba(255, 255, 255, 0.5); 
  }

  .circular-button:not(:last-child) {    
    margin-right: 20px;    
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
  .banner  {  
    background-image: url('../../../images/mainbg.jpg');  
    background-repeat: no-repeat;  
    background-size: cover;  
    background-position: center center;  
    background-attachment: fixed; 
    background-color: transparent; 
  }
</style>