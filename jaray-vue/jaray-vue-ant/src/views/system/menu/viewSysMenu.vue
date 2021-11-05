<template>
  <div id="view">
    <a-modal
      :visible.sync="viewDialogFormVisible"
      title="详情"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="viewForm" :label-col="labelCol" :wrapper-col="wrapperCol" layout="horizontal">
        <a-form-model-item label="类型">
          <a-radio-group v-model="viewForm.type" disabled>
            <a-radio :value="0">目录</a-radio>
            <a-radio :value="1">菜单</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="ID">
          <a-input v-model="viewForm.id" disabled/>
        </a-form-model-item>
        <a-form-model-item label="上级菜单">
          <a-cascader
            v-model="menuPids"
            :options="dirTreeData"
            disabled
            change-on-select
            clearable
            @change="handleChange"/>
        </a-form-model-item>
        <a-form-model-item label="名称">
          <a-input v-model="viewForm.name" disabled/>
        </a-form-model-item>
        <a-form-model-item label="链接">
          <a-input v-model="viewForm.url" disabled/>
        </a-form-model-item>
        <a-form-model-item label="图标">
          <a-input v-model="viewForm.icon" disabled/>
        </a-form-model-item>
        <a-form-model-item label="排序">
          <a-input v-model="viewForm.orderNum" disabled/>
        </a-form-model-item>
        <a-form-model-item label="创建人">
          <a-input v-model="viewForm.createBy" disabled/>
        </a-form-model-item>
        <a-form-model-item label="创建时间">
          <a-date-picker
            :disabled="true"
            v-model="viewForm.createTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
        <a-form-model-item label="更新人">
          <a-input v-model="viewForm.lastUpdateBy" disabled/>
        </a-form-model-item>
        <a-form-model-item label="更新时间">
          <a-date-picker
            :disabled="true"
            v-model="viewForm.lastUpdateTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button type="primary" @click="viewDialogFormVisible = false">关闭</a-button>
      </div>
    </a-modal>
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
    },
    tileMenuData: {
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
      viewDialogFormVisible: false,
      viewForm: {},
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
          that.menuPids = menuIds

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
    },
    handleOk(e) {
      const that = this
      that.viewDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.viewDialogFormVisible = false
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
