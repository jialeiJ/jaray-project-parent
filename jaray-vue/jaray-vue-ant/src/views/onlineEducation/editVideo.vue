<template>
  <div id="edit">
    <a-modal
      :visible.sync="editDialogFormVisible"
      title="编辑"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="editForm">
        <a-form-model-item :label-width="formLabelWidth" label="ID">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="姓名">
          <a-input v-model="editForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="部门">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="角色">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="邮箱">
          <a-input v-model="editForm.email" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="手机号">
          <a-input v-model="editForm.mobile" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="状态">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="创建人">
          <a-input v-model="editForm.createBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="创建时间">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="更新人">
          <a-input v-model="editForm.lastUpdateBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="更新时间">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="editDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="editSysUser">确 定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import SYS_USER_API from '@api/api_sys_user'

export default {
  name: 'EditSysUser',
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
    },
    sysRoleList: {
      type: Array,
      default: function() {
        return []
      }
    },
    tileDeptData: {
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
      editDialogFormVisible: false,
      editForm: {},
      deptIds: [],
      roleIds: []
    }
  },
  created: function() {

  },
  methods: {
    handleChange: function(value) {
      const that = this
      if (value) {
        that.editForm.deptIId = value[value.length - 1]
      }
    },
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      SYS_USER_API.viewSysUserById(params).then(function(result) {
        if (result.code === 200) {
          that.editForm = result.map.sysUser

          // 回显角色
          that.roleIds = []
          const roleIds = result.map.sysUser.roleId.split(',')
          for (var i = 0; i < roleIds.length; i++) {
            that.roleIds.push(roleIds[i])
          }

          // 获取部门id的父id
          const id = result.map.sysUser.deptId
          that.deptIds = []
          that.getAllPidById(id, that.tileDeptData)
          // 回显上层机构
          const deptIds = that.deptIds.reverse()
          that.editForm.deptIds = deptIds

          that.editDialogFormVisible = true
        }
      })
    },
    getAllPidById: function(id, tileDeptData) {
      const that = this
      if (id && that.deptIds.indexOf(id) === -1) {
        that.deptIds.push(id)
      }
      tileDeptData.forEach(function(item, index) {
        if (item.id === id) {
          that.getAllPidById(item.parentId, tileDeptData)
        }
      })
    },
    editSysUser: function() {
      const that = this
      that.editForm.deptId = that.editForm.deptIds[(that.editForm.deptIds.length - 1)]
      that.editForm.roleId = that.roleIds.join(',')
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      SYS_USER_API.updateSysUserById(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，编辑成功',
            type: 'success'
          })
          that.editDialogFormVisible = false
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
