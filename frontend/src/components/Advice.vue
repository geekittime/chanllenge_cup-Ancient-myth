<template>
    <div class="advice">
        <AppHeader />

        <div class="main-col">
            <h2 class="page-title">意见反馈</h2>

            <form class="form-container" @submit.prevent="displayAdvice">
                <div class="advice-box">
                    <div class="input-group">
                        <label>邮箱地址</label>
                        <input type="email" v-model="email" class="email-input" placeholder="请输入您的邮箱" required>
                    </div>

                    <div class="input-group">
                        <label>您的建议</label>
                        <textarea v-model="advice" class="advice-input" placeholder="请输入您的建议" rows="4"
                            required></textarea>
                    </div>

                    <button type="submit" class="submit-btn">提交建议</button>
                </div>

                <div v-if="showMessage" class="advice-display" :class="messageType">
                    {{ displayMessage }}
                </div>
            </form>
        </div>

        <AppFooter />
    </div>
</template>

<script>
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

export default {
    name: "AdvicePage",
    components: {
        AppHeader,
        AppFooter
    },
    data() {
        return {
            email: '',
            advice: '',
            showMessage: false,
            displayMessage: '',
            messageType: ''
        };
    },
    methods: {
        validateEmail(email) {
            var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return re.test(email);
        },

        displayAdvice() {
            if (!this.validateEmail(this.email)) {
                this.showResultMessage('error', '请输入有效的邮箱地址！');
            } else {
                this.showResultMessage('success', '感谢您的建议！我们会尽快回复与处理: ' + this.advice);
                // 清空表单
                this.email = '';
                this.advice = '';
            }
        },

        showResultMessage(type, message) {
            this.messageType = type;
            this.displayMessage = message;
            this.showMessage = true;

            // 3秒后隐藏消息
            setTimeout(() => {
                this.showMessage = false;
            }, 3000);
        }
    }
};
</script>

<style scoped>
.advice {
    min-height: 100vh;
    background-image: url('../../images/mainbg.jpg');
    background-size: cover;
    background-position: center;
    display: flex;
    flex-direction: column;
}

.main-col {
    flex: 1;
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.page-title {
    text-align: center;
    font-size: 32px;
    color: #2c3e50;
    margin-bottom: 30px;
    font-family: '华文行楷', serif;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.form-container {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.advice-box {
    width: 100%;
    max-width: 500px;
    padding: 30px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 15px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.input-group {
    margin-bottom: 20px;
}

.input-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #333;
    font-size: 14px;
}

.email-input,
.advice-input {
    width: 100%;
    padding: 12px 15px;
    border: 2px solid #e1e8ed;
    border-radius: 8px;
    font-size: 14px;
    transition: border-color 0.3s ease;
    box-sizing: border-box;
}

.email-input:focus,
.advice-input:focus {
    outline: none;
    border-color: #409EFF;
}

.advice-input {
    resize: vertical;
    min-height: 100px;
    font-family: inherit;
    line-height: 1.5;
}

.submit-btn {
    width: 100%;
    padding: 12px;
    background: #409EFF;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.submit-btn:hover {
    background: #66b1ff;
}

.advice-display {
    margin-top: 20px;
    padding: 15px;
    border-radius: 8px;
    text-align: center;
    font-weight: bold;
    max-width: 500px;
    width: 100%;
}

.advice-display.success {
    background: #f0f9ff;
    color: #1d4ed8;
    border: 1px solid #93c5fd;
}

.advice-display.error {
    background: #fef2f2;
    color: #dc2626;
    border: 1px solid #fca5a5;
}
</style>