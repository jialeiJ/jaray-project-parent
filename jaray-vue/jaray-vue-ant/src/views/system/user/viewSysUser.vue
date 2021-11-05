<template>
  <div id="view">
    <a-modal
      :visible.sync="viewDialogFormVisible"
      title="详情"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="viewForm" :label-col="labelCol" :wrapper-col="wrapperCol" layout="horizontal">
        <a-form-model-item label="ID">
          <a-input v-model="viewForm.id" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="姓名">
          <a-input v-model="viewForm.name" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="部门">
          <a-cascader
            :disabled="true"
            v-model="deptIds"
            :options="deptOptions"
            change-on-select
            clearable
            @change="handleChange"/>
        </a-form-model-item>
        <a-form-model-item label="角色">
          <a-select v-model="roleIds" :disabled="true" mode="tags">
            <a-select-option
              v-for="item in sysRoleList"
              :key="item.value"
              :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="邮箱">
          <a-input v-model="viewForm.email" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="手机号">
          <a-input v-model="viewForm.mobile" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="状态">
          <a-select v-model="viewForm.status" :disabled="true">
            <a-select-option
              v-for="item in statusOptions"
              :key="item.value"
              :title="item.label"
              :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="创建人">
          <a-input v-model="viewForm.createBy" :disabled="true"/>
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
          <a-input v-model="viewForm.lastUpdateBy" :disabled="true"/>
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
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      viewDialogFormVisible: false,
      viewForm: {},
      roleIds: [],
      // 部门ids,用于回显
      deptIds: []
    }
  },
  watch: {
    // 使用监听
    statusOptions: {
      handler(newVal, oldVal) {
        if (newVal) {
          console.log(newVal, oldVal)
        }
      },
      immediate: true, // 立即执行
      deep: true // 深度监听
    }
  },
  created: function() {
  },
  mounted: function() {
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
          that.deptIds = deptIds

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
