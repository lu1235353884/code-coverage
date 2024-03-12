<template>
  <div>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-alert showIcon="true">
        <template slot="message">
          <span style="margin-right: 12px">当前冲刺为{{ sprintCode }}</span>
        </template>
      </a-alert>
    </a-card>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-form :form="form" layout="inline">
        <standard-form-row title="区域选择" block style="padding-bottom: 11px;">
          <a-form-item>
            <tag-select>
              <tag-select-option value="App">应用</tag-select-option>
              <tag-select-option value="BM">业务中台</tag-select-option>
            </tag-select>
          </a-form-item>
        </standard-form-row>

      </a-form>
    </a-card>

    <div class="ant-pro-pages-list-applications-filterCardList">
      <a-list
        :loading="loading"
        :data-source="data"
        :grid="{ gutter: 24, xl: 4, lg: 3, md: 3, sm: 2, xs: 1 }"
        style="margin-top: 24px;">
        <a-list-item slot="renderItem" slot-scope="item">
          <a-card :body-style="{ paddingBottom: 20 }" hoverable>
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
              <a-tooltip title="重新生成报告" @click="handleChange(item)">
                <a-icon type="reload" />
              </a-tooltip>
              <a-dropdown>
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
              </a-dropdown>
            </template>
            <div class="">
              <card-info active-user="100" new-user="999"></card-info>
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
import { getCompareSprint, getSprintRels, getAppBranch, excuteTask } from '@/api/manage'

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
      sprintCode: '',
      sprintId: null,
      form: this.$form.createForm(this),
      loading: true
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
      this.sprintCode = res.result.sprintCode
      this.sprintId = res.result.id
      this.getList()
    })
  },
  methods: {
    handleChange (record) {
      const relid = record.id
      const appcode = record.appCode

      getAppBranch(relid, appcode).then(res => {
        const rtn = res.result
        // 无回参或回参不含ID或回参的compareTpye为all
        if (!rtn || (rtn && !rtn.id) || (rtn && rtn.compareTpye === 'all')) {
          var that = this
          this.$confirm({
              title: '警告',
              content: '当前项目未配置分支信息，只能执行全量比对，是否执行？',
              okText: '确定',
              okType: 'danger',
              cancelText: '取消',
              onOk () {
                that.exceteTask(null, relid, appcode)
              },
              onCancel () {
                console.log('Cancel')
              }
            })
        } else {
          this.exceteTask(rtn.id, relid, appcode)
        }
      })
    },
    exceteTask (id, relid, appcode) {
      var that = this
      Modal.confirm({
        title: this.$t('请确认是否重新计算覆盖率'),
        content: this.$t('请确认是否重新计算覆盖率'),
        onOk: () => {
          excuteTask(id, relid, appcode).then(res => {
            that.$message.info('任务开始执行，请稍后查看执行进度')
          })
        },
        onCancel () { }
      })
    },
    showDetail (record) {
      this.$dialog(StepDetail,
        // component props
        {
          record: record,
          on: {
            ok () {
              console.log('ok 回调')
            },
            cancel () {
              console.log('cancel 回调')
            },
            close () {
              console.log('modal close 回调')
            }
          }
        },
        // modal props
        {
          title: '操作',
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
          title: '操作',
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
      getSprintRels(this.sprintId).then(res => {
        this.data = res.result
        this.loading = false
      })
    },
    closeDialog () {
      this.$dialog.close()
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
