<template>
  <page-header-wrapper
    :tab-list="tabList"
    :tab-active-key="tabActiveKey"
    :tab-change="handleTabChange"
  >

    <router-view />
  </page-header-wrapper>
</template>

<script>
const getActiveKey = (path) => {
  switch (path) {
    case '/codecoverage/listReport/S16':
      return '1'
    case '/list/search/project':
      return '2'
    case '/list/search/application':
      return '3'
    default:
      return '1'
  }
}
export default {
  name: 'MainPage',
  data () {
    return {
      tabList: [
        { key: '1', tab: 'S16' },
        { key: '2', tab: '项目' },
        { key: '3', tab: '应用' }
      ],
      tabActiveKey: '1',
      search: true
    }
  },
  created () {
    this.tabActiveKey = getActiveKey(this.$route.path)

    this.$watch('$route', (val) => {
      this.tabActiveKey = getActiveKey(val.path)
    })
  },
  methods: {
    handleTabChange (key) {
      this.tabActiveKey = key
      switch (key) {
        case '1':
          this.$router.push('/codecoverage/listReport/S16')
          break
        case '2':
          this.$router.push('/list/search/project')
          break
        case '3':
          this.$router.push('/list/search/application')
          break
        default:
          this.$router.push('/workplace')
      }
    }
  }
}
</script>

<style lang="less" scoped>
.ant-pro-page-header-search {
  text-align: center;
  margin-bottom: 16px;
}
</style>
