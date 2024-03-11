<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="应用">
                <a-input v-model="queryParam.appcode" placeholder=""/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class="table-operator">
        <a-button type="primary" icon="plus" @click="handleAdd">新建</a-button>
        <a-button type="primary" icon="delete" @click="handleDels">删除</a-button>
      </div>

      <s-table
        ref="table"
        size="default"
        rowKey="id"
        :columns="columns"
        :data="loadData"
        :rowSelection="rowSelection"
        showPagination="auto"
      >
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
        <span slot="status" slot-scope="text">
          <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
        </span>
        <span slot="description" slot-scope="text">
          <ellipsis :length="4" tooltip>{{ text }}</ellipsis>
        </span>

        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleEdit(record)">修改</a>
            <a-divider type="vertical" />
            <a @click="handleDel(record.id)">删除</a>
          </template>
        </span>
      </s-table>

      <create-form
        ref="createModal"
        :visible="visible"
        :loading="confirmLoading"
        :curIndex="curIndex"
        :appCode="appcode"
        :owner="owner"
        :sm="sm"
        :phoneNumber="phoneNumber"
        @cancel="handleCancel"
        @ok="handleOk"
      />
      <step-by-step-modal ref="modal" @ok="handleOk"/>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import moment from 'moment'
import { STable, Ellipsis } from '@/components'
import { getApplist, saveApp, deleteApp } from '@/api/manage'

import CreateForm from './CreateForm'

const columns = [
  {
    title: '应用名',
    dataIndex: 'appCode'
  },
  {
    title: '归属方',
    dataIndex: 'owner',
    customRender: function (text) {
      if (text === 'App') {
        return '应用'
      } else if (text === 'BM') {
        return '业务中台'
      } else {
        return ''
      }
    }
  },
  {
    title: 'SM',
    dataIndex: 'sm'
  },
  {
    title: '电话号码',
    dataIndex: 'phoneNumber'
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: '150px',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  name: 'TableList',
  components: {
    STable,
    Ellipsis,
    CreateForm
  },
  data () {
    this.columns = columns
    return {
      // create model
      visible: false,
      confirmLoading: false,
      curIndex: null,
      appcode: null,
      owner: null,
      sm: null,
      phoneNumber: null,
      // 查询参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        this.queryParam.appcode = this.$praseStrEmpty(this.queryParam.appcode)
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        return getApplist(requestParameters)
          .then(res => {
            return res.result
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  created () {
  },
  computed: {
    rowSelection () {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange
      }
    }
  },
  methods: {
    handleAdd () {
      this.curIndex = null
      this.appcode = null
      this.owner = null
      this.sm = null
      this.phoneNumber = null
      this.visible = true
    },
    handleEdit (record) {
      this.visible = true
      this.curIndex = record.id
      this.appcode = record.appCode
      this.owner = record.owner
      this.sm = record.sm
      this.phoneNumber = record.phoneNumber
    },
    handleOk () {
      const values = this.$refs.createModal.getData()
      this.confirmLoading = true
      const params = {}
      if (this.curIndex) {
        params['id'] = this.curIndex
      }
      params['appCode'] = values.appCode
      params['owner'] = values.owner
      params['sm'] = values.sm
      params['phoneNumber'] = values.phoneNumber
      saveApp(params).then(res => {
        if (res.success) {
          this.$refs.table.refresh()
          this.$message.info('修改成功')
        } else {
          this.$message.info('修改失败')
        }
      })
      this.curIndex = 0
      this.confirmLoading = false
      this.visible = false
    },
    handleCancel () {
      this.visible = false
    },
    handleDels () {
      if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
        const keys = this.selectedRowKeys.join(', ')
        this.handleDel(keys)
      }
    },
    handleDel (id) {
      var that = this
      that.$confirm({
        title: '警告',
        content: '是否删除？',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        onOk () {
          deleteApp(id).then(res => {
            if (res.success) {
              that.$refs.table.refresh()
              that.$message.info('删除成功')
            } else {
              that.$message.error('删除失败')
            }
          })
        },
        onCancel () {
          console.log('Cancel')
        }
      })
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    resetSearchForm () {
      this.queryParam = {
        date: moment(new Date())
      }
    }
  }
}
</script>
