<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="editForm.id" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="姓名">
          <el-input v-model="editForm.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="部门">
          <el-cascader
            v-model="editForm.deptIds"
            :options="deptOptions"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="角色">
          <el-col :span="24">
            <el-select v-model="roleIds" multiple placeholder="请选择角色">
              <el-option
                v-for="item in sysRoleList"
                :key="item.value"
                :label="item.label"
                :value="item.value"/>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="邮箱">
          <el-input v-model="editForm.email" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号">
          <el-input v-model="editForm.mobile" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="状态">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="editForm.createBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="editForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="editForm.lastUpdateBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="editForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editSysUser">确 定</el-button>
      </div>
    </el-dialog>
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
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            picker.$emit('pick', date)
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', date)
          }
        }]
      },
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
