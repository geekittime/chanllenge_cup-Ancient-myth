const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        // 设置开发服务器的端口号
        port: 3000, // 将此处的 8080 替换为您希望使用的端口
        // 您也可以在这里配置主机名，例如 '0.0.0.0' 或 'localhost'
        // host: 'localhost',
        proxy: {
            '/api': { // 当前端请求的 URL 以 '/api' 开头时，就触发代理
                target: 'http://49.0.253.31:8777', // <-- 【这里才是你的后端目标端口】
                changeOrigin: true, // 这是解决跨域问题的关键，它会把请求头的 Origin 字段改为目标服务器的地址
                ws: true, // 如果你的后端有 WebSocket 服务，需要开启此项

            }
        }
    }
})
