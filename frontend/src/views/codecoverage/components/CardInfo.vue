<template>
  <!-- <div class="cardInfo">
    <div>
      <p>增量代码覆盖率</p>
      <p><a :href="allfilepath" target="_blank">{{ allcount }}</a></p>
    </div>
    <div v-if="compareType && compareType==='branch'">
      <p>增量代码覆盖率</p>
      <p><a :href="difffilepath" target="_blank">{{ diffcount }}</a></p>
    </div>
  </div> -->
  <a-form>
    <a-row>
      <a-col :md="16" :sm="16"></a-col>
      <!-- <a-col :md="6" :sm="6">{{ allcount }}</a-col> -->
    </a-row>
    <a-row style="margin-top: 8px;">
      <a-col :md="18" :sm="18">全量代码覆盖率</a-col>
      <a-col :md="6" :sm="6">
        <p>
          <a :href="allfilepath" target="_blank">{{ allCount }}</a>
        </p>
      </a-col>
    </a-row>
    <a-row style="margin-bottom: 0px;" v-if="compareType && compareType==='branch'">
      <a-col :md="18" :sm="18">增量代码覆盖率</a-col>
      <a-col :md="6" :sm="6">
        <p>
          <a :href="difffilepath" target="_blank">{{ diffCount }}</a>
        </p>
      </a-col>
    </a-row>
  </a-form>
</template>

<script>
export default {
  name: 'CardInfo',
  props: {
    activeUser: {
      type: [String, Number],
      default: 0
    },
    newUser: {
      type: [String, Number],
      default: 0
    },
    compareType: {
      type: String,
      default: ''
    },
    allcount: {
      type: String,
      default: ''
    },
    allfilepath: {
      type: String,
      default: ''
    },
    diffcount: {
      type: String,
      default: ''
    },
    difffilepath: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      allCount: this.allcount,
      allFilePath: this.allfilepath,
      diffCount: this.diffcount,
      diffFilePath: this.difffilepath
    }
  },
  mounted () {
    if (!this.allCount) {
      this.allCount = '----'
    }
    if (!this.diffCount) {
      this.diffCount = '----'
    }
  }
}
</script>

<!-- <style lang="less" scoped>
@import "~@/components/index.less";
@import "~@/utils/utils.less";

.cardInfo {

  .clearfix();

  margin-top: 16px;
  margin-left: 40px;
  & > div {
    position: relative;
    float: left;
    width: 33%;
    text-align: left;
    p {
      margin: 0;
      font-size: 24px;
      line-height: 32px;
    }
    p:first-child {
      margin-bottom: 4px;
      color: @text-color-secondary;
      font-size: 12px;
      line-height: 20px;
    }
  }
}
</style> -->
