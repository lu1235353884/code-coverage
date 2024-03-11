<template>
  <a-modal
    title="新建应用"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <a-row>
          <a-col :md="10" :sm="10">
            <a-form-item label="应用名">
              <a-input :readOnly="isReadOnly" :disabled="isReadOnly" v-decorator="['appCode', {rules: [{required: true, message: '应用名必填！'}]}]" />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="10">
            <a-form-item label="归属方">
              <a-select v-decorator="['owner']">
                <a-select-option value="App">应用</a-select-option>
                <a-select-option value="BM">业务中台</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md="10" :sm="10">
            <a-form-item label="SM">
              <a-input v-decorator="['sm']" />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="10">
            <a-form-item label="联系电话">
              <a-input v-decorator="['phoneNumber']" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { STable, Ellipsis } from '@/components'
// import { getSprintRels } from '@/api/manage'

// import AppList from './AppList'

const fields = ['appCode', 'owner', 'sm', 'phoneNumber']

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
    appCode: {
      type: String,
      default: () => ''
    },
    owner: {
      type: String,
      default: () => ''
    },
    sm: {
      type: String,
      default: () => ''
    },
    phoneNumber: {
      type: String,
      default: () => ''
    }
  },
  data () {
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
      appVisible: false
    }
  },
  created () {
    fields.forEach(v => this.form.getFieldDecorator(v))
  },
  computed: {
  },
  methods: {
    getData () {
      const appCode = this.form.getFieldValue('appCode')
      const owner = this.form.getFieldValue('owner')
      const sm = this.form.getFieldValue('sm')
      const phoneNumber = this.form.getFieldValue('phoneNumber')
      return {
        'appCode': appCode,
        'owner': owner,
        'sm': sm,
        'phoneNumber': phoneNumber
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
      this.form.setFieldsValue({
        appCode: this.appCode,
        owner: this.owner,
        sm: this.sm,
        phoneNumber: this.phoneNumber
      })
    }
  }
}
</script>
