<template>
  <a-form @submit="handleSubmit" :form="form">
    <a-form-item
      label="比较源分支"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
    >
      <a-select>
        <a-select-option :value="0">develop</a-select-option>
        <a-select-option :value="1">release1.0</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item
      label="比较目标分支"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
    >
      <a-select>
        <a-select-option :value="0">develop</a-select-option>
        <a-select-option :value="1">release2.0</a-select-option>
      </a-select>
    </a-form-item>
  </a-form>
</template>

<script>
import pick from 'lodash.pick'

const fields = ['title', 'startAt', 'owner', 'description']

export default {
  name: 'BranchInfo',
  props: {
    record: {
      type: Object,
      default: null
    }
  },
  data () {
    return {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      form: this.$form.createForm(this)
    }
  },
  mounted () {
    this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    onOk () {
      console.log('监听了 modal ok 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    },
    onCancel () {
      console.log('监听了 modal cancel 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    },
    handleSubmit () {
      const { form: { validateFields } } = this
      this.visible = true
      validateFields((errors, values) => {
        if (!errors) {
          console.log('values', values)
        }
      })
    }
  }
}
</script>
