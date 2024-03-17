<template>
  <div>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-alert showIcon="true" :message="'当前冲刺：' + sprintCode" type="info" show-icon>
      </a-alert>
    </a-card>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-form layout="inline">
        <a-row :gutter="1">
          <a-col :md="7" :sm="5">
            <a-form-item label="应用/业务中台名称">
              <a-input v-model="code" placeholder=""/>
            </a-form-item>
          </a-col>
          <a-col :pull="1" :md="3" :sm="5">
            <a-button type="primary" @click="search">查询</a-button>
          </a-col>
          <a-col :push="12" :md="3" :sm="5">
            <a-button shape="round" danger @click="handle">
              一键执行
            </a-button>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <div class="ant-pro-pages-list-applications-filterCardList">
      <a-list
        :loading="loading"
        :data-source="data"
        :grid="{ gutter: 24, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }"
        style="margin-top: 24px;">
        <a-list-item slot="renderItem" slot-scope="item">
          <a-card :body-style="{ paddingBottom: 20 }" hoverable >
            <a-card-meta :title="item.appCode">
              <template slot="avatar">
                <a-avatar size="small" src="https://gw.alipayobjects.com/zos/rmsportal/zOsKZmFRdUtvpqCImOVY.png" />
              </template>
            </a-card-meta>
            <template slot="actions">
              <a-tooltip title="查看执行详情" @click="showDetail(item)">
                <a-icon type="select" />
              </a-tooltip>
              <a-tooltip title="编辑对比分支" @click="edit(item)">
                <a-icon type="edit" />
              </a-tooltip>
              <a-tooltip title="生成报告" @click="handleChange(item)">
                <a-icon type="reload" />
              </a-tooltip>
              <!-- <a-dropdown>
                <a class="ant-dropdown-link">
                  <a-icon type="ellipsis" />
                </a>
                <a-menu slot="overlay">
                  <a-menu-item>
                    <a href="javascript:;">查看执行记录</a>
                  </a-menu-item>
                  <a-menu-item>
                    <a href="javascript:;">2nd menu item</a>
                  </a-menu-item>
                  <a-menu-item>
                    <a href="javascript:;">3rd menu item</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown> -->
            </template>
            <div style="height: 80px;">
              <card-info
                active-user="100"
                new-user="999"
                :compareType="item.compareType"
                :allcount="item.allCount"
                :allfilepath="item.allFilePath"
                :allfiledate="item.allFileDate"
                :diffcount="item.diffCount"
                :difffilepath="item.diffFilePath"
                :difffiledate="item.diffFileDate"></card-info>
            </div>
          </a-card>
        </a-list-item>
      </a-list>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import { TagSelect, StandardFormRow, Ellipsis, AvatarList } from '@/components'
import CardInfo from './components/CardInfo'
import { Modal } from 'ant-design-vue'
// import TaskForm from '@/views/list/modules/TaskForm.vue'
import BranchInfo from './components/BranchInfo.vue'
import StepDetail from './components/StepDetail.vue'
import { getCompareSprint, getSprintRels, getAppBranch, excuteTask, excuteAllTask } from '@/api/manage'

const TagSelectOption = TagSelect.Option
const AvatarListItem = AvatarList.Item

export default {
  components: {
    AvatarList,
    AvatarListItem,
    Ellipsis,
    TagSelect,
    TagSelectOption,
    StandardFormRow,
    CardInfo
  },
  data () {
    return {
      data: [],
      oriData: [],
      sprintCode: '',
      sprintId: '',
      form: this.$form.createForm(this),
      loading: false,
      type: '',
      code: ''
    }
  },
  filters: {
    fromNow (date) {
      return moment(date).fromNow()
    }
  },
  created () {

  },
  mounted () {
    getCompareSprint().then(res => {
      if (res.result) {
        this.sprintCode = res.result.sprintCode
        this.sprintId = res.result.id
        this.getList()
      }
    })
  },
  methods: {
    handleChange (record) {
      const relid = record.id
      const sprintId = record.sprintId
      const appcode = record.appCode

      getAppBranch(relid, appcode).then(res => {
        const rtn = res.result
        // 无回参或回参不含ID或回参的compareTpye为all
        if (!rtn || (rtn && !rtn.id) || (rtn && rtn.compareTpye === 'all')) {
          var that = this
          this.$confirm({
              title: '警告',
              content: '当前项目未配置分支信息，是否执行？',
              okText: '确定',
              okType: 'danger',
              cancelText: '取消',
              onOk () {
                // that.exceteTask(null, sprintId, relid, appcode)
                excuteTask(null, sprintId, relid, appcode).then(res => {
                  that.$message.info('任务开始执行，请稍后查看执行进度')
                })
              },
              onCancel () {
                console.log('Cancel')
              }
            })
        } else {
          if (rtn.status === '2') {
            this.$message.info('当前应用正在执行中，请稍等')
          } else {
            this.exceteTask(rtn.id, sprintId, relid, appcode)
          }
        }
      })
    },
    exceteTask (id, sprintId, relid, appcode) {
      var that = this
      Modal.confirm({
        title: this.$t('请确认是否重新计算覆盖率'),
        content: this.$t('请确认是否重新计算覆盖率'),
        onOk: () => {
          excuteTask(id, sprintId, relid, appcode).then(res => {
            that.$message.info('任务开始执行，请稍后查看执行进度')
          })
        },
        onCancel () { }
      })
    },
    showDetail (record) {
      console.log(record)
      var that = this
      this.$dialog(StepDetail,
        // component props
        {
          record: record,
          on: {
            close () {
              that.getList()
            }
          }
        },
        // modal props
        {
          title: '执行详情',
          width: 900,
          centered: true,
          maskClosable: false
        })
    },
    edit (record) {
      this.$dialog(BranchInfo,
        // component props
        {
          record: record
        },
        // modal props
        {
          title: '编辑对比分支',
          width: 700,
          centered: true,
          maskClosable: false
        }
      )
    },
    getList () {
      // this.$http.get('/downLoadFile/file', { params: { appId: 'BM-OPSC' } }).then(res => {
      //   console.log('res', res)
      //   this.data = res.result
      //   this.loading = false
      // })
      // this.$http.get('/list/article', { params: { count: 8 } }).then(res => {
      //   console.log('res', res)
      //   this.data = res.result
      //   this.loading = false
      // })
      this.data = []
      this.loading = true
      getSprintRels(this.sprintId).then(res => {
        this.oriData = res.result
        this.data = res.result
        this.loading = false
      })
    },
    closeDialog () {
      this.$dialog.close()
    },
    search () {
      if (this.code) {
        this.data = []
        var that = this
        const dataCopy = Object.assign([], this.oriData)
        dataCopy.forEach(e => {
          if (e.appCode.includes(that.code)) {
            that.data.push(e)
          }
        })
      } else {
        this.getList()
      }
    },
    handle () {
      var that = this
      Modal.confirm({
        title: this.$t('一键执行所有应用/业务中台'),
        content: this.$t('所有已生成的报告将被删除，请谨慎执行！'),
        onOk: () => {
          excuteAllTask(this.sprintId).then(res => {
            if (res && res.success) {
              that.$message.info('任务已进入执行队列，请等待')
            } else {
              that.$message.error('任务执行失败')
            }
          })
        },
        onCancel () { }
      })
    }
  }
}
</script>

<style lang="less" scoped>
.ant-pro-components-tag-select {
  :deep(.ant-pro-tag-select .ant-tag) {
    margin-right: 24px;
    padding: 0 8px;
    font-size: 14px;
  }
}

.ant-pro-pages-list-projects-cardList {
  margin-top: 24px;

  :deep(.ant-card-meta-title) {
    margin-bottom: 4px;
  }

  :deep(.ant-card-meta-description) {
    height: 44px;
    overflow: hidden;
    line-height: 22px;
  }

  .cardItemContent {
    display: flex;
    height: 20px;
    margin-top: 16px;
    margin-bottom: -4px;
    line-height: 20px;

    >span {
      flex: 1 1;
      color: rgba(0, 0, 0, 0.45);
      font-size: 12px;
    }

    :deep(.ant-pro-avatar-list) {
      flex: 0 1 auto;
    }
  }
}
</style>
