<template>
  <div id="add">
    <el-dialog :visible.sync="addDialogFormVisible" title="新增">
      <el-form :model="addForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级机构">
          <el-cascader
            v-model="addForm.deptIds"
            :options="deptOptions"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="addForm.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="addForm.createBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="addForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="addForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addSysDept">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SYS_DEPT_API from '@api/api_sys_dept'

export default {
  name: 'AddSysDept',
  components: {},
  props: {
    deptOptions: {
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
    handleChange(value) {
      const that = this
      if (value) {
        that.addForm.deptId = value[value.length - 1]
      }
      console.log(value)
    },
    addSysDept: function() {
      const that = this
      if (that.addForm.deptIds && that.addForm.deptIds.length > 0) {
        that.addForm.parentId = that.addForm.deptIds[0]
      }
      // 定义请求参数
      const params = that.addForm
      // 调用接口
      SYS_DEPT_API.addSysDept(params).then(function(result) {
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
::v-deep .el-cascader {
    display: block;
    position: relative;
}
</style>
