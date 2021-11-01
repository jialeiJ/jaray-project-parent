<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="viewForm.id" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级机构">
          <el-cascader
            v-model="viewForm.deptIds"
            :options="deptOptions"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="viewForm.name" autocomplete="off" placeholder="自动生成ID，无需填写"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="viewForm.createBy" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="viewForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="viewForm.lastUpdateBy" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="viewForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import API from '@api/api_sys_dept'

export default {
  name: 'ViewSysDept',
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
      // 部门ids,用于回显
      deptIds: []
    }
  },
  created: function() {

  },
  methods: {
    handleChange(value) {
      console.log(value)
    },
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      API.viewSysDept(params).then(function(result) {
        if (result.code === 200) {
          that.viewForm = result.map.sysDept

          // 获取部门id的父id
          const id = result.map.sysDept.id
          that.deptIds = []
          that.getAllPidById(id, that.tileDeptData)
          // 删除本机构,用于回显上层机构
          const deptIds = that.deptIds.reverse()
          deptIds.splice(that.deptIds.length - 1, 1)
          that.viewForm.deptIds = deptIds

          that.viewDialogFormVisible = true
        }
      })
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
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-cascader {
    display: block;
    position: relative;
}
</style>
