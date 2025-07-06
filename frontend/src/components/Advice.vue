<template>
    <div class="Advice">
    <head>
        <meta charset="UTF-8">
        <title>中国神话人物介绍</title>
    </head>
    <body>
        <header>
            <div class="container">
                <div id="branding">
                </div>
                <nav>
                    <ul>
                        <li><router-link to="/">主页</router-link></li>
                        <li><router-link to="/characters">人物关系</router-link></li>
                        <li><router-link to="/places">地点关系</router-link></li>
                        <li><router-link to="/events">事件关系</router-link></li>
                        <li><router-link to="/books">涉及典籍</router-link></li>
                        <li class="current"><router-link to="/advice">联系我们</router-link></li>
                    </ul>
                </nav>
            </div>
        </header>

        <div class="main-col">  
            <form class="form-container" @submit.prevent="displayAdvice">  
                <div class="advice-box">  
                    <input type="email" class="email-input" placeholder="请输入您的邮箱" required>  
                    <input type="text" id="adviceInput" class="advice-input" placeholder="请输入您的建议" required>  
                    <input type="submit" value="提交">  
                </div>  
                <div id="adviceDisplay" class="advice-display"></div>  
            </form>  
        </div>  


        <footer>
            <div class="container">
                <p>&copy; 2024 中国神话知识图谱</p>
            </div>
        </footer>
    </body>
    </div>
</template>
  
<script>
    function validateEmail(email) {  
        var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;  
        return re.test(email);  
    }  

  export default {
    name: "AdvicePage",
    data() {
      return {
      };
    },
    methods: {
        displayAdvice() {  
            var emailInput = document.querySelector('.email-input');  
            var adviceInput = document.getElementById('adviceInput');  
            var adviceDisplay = document.getElementById('adviceDisplay');  
              
            var email = emailInput.value;  
            var advice = adviceInput.value;  
              
            if (!validateEmail(email)) {  
                adviceDisplay.textContent = '请输入有效的邮箱地址！';  
            } else {  
                adviceDisplay.textContent = '您的建议我们会尽快回复与处理: ' + advice;  
            }  
        }
    }
  };
</script>
  
<style scoped>
    @import '../assets/styles.css';
    .form-container {  
        text-align: center;  
        margin: 20px 0;  
        position: relative; 
    }  
  
    .advice-box {  
        position: absolute; 
        top: 0;  
        left: 50%; 
        transform: translateX(-50%);  
        width: 500px; 
        height: 150px; 
        padding: 20px; 
        background-color: rgba(255, 255, 255, 0.5); 
        border-radius: 10px; 
    }  
  
    .advice-input {  
        border-radius: 10px; 
        padding: 10px; 
        margin-bottom: 10px;  
        width: 90%; 
        height: 20%;
        margin-top: 10px;
    }  
  
    .advice-display {  
        margin-top: 250px;  
        text-align: center;  
        font-weight: bold;  
    }  
</style>