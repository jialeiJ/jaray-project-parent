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
        <a-form-model-item label="用户名">
          <a-input v-model="viewForm.userName" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="用户操作">
          <a-input v-model="viewForm.operation" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="方法">
          <a-input v-model="viewForm.method" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="参数">
          <a-input v-model="viewForm.params" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="IP地址">
          <a-input v-model="viewForm.ip" :disabled="true"/>
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
import LOG_API from '@api/api_sys_log'

export default {
  name: 'ViewSysLog',
  components: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      viewDialogFormVisible: false,
      viewForm: {}
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
      LOG_API.viewSysLog(params).then(function(result) {
        if (result.code === 200) {
          that.viewForm = result.map.sysLog
          that.viewDialogFormVisible = true
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
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
