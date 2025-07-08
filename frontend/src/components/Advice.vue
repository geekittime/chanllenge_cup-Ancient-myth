<template>
    <div class="Advice">

        <head>
            <meta charset="UTF-8">
            <title>中国神话人物介绍</title>
        </head>

        <body>
            <AppHeader />

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


            <AppFooter />
        </body>
    </div>
</template>

<script>
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

function validateEmail(email) {
    var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

export default {
    name: "AdvicePage",
    components: {
        AppHeader,
        AppFooter
    },
    data() {
        return {};
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
