import Vue from 'vue'
import App from './App.vue'
import router from './router/index'
import * as echarts from 'echarts'
//import neo4j from 'neo4j-driver'
Vue.prototype.$echarts = echarts

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
