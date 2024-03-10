<template>
  <a-card :bordered="false">
    <a-result status="success" :title="result.title" :sub-title="result.description">
      <div class="content">
        <a-steps :current="1" :direction="isMobile && directionType.vertical || directionType.horizontal" progressDot>
          <a-step :title="步骤1">
            <span style="font-size: 14px" slot="title">数据文件下载</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" >
                <div style="margin: 8px 0 4px">
                  数据文件下载成功
                </div>
                <div>2023-12-12 12:32</div>
              </div>
            </template>
          </a-step>
          <a-step :title="步骤2">
            <span style="font-size: 14px" slot="title">代码拉取并编译</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" >
                <div style="margin: 8px 0 4px">
                  代码分支拉取中
                </div>
              </div>
            </template>
          </a-step>
          <a-step :title="步骤3">
            <span style="font-size: 14px" slot="title">对比分支代码</span>
          </a-step>
          <a-step :title="步骤4" >
            <span style="font-size: 14px" slot="title">生成报告</span>
          </a-step>
        </a-steps>
      </div>
    </a-result>
  </a-card>
</template>

<script>
import { baseMixin } from '@/store/app-mixin'

const directionType = {
  horizontal: 'horizontal',
  vertical: 'vertical'
}

export default {
  name: 'StepDetail',
  mixins: [baseMixin],
  data () {
    this.directionType = directionType
    return {
      // title: this.$t('result.success.title'),
      // description: this.$t('result.success.description')
    }
  },
  computed: {
    result () {
      return {
        title: '执行中',
        description: ''
      }
    }
  }
}
</script>
