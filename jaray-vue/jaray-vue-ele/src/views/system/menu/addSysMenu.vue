<template>
  <div id="add">
    <el-dialog :visible.sync="addDialogFormVisible" title="新增">
      <el-form :model="addForm">
        <el-form-item :label-width="formLabelWidth" label="类型">
          <el-radio-group v-model="addForm.type">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="上级菜单">
          <el-cascader
            v-model="addForm.menuPids"
            :options="dirTreeData"
            :props="{ expandTrigger: 'hover', checkStrictly: true }"
            clearable
            @change="handleChange"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="名称">
          <el-input v-model="addForm.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item v-if="addForm.type == 1" :label-width="formLabelWidth" label="菜单路径">
          <el-input v-model="addForm.url" :disabled="addForm.type == 0?true:false" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="图标">
          <el-input v-model="addForm.icon" autocomplete="off"/>
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
        <el-button type="primary" @click="addSysMenu">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import MENU_API from '@api/api_sys_menu'

export default {
  name: 'AddSysMenu',
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
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
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
    openDialog: function() {
      const that = this
      that.addDialogFormVisible = true
    },
    addSysMenu: function() {
      const that = this
      // 目录/菜单父id赋值
      if (that.addForm.menuPids) {
        that.addForm.parentId = that.addForm.menuPids[that.addForm.menuPids.length - 1]
      }

      // 定义请求参数
      const params = that.addForm
      // 调用接口
      MENU_API.addSysMenu(params).then(function(result) {
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
    },
    handleChange(value) {
      const that = this
      that.parentId = value[value.length - 1]
    },
    getDirTreeData: function(dirTreeData) {
      const that = this
      for (var i = 0; i < dirTreeData.length; i++) {
        if (dirTreeData[i].children && dirTreeData[i].children.length) {
          const data = dirTreeData[i].children.filter(function(item) {
            return item.type === 0
          })

          if (data && data.length) {
            dirTreeData[i].children = data
          } else {
            // 删除children属性
            that.$delete(dirTreeData[i], 'children')
          }

          if (data.length) {
            that.getDirTreeData(dirTreeData[i].children)
          } else {
            continue
          }
        }
      }
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
