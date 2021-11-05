<template>
  <div id="add">
    <a-modal
      :visible.sync="addDialogFormVisible"
      title="新增"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="addForm">
        <a-form-model-item :label-width="formLabelWidth" label="ID">
          <a-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="姓名">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="部门">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="邮箱">
          <a-input v-model="addForm.email" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="手机号">
          <a-input v-model="addForm.mobile" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="状态">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="创建人">
          <a-input v-model="addForm.createBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="创建时间">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="更新人">
          <a-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="更新时间">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="addDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="addSysUser">确 定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import SYS_USER_API from '@api/api_sys_user'

export default {
  name: 'AddSysUser',
  components: {},
  props: {
    deptOptions: {
      type: Array,
      default: function() {
        return []
      }
    },
    statusOptions: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      addDialogFormVisible: false,
      addForm: {}
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function() {
      const that = this
      that.addDialogFormVisible = true
    },
    closeDialog: function() {
      const that = this
      that.addDialogFormVisible = false
    },
    handleChange: function(value) {
      const that = this
      if (value) {
        that.addForm.deptId = value[value.length - 1]
      }
    },
    addSysUser: function() {
      const that = this
      // 定义请求参数
      const params = that.addForm
      // 调用接口
      SYS_USER_API.addSysUser(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message.success('恭喜你，添加成功')
          that.addDialogFormVisible = false
          that.addForm = {}
        }
      })
    },
    handleOk(e) {
      const that = this
      that.addDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.addDialogFormVisible = false
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-select,.el-cascader {
    display: block;
    position: relative;
}

/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
