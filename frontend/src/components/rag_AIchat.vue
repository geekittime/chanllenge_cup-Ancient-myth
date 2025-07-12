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
        </div>
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
import AppFooter from "@/components/AppFooter.vue"; // 注意这里有花括号 { }

export default {
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

      this.eventSource = new EventSource(`http://49.0.253.31:8777/api/stream?message=${message}`)
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
    height: calc(100vh - 320px);
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
    width: 750px;
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
    }
  }
}

.no-data {
  text-align: center;
  color: #5f5f5f;
}
</style>
