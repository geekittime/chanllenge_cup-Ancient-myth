<template>
  <div class="chat-panel">
    <AppHeader/>

    <div class="chat-title">上古神话</div>

    <div class="message-panel-wrapper">
    <div class="message-panel" id="message-panel">
      <div class="message-list">
        <div
            :class="['message-item', item.type === 1 ? 'ai-item' : '']"
            v-for="(item, index) in messageList"
            :key="index"
            :id="'item' + index"
        >
          <template v-if="item.type === 0">
            <div class="message-content">
              <div class="content-inner">{{ item.content }}</div>
            </div>
            <div class="user-icon">我</div>
          </template>
          <template v-else>
            <div class="user-icon">AI</div>
            <div class="message-content ai-item">
              <div
                  class="markdown-content"
                  v-html="renderMarkdown(item.content.join(''))"
              ></div>
              <div class="loading" v-if="item.loading">
                <img src="../assets/loading.gif" />
              </div>
            </div>
          </template>
        </div>
<<<<<<< HEAD
      </div>
=======

        <div class="send-panel">
            <el-form :model="formData" ref="formDataRef" @submit.prevent>
                <el-form-item prop="content">
                    <el-input type="textarea" :rows="2" clearable placeholder="请输入你想问的问题" v-model="formData.content"
                        @keyup.native="keySend" />
                </el-form-item>
                <el-form-item label="" prop="" class="send-btn">
                    <el-button circle :disabled="loading" class="send-icon-btn" @click="sendMessage">
                        <i class="el-icon-s-promotion"></i>
                    </el-button>
                </el-form-item>

            </el-form>
        </div>
        <AppFooter />
>>>>>>> 6f711ee26b46ee3b3e48e97b48fb08c0b18e50ea
    </div>
    </div>

    <div class="send-panel">
      <el-form :model="formData" ref="formDataRef" @submit.prevent>
        <el-form-item prop="content">
          <el-input
              type="textarea"
              :rows="3"
              clearable
              placeholder="请输入你想问的问题"
              v-model="formData.content"
              @keyup.native="keySend"
          />
        </el-form-item>
        <el-form-item label="" prop="" class="send-btn">
          <el-button
              circle
              :disabled="loading"
              class="send-icon-btn"
              @click="sendMessage"
          >
            <img src="../assets/img_6.png" class="send-icon-img" />
          </el-button>
        </el-form-item>

      </el-form>
    </div>
    <AppFooter/>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { marked } from 'marked';
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";

export default {
<<<<<<< HEAD
  name: 'ChatPage',
  components: {AppFooter, AppHeader},
  data() {
    return {
      formData: {
        content: '',
      },
      messageList: [],
      loading: false,
      eventSource: null,
=======
    name: 'ChatPage',
    components: { AppFooter, AppHeader },
    data() {
        return {
            formData: {
                content: '',
            },
            messageList: [],
            loading: false,
            eventSource: null,
        }
    },
    methods: {
        goToSelfInfo() {
            this.$router.push('/selfInfo')
        },
        keySend(e) {
            if (e.key === 'Enter') {
                if (!this.formData.content) {
                    Message.warning('请输入内容')
                    return
                }
                this.sendMessage()
            }
        },
        sendMessage() {
            const message = this.formData.content
            if (!message) {
                Message.warning('请输入内容')
                return
            }

            this.messageList.push({
                type: 0,
                content: message,
            })

            this.messageList.push({
                type: 1,
                content: [],
                loading: true,
            })

            this.loading = true
            this.formData.content = ''

            this.eventSource = new EventSource(`/api/stream?message=${message}`)

            this.eventSource.onmessage = (event) => {
                let response = event.data
                if (response === 'end') {
                    this.close()
                    return
                }

                try {
                    const content = JSON.parse(response).content
                    this.messageList[this.messageList.length - 1].content.push(content)
                } catch (e) {
                    console.error('解析错误:', e)
                }

                this.$nextTick(() => {
                    const panel = document.getElementById('message-panel')
                    if (panel) {
                        panel.scrollTop = panel.scrollHeight
                    }
                })
            }

            this.eventSource.onerror = () => {
                this.close()
            }
        },
        close() {
            if (this.eventSource) {
                this.eventSource.close()
                this.eventSource = null
            }
            this.messageList[this.messageList.length - 1].loading = false
            this.loading = false
        },
        renderMarkdown(text) {
            return marked(text || '')
        }
    },
    beforeDestroy() {
        this.close()
>>>>>>> 6f711ee26b46ee3b3e48e97b48fb08c0b18e50ea
    }
  },
  methods: {
    goToSelfInfo() {
      this.$router.push('/selfInfo')
    },
    keySend(e) {
      // if (e.ctrlKey && e.key === 'Enter') {
      //   this.sendMessage()
      // }
      if (e.key === 'Enter') {
        if(!this.formData.content) {
          Message.warning('请输入内容')
          return
        }
          this.sendMessage()
      }
    },
    sendMessage() {
      const message = this.formData.content
      if (!message) {
        Message.warning('请输入内容')
        return
      }

      this.messageList.push({
        type: 0,
        content: message,
      })

      this.messageList.push({
        type: 1,
        content: [],
        loading: true,
      })

      this.loading = true
      this.formData.content = ''

      this.eventSource = new EventSource(`/api/stream?message=${message}`)
      //Message.success('开始输入')

      this.eventSource.onmessage = (event) => {
        let response = event.data
        // Message.success(response)
        if (response === 'end') {
          this.close()
          return
        }

        try {
          const content = JSON.parse(response).content
          this.messageList[this.messageList.length - 1].content.push(content)
          //Message.success(this.messageList)
        } catch (e) {
          console.error('解析错误:', e)
        }

        this.$nextTick(() => {
          const panel = document.getElementById('message-panel')
          if (panel) {
            panel.scrollTop = panel.scrollHeight
          }
        })
      }

      this.eventSource.onerror = () => {
        this.close()
      }
    },
    close() {
      if (this.eventSource) {
        this.eventSource.close()
        this.eventSource = null
      }
      this.messageList[this.messageList.length - 1].loading = false
      this.loading = false
    },
    renderMarkdown(text) {
      return marked(text || '')
    }
  },
  beforeDestroy() {
    this.close()
  }
}
</script>

<style scoped lang="scss">
.message-panel-wrapper {
  flex: 1; /* 让消息面板区域占据 Header, Title, Footer, Send Panel 之外的所有剩余垂直空间 */
  display: flex;
  justify-content: center; /* 水平居中 */

  padding: 0 20px 20px; /* 顶部0，左右20px，底部20px的内边距，为了让白框不贴边 */
  box-sizing: border-box;

}
.chat-panel .message-panel {

  background: rgba(255, 255, 255, 0.55); /* 半透明白色背景 */
  border-radius: 15px; /* 圆角 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); /* 阴影 */

  width: 900px; /* 或根据需要调整白框的固定宽度 */
  max-width: 90%; /* 确保在小屏幕上也能适应 */
  height: 100%; /* 填充其父容器 message-panel-wrapper 的高度 */
  overflow-y: auto; /* 如果内容超出，允许滚动 */
  padding: 20px; /* 消息列表内容与白框内边缘的距离 */
  box-sizing: border-box; /* 确保 padding 不增加宽度 */


}
.send-icon-btn {
<<<<<<< HEAD
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 50px;
  height: 50px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.send-icon-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}
.global-user-avatar {
  position: fixed;
  top: 16px;
  right: 16px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  cursor: pointer;
  z-index: 1000;
  transition: box-shadow 0.2s, transform 0.2s;
}

.global-user-avatar:hover {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
  transform: scale(1.15);
}

.chat-panel {
  //background: #eff0f6;
  background-image: url('../assets/img_7.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  //background: white;
  //height: 100vh;
  position: relative;

  .chat-title {
    font-family: '华文行楷', serif;
    text-align: center;
    font-size: 35px;
    font-weight: bold;
  }

  .message-panel {
    height: calc(100vh - 200px);
    overflow-y: auto;
    padding-bottom: 10px;
    //background-image: url('../assets/img.png');
    background-size: contain;
    background-position: center;
    background-repeat: no-repeat;
    //background-attachment: fixed;

    .message-list {
      margin: 0 auto;
      width: 800px;

      .message-item {
        margin: 10px 0;
        display: flex;

        .user-icon {
          width: 40px;
          height: 40px;
          line-height: 40px;
          border-radius: 50%;
          //background: #535353;
          background: orange;
          color: white;
          text-align: center;
          margin-left: 10px;
        }

        .message-content {
          flex: 1;
          margin-left: 10px;
          display: flex;
          justify-content: flex-end;
          align-items: center;
        }

        .content-inner {
          //background: #2d65f7;
          background: white;
          //background: transparent;
          border-radius: 5px;
          padding: 10px;
          //color: #fff;
          color:black;
        }
      }

      .ai-item {
        .message-content {
          background: white;
          //background: transparent;
          display: block;
          border-radius: 5px;
          color: #0f0f0f;
        }

        .user-icon {
          //background: #64018f;
          background: orange;
          margin-left: 0;
        }

        .loading {
          text-align: center;
        }

        .markdown-content {
          padding: 10px;
          font-size: 14px;
          line-height: 1.6;
          color: #0f0f0f;
        }
      }
    }
  }

  .send-panel {
    width: 650px;
    margin: 5px auto 0;
    //background: transparent;
    border-radius: 10px;
    padding: 10px;
    //background-image: url('../assets/img_2.png');
    background: white;
    background-size: 100% 100%;
    background-repeat: no-repeat;
    background-position: center;
    color: #000;

    .send-btn {
      text-align: right;
      margin-bottom: 0;
      padding: 5px;
    }

    ::v-deep .el-textarea__inner {
      background: transparent !important; // 确保输入框内部是透明的
      border: none !important;
      resize: none !important;
      box-shadow: none;
      color: #0f0f0f;
=======
    position: absolute;
    bottom: 8px;
    right: 8px;
    width: 36px;
    height: 36px;
    padding: 0;
    background: #409EFF;
    border: none;
    color: white;
    font-size: 16px;

    &:hover {
        background: #66b1ff;
    }

    &:disabled {
        background: #c0c4cc;
        cursor: not-allowed;
    }
}

.chat-panel {
    background-image: url('../../images/mainbg.jpg');
    background-repeat: no-repeat;
    background-position: center;
    background-size: cover;
    position: relative;
    height: 100vh;
    display: flex;
    flex-direction: column;

    .chat-title {
        font-family: '华文行楷', serif;
        text-align: center;
        font-size: 28px;
        font-weight: bold;
        color: #2c3e50;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
        margin: 10px 0;
        padding: 5px 0;
    }

    .message-panel {
        flex: 1;
        overflow-y: auto;
        padding: 10px 0;

        .message-list {
            margin: 0 auto;
            width: 800px;
            max-width: 90%;

            .message-item {
                margin: 10px 0;
                display: flex;
                animation: fadeInUp 0.3s ease;

                .user-icon {
                    width: 36px;
                    height: 36px;
                    line-height: 36px;
                    border-radius: 50%;
                    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
                    color: white;
                    text-align: center;
                    margin-left: 8px;
                    font-weight: bold;
                    font-size: 12px;
                    box-shadow: 0 2px 6px rgba(255, 154, 158, 0.4);
                }

                .message-content {
                    flex: 1;
                    margin-left: 10px;
                    display: flex;
                    justify-content: flex-end;
                    align-items: center;
                }

                .content-inner {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    border-radius: 16px;
                    padding: 8px 14px;
                    color: white;
                    max-width: 75%;
                    word-wrap: break-word;
                    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
                    font-size: 14px;
                    line-height: 1.4;
                }
            }

            .ai-item {
                .message-content {
                    background: rgba(255, 255, 255, 0.92);
                    display: block;
                    border-radius: 16px;
                    color: #2c3e50;
                    max-width: 75%;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                    backdrop-filter: blur(10px);
                }

                .user-icon {
                    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
                    margin-left: 0;
                    margin-right: 10px;
                    box-shadow: 0 2px 6px rgba(168, 237, 234, 0.4);
                }

                .loading {
                    text-align: center;
                    padding: 8px;
                }

                .markdown-content {
                    padding: 10px 14px;
                    font-size: 14px;
                    line-height: 1.5;
                    color: #2c3e50;
                }
            }
        }
    }

    .send-panel {
        width: 600px;
        max-width: 90%;
        margin: 0 auto 10px;
        background: rgba(255, 255, 255, 0.9);
        backdrop-filter: blur(15px);
        border-radius: 20px;
        padding: 12px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        position: relative;

        .send-btn {
            text-align: right;
            margin-bottom: 0;
            padding: 0;
        }

        ::v-deep .el-textarea__inner {
            background: transparent !important;
            border: 1px solid rgba(102, 126, 234, 0.2) !important;
            border-radius: 12px !important;
            resize: none !important;
            box-shadow: none !important;
            color: #2c3e50 !important;
            font-size: 14px;
            line-height: 1.4;
            padding: 10px 50px 10px 12px !important;
            min-height: 40px !important;
            transition: all 0.3s ease;

            &:focus {
                border-color: #667eea !important;
                box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1) !important;
            }

            &::placeholder {
                color: #a0a0a0;
                font-style: italic;
            }
        }

        ::v-deep .el-form-item {
            margin-bottom: 0;
        }
>>>>>>> 6f711ee26b46ee3b3e48e97b48fb08c0b18e50ea
    }
  }
}

<<<<<<< HEAD
.no-data {
  text-align: center;
  color: #5f5f5f;
=======
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(15px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
>>>>>>> 6f711ee26b46ee3b3e48e97b48fb08c0b18e50ea
}
</style>