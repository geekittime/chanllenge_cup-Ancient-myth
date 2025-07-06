<template>
    <div class="CharacterPage">
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
                        <li class="current"><router-link to="/characters">人物关系</router-link></li>
                        <li><router-link to="/places">地点关系</router-link></li>
                        <li><router-link to="/events">事件关系</router-link></li>
                        <li><router-link to="/books">涉及典籍</router-link></li>
                        <li><router-link to="/advice">联系我们</router-link></li>
                    </ul>
                </nav>
            </div>
            </header>

            <div class="main-col">
                <CharacterChat ref="characterchat"></CharacterChat>
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
  import CharacterChat from './CharacterChat.vue';
  export default {
    name: "CharacterPage",
    mounted(){
        this.init();
    },
    components: {
        CharacterChat,
    },
    data() {
      return {
        input:'',
      };
    },
    methods:{
        init(){
            this.input=this.$route.query.c_name;
            this.$refs.characterchat.updateMsg(this.input)
            //this.$refs.CharacterChat.updateMsg(this.$route.query.c_name)
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

        .banner, .main-col {  
            background-image: url('../../images/mainbg.jpg');  
            background-repeat: no-repeat;  
            background-size: cover;  
            background-position: center center;  
            background-attachment: fixed; 
            background-color: transparent; 
        }
</style>