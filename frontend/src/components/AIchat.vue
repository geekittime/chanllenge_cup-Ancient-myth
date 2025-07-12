<template>
  <div class="chat-container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <el-button
            type="primary"
            icon="el-icon-plus"
            @click="createNewSession"
            :loading="creatingSession">
          新建对话
        </el-button>
      </div>
      <div class="session-list">
        <div
            v-for="session in sessionList"
            :key="session.id"
            :class="['session-item', { 'active': currentSessionId === session.id }]"
            @click="switchSession(session.id)">
          <span class="session-name">{{ session.sessionName }}</span>
          <span class="session-time">{{ formatTime(session.updateTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 主聊天区域 -->
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
                      v-html="renderMarkdown(item.content)"
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
                :disabled="loading || !currentSessionId"
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
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { marked } from 'marked';
import AppHeader from "@/components/AppHeader.vue";
import axios from 'axios';

export default {
  name: 'ChatPage',
  components: { AppHeader},
  data() {
    return {
      formData: {
        content: '',
      },
      messageList: [],
      sessionList: [],
      currentSessionId: null,
      userId: 'user', // 这里可以从登录信息获取
      loading: false,
      creatingSession: false,
      eventSource: null,
    }
  },
  async mounted() {
    await this.loadUserSessions();
    if (this.sessionList.length === 0) {
      await this.createNewSession();
    } else {
      this.currentSessionId = this.sessionList[0].id;
      await this.loadSessionRecords(this.currentSessionId);
    }
  },
  methods: {
    // 加载用户的会话列表
    async loadUserSessions() {
      try {
        const response = await axios.get(`/api/chat/sessions?userId=${this.userId}`);
        this.sessionList = response.data;
      } catch (error) {
        console.error('加载会话列表失败:', error);
        Message.error('加载会话列表失败');
      }
    },

    // 创建新会话
    async createNewSession() {
      this.creatingSession = true;
      try {
        const response = await axios.post(`/api/chat/session?userId=${this.userId}`);
        const newSession = response.data;
        this.sessionList.unshift(newSession);
        this.currentSessionId = newSession.id;
        this.messageList = [];
        Message.success('新对话创建成功');
      } catch (error) {
        console.error('创建新会话失败:', error);
        Message.error('创建新会话失败');
      } finally {
        this.creatingSession = false;
      }
    },

    // 切换会话
    async switchSession(sessionId) {
      if (this.currentSessionId === sessionId) return;

      this.currentSessionId = sessionId;
      await this.loadSessionRecords(sessionId);
    },

    // 加载会话的聊天记录
    async loadSessionRecords(sessionId) {
      try {
        const response = await axios.get(`/api/chat/records/${sessionId}`);
        this.messageList = response.data.map(record => ({
          type: record.messageType,
          content: record.messageType === 1 ? record.content : record.content,
          loading: false
        }));

        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('加载聊天记录失败:', error);
        Message.error('加载聊天记录失败');
      }
    },

    // 滚动到底部
    scrollToBottom() {
      const panel = document.getElementById('message-panel');
      if (panel) {
        panel.scrollTop = panel.scrollHeight;
      }
    },

    // 格式化时间
    formatTime(timeStr) {
      const date = new Date(timeStr);
      const now = new Date();
      const diff = now - date;

      if (diff < 60000) { // 1分钟内
        return '刚刚';
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前';
      } else if (diff < 86400000) { // 24小时内
        return Math.floor(diff / 3600000) + '小时前';
      } else {
        return date.toLocaleDateString();
      }
    },

    keySend(e) {
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

      if (!this.currentSessionId) {
        Message.warning('请先创建对话')
        return
      }

      this.messageList.push({
        type: 0,
        content: message,
      })

      this.messageList.push({
        type: 1,
        content: '',
        loading: true,
      })

      this.loading = true
      this.formData.content = ''

      this.eventSource = new EventSource(`/api/stream?message=${encodeURIComponent(message)}&sessionId=${this.currentSessionId}&userId=${this.userId}`)

      this.eventSource.onmessage = (event) => {
        let response = event.data
        if (response === 'end') {
          this.close()
          // 刷新会话列表以更新时间
          this.loadUserSessions()
          return
        }

        try {
          const content = JSON.parse(response).content
          this.messageList[this.messageList.length - 1].content += content
        } catch (e) {
          console.error('解析错误:', e)
        }

        this.$nextTick(() => {
          this.scrollToBottom()
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
.chat-container {
  display: flex;
  height: 100vh;
  //background-image: url('../assets/img_7.png');  /* 新增 */
  background-repeat: no-repeat;                   /* 新增 */
  background-position: center;                    /* 新增 */
  background-size: cover;                         /* 新增 */
  background-attachment: fixed;
}
/* 新增伪元素背景 */
.chat-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-image: url('../assets/img_7.png');
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  z-index: -1;
}

.sidebar {
  width: 200px;
  background: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;

  .sidebar-header {
    padding: 20px;
    border-bottom: 1px solid #e0e0e0;
  }

  .session-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;

    .session-item {
      padding: 15px;
      margin-bottom: 8px;
      background: white;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      border: 1px solid transparent;

      &:hover {
        background: #f0f0f0;
      }

      &.active {
        background: #e6f7ff;
        border-color: #1890ff;
      }

      .session-name {
        display: block;
        font-weight: 500;
        margin-bottom: 5px;
      }

      .session-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.chat-panel {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;

  .chat-title {
    font-family: '华文行楷', serif;
    text-align: center;
    font-size: 35px;
    font-weight: bold;
    padding: 20px 0;
  }

  .message-panel-wrapper {
    flex: 1;
    display: flex;
    justify-content: center;
    padding: 0 20px 20px;
    box-sizing: border-box;
  }

  .message-panel {
    background: rgba(255, 255, 255, 0.55);
    border-radius: 15px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    width: 900px;
    max-width: 90%;
    height: 100%;
    max-height: calc(100vh - 320px);  //不加这一行整个页面滚动
    overflow-y: auto;
    padding: 20px;
    box-sizing: border-box;

    .message-list {
      margin: 0 auto;
      width: 100%;

      .message-item {
        margin: 10px 0;
        display: flex;

        .user-icon {
          width: 40px;
          height: 40px;
          line-height: 40px;
          border-radius: 50%;
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
          background: white;
          border-radius: 5px;
          padding: 10px;
          color: black;
        }
      }

      .ai-item {
        .message-content {
          background: white;
          display: block;
          border-radius: 5px;
          color: #0f0f0f;
        }

        .user-icon {
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
    margin: 5px auto 20px;
    border-radius: 10px;
    padding: 10px;
    background: white;
    color: #000;

    .send-btn {
      text-align: right;
      margin-bottom: 0;
      padding: 5px;
    }

    ::v-deep .el-textarea__inner {
      background: transparent !important;
      border: none !important;
      resize: none !important;
      box-shadow: none;
      color: #0f0f0f;
    }
  }
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
</style>