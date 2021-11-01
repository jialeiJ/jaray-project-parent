<template>
  <div id="add">
    <el-dialog :visible.sync="addDialogFormVisible" title="新增">
      <el-form :model="addForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-col :span="24">
            <el-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="姓名">
          <el-col :span="24">
            <el-input v-model="addForm.name" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="部门">
          <el-cascader
            v-model="addForm.deptIds"
            :options="deptOptions"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            clearable
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="邮箱">
          <el-col :span="24">
            <el-input v-model="addForm.email" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号">
          <el-col :span="24">
            <el-input v-model="addForm.mobile" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="状态">
          <el-col :span="24">
            <el-select v-model="addForm.status" placeholder="请选择状态">
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
            <el-input v-model="addForm.createBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-col :span="24">
            <el-date-picker
              v-model="addForm.createTime"
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
            <el-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-col :span="24">
            <el-date-picker
              v-model="addForm.lastUpdateTime"
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
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addSysUser">确 定</el-button>
      </div>
    </el-dialog>
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
      addForm: {},
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
      }
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
          that.$message({
            message: '恭喜你，添加成功',
            type: 'success'
          })
          that.addDialogFormVisible = false
          that.addForm = {}
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
