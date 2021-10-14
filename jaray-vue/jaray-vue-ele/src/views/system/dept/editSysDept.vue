<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="editForm.id" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级机构">
          <el-cascader
            v-model="editForm.deptIds"
            :options="deptOptions"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="editForm.name" autocomplete="off" placeholder="自动生成ID，无需填写"/>
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
        <el-button type="primary" @click="editSysDept">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '@api/api_sys_dept'

export default {
  name: 'EditSysDept',
  components: {},
  props: {
    deptOptions: {
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
      // 部门ids,用于回显
      deptIds: []
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      API.viewSysDept(params).then(function(result) {
        if (result.code === 200) {
          that.editForm = result.map.sysDept

          // 获取部门id的父id
          const id = result.map.sysDept.id
          that.deptIds = []
          that.getAllPidById(id, that.tileDeptData)
          // 删除本机构,用于回显上层机构
          const deptIds = that.deptIds.reverse()
          deptIds.splice(that.deptIds.length - 1, 1)
          that.editForm.deptIds = deptIds

          that.editDialogFormVisible = true
        }
      })
    },
    handleChange(value) {
      const that = this
      console.log(value)
      console.log(that.tileDeptData)
    },
    getAllPidById: function(id, tileDeptData) {
      const that = this
      if (id) {
        that.deptIds.push(id)
      }
      tileDeptData.forEach(function(item, index) {
        if (item.id === id) {
          that.getAllPidById(item.parentId, tileDeptData)
        }
      })
    },
    editSysDept: function() {
      const that = this
      if (that.editForm.deptIds && that.editForm.deptIds.length > 0) {
        that.editForm.parentId = that.editForm.deptIds[0]
      }
      // 删除children属性
      that.$delete(this.editForm, 'children')
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      API.updateSysDept(params).then(function(result) {
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
::v-deep .el-cascader {
    display: block;
    position: relative;
}
</style>
