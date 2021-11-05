<template>
  <div id="add">
    <a-modal
      :visible.sync="addDialogFormVisible"
      title="新增"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form :model="form">
        <a-form-item :label-width="formLabelWidth" label="标题">
          <a-input v-model="form.title" autocomplete="off"/>
        </a-form-item>
        <a-form-item :label-width="formLabelWidth" label="描述">
          <a-input v-model="form.desc" autocomplete="off"/>
        </a-form-item>
        <a-form-item :label-width="formLabelWidth" label="活动区域">
          <a-select v-model="form.region" placeholder="请选择活动区域">
            <a-option label="区域一" value="shanghai"/>
            <a-option label="区域二" value="beijing"/>
          </a-select>
        </a-form-item>
      </a-form>
      <div slot="footer" class="dialog-footer">
        <a-button @click="addDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="addComplaint">确 定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import COMPLAINT_API from '@api/api_complaint'

export default {
  name: 'AddComplaint',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      addDialogFormVisible: false,
      form: {

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
    addComplaint: function() {
      const that = this
      // 定义请求参数
      const params = that.form
      // 调用接口
      COMPLAINT_API.addComplaint(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，添加成功',
            type: 'success'
          })
          that.addDialogFormVisible = false
        }
      })
    },
    handleOk(e) {
      const that = this
      that.addDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.addDialogFormVisible = false
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
