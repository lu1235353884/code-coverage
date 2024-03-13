<template>
  <a-modal
    title="新建冲刺"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <!-- 检查是否有 id 并且大于0，大于0是修改。其他是新增，新增不显示主键ID -->
        <a-row>
          <a-col :md="6" :sm="6">
            <a-form-item label="冲刺">
              <a-input :readOnly="isReadOnly" :disabled="isReadOnly" v-decorator="['sprintCode', {rules: [{required: true, message: '冲刺必填！'}]}]" />
            </a-form-item>
          </a-col>
          <a-col :md="16" :sm="16">
            <a-form-item label="是否生效">
              <a-checkbox v-model="isCompare"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <a-alert showIcon="true">
              <template slot="message">
                <span style="margin-right: 12px">默认带入全部应用</span>
              </template>
            </a-alert>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <a-alert showIcon="true">
              <template slot="message">
                <span style="margin-right: 12px">同一时间只可有一个冲刺生效，若当前冲刺生效，则其他冲刺均会自动失效</span>
              </template>
            </a-alert>
          </a-col>
        </a-row>
        <!-- <a-row>
          <a-col>
            <a-button type="primary" @click="handleAdd">选择应用</a-button>
            <a-button style="margin-left: 8px" type="primary" @click="deleteRow">删除</a-button>
          </a-col>
        </a-row> -->
        <!-- <s-table
          ref="table"
          size="default"
          :rowKey="record => record.id"
          :columns="columns"
          :data="loadData"
          showPagination="auto"
        ></s-table> -->
      </a-form>
    </a-spin>
    <!-- <app-list
      ref="AppList"
      :appVisible="appVisible"
      @cancel="handleCancel"
      @ok="handleOk"
    /> -->
  </a-modal>
</template>

<script>
import { STable, Ellipsis } from '@/components'
// import { getSprintRels } from '@/api/manage'

// import AppList from './AppList'

// 表单字段
const fields = ['sprintCode', 'isCompare']

const columns = [
  {
    title: '应用名',
    dataIndex: 'appCode'
  },
  {
    title: '应用id',
    dataIndex: 'appId',
    colSpan: 0,
    customRender: (value, row, index) => {
      const obj = {
          children: value,
          attrs: {}
      }

      obj.attrs.colSpan = 0
      return obj
    }
  }
]

export default {
  components: {
    STable,
    Ellipsis
  },
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    curIndex: {
      type: Number,
      default: () => null
    },
    sprintcode: {
      type: String,
      default: () => ''
    },
    isCompared: {
      type: Boolean,
      default: () => false
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
      form: this.$form.createForm(this),
      selectedRowKeys: [],
      selectedRows: [],
      isReadOnly: false,
      isCompare: this.isCompared,
      sprintCode: this.sprintcode,
      appVisible: false
      // loadData: parameter => {
      //   this.isCompare = this.isCompared
      //   this.sprintCode = this.sprintcode
      //   if (!this.curIndex) {
      //     this.isReadOnly = false
      //     return null
      //   }
      //   this.isReadOnly = true
      //   this.queryParam = {
      //     'sprintid': this.curIndex
      //   }
      //   const requestParameters = Object.assign({}, parameter, this.queryParam)
      //   return getSprintRels(requestParameters)
      //     .then(res => {
      //       return res.result
      //     })
      // }
    }
  },
  created () {
    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))

    // 当 model 发生改变时，为表单设置值
    this.$watch('curIndex', () => {
      this.isCompare = this.isCompared
      this.sprintCode = this.sprintcode
      this.form.setFieldsValue({
        sprintCode: this.sprintCode,
        isCompare: this.isCompare
      })
    })
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
    deleteRow () {
      if (!this.selectedRowKeys) {
        return
      }
      const ls = this.$refs.table._data.localDataSource
      this.$refs.table._data.localDataSource = ls.filter(l => this.selectedRowKeys.findIndex(j => j === l.id))
    },
    getData () {
      const sprintCode = this.form.getFieldValue('sprintCode')
      return {
        'sprintCode': sprintCode,
        'isCompare': this.isCompare
      }
    }
  },
  watch: {
    curIndex () {
      if (this.curIndex) {
        this.isReadOnly = true
      } else {
        this.isReadOnly = false
      }
      // this.$refs.table.loadData()
    }
  }
}
</script>
