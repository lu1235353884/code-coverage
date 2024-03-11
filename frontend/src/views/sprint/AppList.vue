<template>
  <a-modal
    title="新建规则11111111"
    :width="520"
    :visible="appVisible"
    :confirmLoading="appLoading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="16" :sm="48">
              <a-form-item label="应用">
                <a-input v-model="queryParam.appcode"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-button type="primary" @click="$refs.appTable.refresh(true)">查询</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <s-table
        ref="appTable"
        size="default"
        showPagination="auto"
        :rowKey="record => record.id"
        :columns="columns"
        :data="loadData"
        :rowSelection="rowSelection"
      ></s-table>
    </a-card>
  </a-modal>
</template>

<script>
import { STable, Ellipsis } from '@/components'
import { getApplist } from '@/api/manage'

const columns = [
  {
    title: '应用名',
    dataIndex: 'appCode'
  },
  {
    title: 'SM',
    dataIndex: 'sm'
  }
]

export default {
  components: {
    STable,
    Ellipsis
  },
  props: {
    appVisible: {
      type: Boolean,
      required: true
    }
  },
  data () {
    this.columns = columns
    this.formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      }
    }
    return {
      selectedRowKeys: [],
      selectedRows: [],
      appLoading: false,
      queryParam: {},
      loadData: parameter => {
        if (!this.queryParam.appcode) {
          this.queryParam.appcode = ''
        }
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        return getApplist(requestParameters)
          .then(res => {
            return res.result
          })
      }
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
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },

    getData () {
      return this.$refs.table._data.localDataSource
    }
  },
  watch: {

  }
}
</script>
