<template>
  <a-form @submit="handleSubmit" :form="form">
    <a-form-item
      label="源分支"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
    >
      <a-select :loading="loading" v-model="sourceBranch">
        <a-select-option v-for="item of sourceList" :value="item.branch" :key="item.branch">{{ item.branch }}</a-select-option>
        <!-- <a-select-option :value="0">develop</a-select-option>
        <a-select-option :value="1">release1.0</a-select-option> -->
      </a-select>
    </a-form-item>
    <a-form-item
      label="目标分支"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
    >
      <a-select :loading="loading" v-model="targetBranch">
        <a-select-option v-for="item of targetList" :value="item.branch" :key="item.branch">{{ item.branch }}</a-select-option>
      </a-select>
    </a-form-item>
  </a-form>
</template>

<script>
import { getBranchlist, getAppBranch, saveAppBranch } from '@/api/manage'

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
      sourceList: [],
      targetList: [],
      id: 0,
      sprintId: this.record.sprintId,
      relid: this.record.id,
      appcode: this.record.appCode,
      sourceBranch: '',
      targetBranch: '',
      form: this.$form.createForm(this),
      loading: false
    }
  },
  mounted () {
    this.loading = true
    if (this.relid && this.appcode) {
      getAppBranch(this.relid, this.appcode).then(res => {
        const rtn = res.result
        if (rtn) {
          this.id = rtn.id
          this.targetBranch = rtn.targetBranchName
          this.sourceBranch = rtn.sourceBranchName
        }
      })
    }
    getBranchlist(this.appcode).then(res => {
      this.loading = false
      const list = res.result
      this.targetList = Object.assign([], list)
      this.sourceList = Object.assign([], list)
    })
    // this.record && this.form.setFieldsValue(pick(this.record, fields))
  },
  methods: {
    onOk () {
      if (!this.sourceBranch || !this.targetBranch) {
        this.$message.error('请选择分支')
        return
      }
      const param = {
        id: this.id,
        sprintId: this.sprintId,
        relId: this.relid,
        appCode: this.appcode,
        compareType: 'branch',
        sourceBranchName: this.sourceBranch,
        targetBranchName: this.targetBranch
      }
      saveAppBranch(param).then(res => {
        this.$message.info('保存成功')
        this._provided.dialogContext.close()
      })
    },
    onCancel () {
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
