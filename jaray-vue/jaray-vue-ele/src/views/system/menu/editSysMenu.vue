<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="类型">
          <el-radio-group v-model="editForm.type">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="editForm.id" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级菜单">
          <el-cascader
            v-model="editForm.menuPids"
            :options="dirTreeData"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            clearable
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="editForm.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="类型">
          <el-input v-model="editForm.type" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="链接">
          <el-input v-model="editForm.url" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="图标">
          <el-input v-model="editForm.icon" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="排序">
          <el-input v-model="editForm.orderNum" autocomplete="off"/>
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
        <el-button type="primary" @click="editSysMenu">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import MENU_API from '@api/api_sys_menu'

export default {
  name: 'EditSysMenu',
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
          that.editForm = result.map.sysMenu

          // 获取部门id的父id
          const id = result.map.sysMenu.parentId
          that.menuIds = []
          that.getAllPidById(id, that.tileMenuData)

          // 回显上层机构
          const menuIds = that.menuIds.reverse()
          that.editForm.menuPids = menuIds

          that.editDialogFormVisible = true
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
    editSysMenu: function() {
      const that = this
      // 删除该属性
      delete that.editForm.childMenus
      delete that.editForm.perms

      // 目录/菜单父id赋值
      that.editForm.parentId = that.editForm.menuPids[that.editForm.menuPids.length - 1]
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      MENU_API.updateSysMenu(params).then(function(result) {
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
    handleChange(value) {
      const that = this
      that.parentId = value[value.length - 1]
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
::v-deep .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
