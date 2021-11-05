<template>
  <div id="add">
    <a-modal
      :visible.sync="addDialogFormVisible"
      title="新增"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="addForm" :label-col="labelCol" :wrapper-col="wrapperCol" layout="horizontal">
        <a-form-model-item label="类型">
          <a-radio-group v-model="addForm.type">
            <a-radio :value="0">目录</a-radio>
            <a-radio :value="1">菜单</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="ID">
          <a-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写"/>
        </a-form-model-item>
        <a-form-model-item label="上级菜单">
          <a-cascader
            v-model="menuPids"
            :options="dirTreeData"
            change-on-select
            clearable
            @change="handleChange"/>
        </a-form-model-item>
        <a-form-model-item label="名称">
          <a-input v-model="addForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item v-if="addForm.type == 1" label="菜单路径">
          <a-input v-model="addForm.url" :disabled="addForm.type == 0?true:false" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="图标">
          <a-input v-model="addForm.icon" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="创建人">
          <a-input v-model="addForm.createBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="创建时间">
          <a-date-picker
            v-model="addForm.createTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
        <a-form-model-item label="更新人">
          <a-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="更新时间">
          <a-date-picker
            v-model="addForm.lastUpdateTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="addDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="addSysMenu">确 定</a-button>
      </div>
    </a-modal>
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
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      addDialogFormVisible: false,
      addForm: {},
      tileMenuData: [],
      // 菜单ids,用于回显
      menuIds: [],
      menuPids: [],
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
      if (that.menuPids) {
        that.parentId = that.menuPids[that.menuPids.length - 1]
      }

      // 定义请求参数
      const params = that.addForm
      // 调用接口
      MENU_API.addSysMenu(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message.success('恭喜你，添加成功')
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
    },
    handleOk(e) {
      const that = this
      that.addDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.addDialogFormVisible = false
    },
    onChange(value, dateString) {
    },
    onOk(value) {
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .ant-modal .ant-modal-body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}

.code-box-demo .ant-select {
  margin: 0 8px 10px 0;
}

.ant-row-rtl .code-box-demo .ant-select {
  margin: 0 0 10px 8px;
}

#components-select-demo-search-box .code-box-demo .ant-select {
  margin: 0;
}
</style>
