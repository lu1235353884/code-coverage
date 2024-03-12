<template>
  <a-card :bordered="false">
    <a-result :status="statusIcon" :title="result.title" :sub-title="result.description">
      <div class="content">
        <a-steps :current="curStep" :direction="isMobile && directionType.vertical || directionType.horizontal" progressDot>
          <a-step title="数据文件下载">
            <span style="font-size: 14px" slot="title">数据文件下载</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" v-if="result.downloadDataFileDate || res1 === '失败'">
                <div style="margin: 8px 0 4px">
                  数据文件下载{{ res1 }}
                </div>
                <div>{{ result.downloadDataFileDate }}</div>
              </div>
            </template>
          </a-step>
          <a-step title="代码拉取并编译">
            <span style="font-size: 14px" slot="title">代码拉取并编译</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" v-if="result.downloadBranchDate || res2 === '失败'">
                <div style="margin: 8px 0 4px">
                  代码编译{{ res2 }}
                </div>
                <div>{{ result.downloadBranchDate }}</div>
              </div>
            </template>
          </a-step>
          <a-step title="对比分支代码" v-if="isBranch">
            <span style="font-size: 14px" slot="title">对比分支代码</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" v-if="result.compareDate || res3 === '失败'">
                <div style="margin: 8px 0 4px">
                  对比分支代码{{ res3 }}
                </div>
                <div>{{ result.compareDate }}</div>
              </div>
            </template>
          </a-step>
          <a-step title="生成全量报告" >
            <span style="font-size: 14px" slot="title">生成全量报告</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" v-if="result.allFilePath || res4 === '失败'">
                <div style="margin: 8px 0 4px">
                  生成全量报告{{ res4 }}
                </div>
                <div>{{ result.allFileDate }}</div>
              </div>
            </template>
          </a-step>
          <a-step title="生成增量报告" v-if="isBranch">
            <span style="font-size: 14px" slot="title">生成增量报告</span>
            <template slot="description">
              <div style="fontSize: 12px; color: rgba(0, 0, 0, 0.45); position: relative; left: 42px;text-align: left;" slot="description" v-if="isBranch && (result.diffFilePath || res5 === '失败')">
                <div style="margin: 8px 0 4px">
                  生成增量报告{{ res5 }}
                </div>
                <div>{{ result.diffFileDate }}</div>
              </div>
            </template>
          </a-step>
        </a-steps>
      </div>
    </a-result>
  </a-card>
</template>

<script>
import { baseMixin } from '@/store/app-mixin'
import { getAppBranch } from '@/api/manage'

const directionType = {
  horizontal: 'horizontal',
  vertical: 'vertical'
}

export default {
  name: 'StepDetail',
  props: {
    record: {
      type: Object,
      default: null
    }
  },
  mixins: [baseMixin],
  data () {
    this.directionType = directionType
    return {
      id: 0,
      relid: this.record.id,
      appcode: this.record.appCode,
      result: {},
      status: '',
      statusIcon: '',
      isBranch: false,
      curStep: 0,
      res1: '成功',
      res2: '成功',
      res3: '成功',
      res4: '成功',
      res5: '成功',
      timerStart: false
    }
  },
  mounted () {
    this.getInfo()
  },
  methods: {
    getInfo () {
      getAppBranch(this.relid, this.appcode).then(res => {
        this.result = res.result
        if (!this.result) {
          this.result = {}
          this.result.title = '待执行'
          this.statusIcon = 'warning'
          this.status = '1'
        } else {
          const status = this.result.status
          this.status = status
          this.isBranch = this.result.compareType === 'branch'
          if (status === '1') {
            this.result.title = '待执行'
            this.statusIcon = 'warning'
          } else if (status === '2') {
            this.result.title = '执行中'
            this.statusIcon = 'warning'
          } else if (status === '3') {
            this.result.title = '执行完成'
            this.statusIcon = 'success'
          } else if (status === '4') {
            this.result.title = '执行失败'
            this.statusIcon = 'error'
          }
          this.setStep()
        }
        if (this.status === '2' && !this.timerStart) {
          this.startTimer()
        } else if (this.status !== '2') {
          this.stopTimer()
        }
      })
    },
    setStep () {
      if (this.status === '1') {
        this.curStep = 0
      } else {
        if (this.result.downloadDataFileDate) {
          this.curStep = 1
        }
        if (this.result.downloadBranchDate) {
          this.curStep = 2
        }
        if (this.result.compareDate) {
          this.curStep = 3
        }
        if (this.result.allFilePath) {
          this.curStep = 4
        }
        if (this.result.diffFilePath) {
          this.curStep = 5
        }
        if (this.status === '4') {
          if (this.curStep === 0) {
            this.res1 = '失败'
          }
          if (this.curStep === 1) {
            this.res2 = '失败'
          }
          if (this.curStep === 2) {
            this.res3 = '失败'
          }
          if (this.curStep === 3) {
            this.res4 = '失败'
          }
          if (this.curStep === 4) {
            this.res5 = '失败'
          }
        }
      }
    },
    startTimer () {
      this.timerStart = true
      var that = this
      this.timerId = setInterval(() => {
        that.getInfo() // 这里写需要执行的操作或者调用其他方法
      }, 30000)
    },
    stopTimer () {
      clearInterval(this.timerId) // 清除定时器
    }
  },
  beforeDestroy () {
    this.stopTimer() // 组件被销毁前先清除定时器
  }
}
</script>
