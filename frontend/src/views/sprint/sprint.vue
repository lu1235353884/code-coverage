<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="冲刺">
                <a-input v-model="code" placeholder=""/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => this.code = null">重置</a-button>
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
        :sprintcode="sprintcode"
        :isCompared="isCompared"
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
import { getSprintList, saveSprint, deleteSprint } from '@/api/manage'

import CreateForm from './CreateForm'

const columns = [
  {
    title: '冲刺',
    dataIndex: 'sprintCode'
  },
  {
    title: '是否执行比对',
    dataIndex: 'isCompare',
    customRender: function (text) {
      if (text === 1) {
        return '是'
      } else {
        return '否'
      }
    }
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
      sprintcode: null,
      isCompared: null,
      // 查询参数
      code: null,
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        this.code = this.$praseStrEmpty(this.code)
        const queryParam = { 'sprintcode': this.code }
        const requestParameters = Object.assign({}, parameter, queryParam)
        return getSprintList(requestParameters)
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
      this.sprintcode = null
      this.isCompared = false
      this.visible = true
    },
    handleEdit (record) {
      this.visible = true
      this.curIndex = record.id
      this.sprintcode = record.sprintCode
      this.isCompared = record.isCompare === 1
    },
    handleOk () {
      const values = this.$refs.createModal.getData()
      console.log(values)
      this.confirmLoading = true
      const params = {}
      if (this.curIndex) {
        params['id'] = this.curIndex
      }
      params['sprintCode'] = values.sprintCode
      params['isCompare'] = values.isCompare ? 1 : 0
      saveSprint(params).then(res => {
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
          deleteSprint(id).then(res => {
            if (res.success) {
              that.$message.info('删除成功')
              that.$refs.table.refresh()
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
