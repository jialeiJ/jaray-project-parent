<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="类型">
          <el-radio-group v-model="viewForm.type" disabled>
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="viewForm.id" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级菜单">
          <el-cascader
            v-model="viewForm.menuPids"
            :options="dirTreeData"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            clearable
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="viewForm.name" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="链接">
          <el-input v-model="viewForm.url" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="图标">
          <el-input v-model="viewForm.icon" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="排序">
          <el-input v-model="viewForm.orderNum" readonly="readonly"/>
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
import MENU_API from '@api/api_sys_menu'

export default {
  name: 'ViewSysMenu',
  components: {},
  props: {
    dirTreeData: {
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
      tileMenuData: [],
      // 菜单ids,用于回显
      menuIds: [],
      typeOptions: []
    }
  },
  created: function() {

  },
  mounted: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      MENU_API.viewSysMenu(params).then(function(result) {
        if (result.code === 200) {
          that.viewForm = result.map.sysMenu

          // 获取部门id的父id
          const id = result.map.sysMenu.parentId
          that.menuIds = []
          that.getAllPidById(id, that.tileMenuData)

          // 回显上层机构
          const menuIds = that.menuIds.reverse()
          that.viewForm.menuPids = menuIds

          that.viewDialogFormVisible = true
        }
      })
    },
    getAllPidById: function(id, tileMenuData) {
      const that = this
      if (id && that.menuIds.indexOf(id) === -1) {
        that.menuIds.push(id)
      }
      tileMenuData.forEach(function(item, index) {
        if (item.id === id) {
          that.getAllPidById(item.parentId, tileMenuData)
        }
      })
    },
    handleChange(value) {
      const that = this
      that.parentId = value[value.length - 1]
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
