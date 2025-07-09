<template>
  <header>
    <div class="container">
      <div id="branding"></div>
      <nav>
        <ul>
          <li>
            <router-link to="/">主页</router-link>
          </li>
          <li :class="{ current: $route.path === '/characters' }">
            <router-link to="/characters">人物关系</router-link>
          </li>
          <li :class="{ current: $route.path === '/places' }">
            <router-link to="/places">地点关系</router-link>
          </li>
          <li :class="{ current: $route.path === '/events' }">
            <router-link to="/events">事件关系</router-link>
          </li>
          <li :class="{ current: $route.path === '/books' }">
            <router-link to="/books">涉及典籍</router-link>
          </li>
          <li :class="{ current: $route.path === '/AIchat' }">
            <router-link to="/AIchat">AI对话</router-link>
          </li>
          <li :class="{ current: $route.path === '/advice' }">
            <router-link to="/advice">联系我们</router-link>
          </li>

        </ul>
      </nav>
    </div>
  </header>
</template>
<script>
export default {name: "AppHeader"}
</script>
<style scoped>

</style>
