<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-col :span="24">
            <el-input v-model="viewForm.id" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="姓名">
          <el-col :span="24">
            <el-input v-model="viewForm.name" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="部门">
          <el-cascader
            v-model="viewForm.deptIds"
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
          <el-col :span="24">
            <el-input v-model="viewForm.email" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号">
          <el-col :span="24">
            <el-input v-model="viewForm.mobile" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="状态">
          <el-col :span="24">
            <el-select v-model="viewForm.status" placeholder="请选择状态">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"/>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-col :span="24">
            <el-input v-model="viewForm.createBy" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-col :span="24">
            <el-date-picker
              v-model="viewForm.createTime"
              :picker-options="pickerOptions"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-col :span="24">
            <el-input v-model="viewForm.lastUpdateBy" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-col :span="24">
            <el-date-picker
              v-model="viewForm.lastUpdateTime"
              :picker-options="pickerOptions"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SYS_USER_API from '@api/api_sys_user'

export default {
  name: 'ViewSysUser',
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
      viewDialogFormVisible: false,
      viewForm: {},
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
      roleIds: [],
      // 部门ids,用于回显
      deptIds: []
    }
  },
  created: function() {

  },
  methods: {
    handleChange(value) {
      const that = this
      if (value) {
        that.viewForm.deptId = value[value.length - 1]
      }
    },
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      SYS_USER_API.viewSysUserById(params).then(function(result) {
        if (result.code === 200) {
          that.viewForm = result.map.sysUser

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
          that.viewForm.deptIds = deptIds

          that.viewDialogFormVisible = true
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
