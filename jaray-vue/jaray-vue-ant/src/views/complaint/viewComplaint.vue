<template>
  <div id="view">
    <a-modal
      :visible.sync="viewDialogFormVisible"
      title="详情"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form :model="form">
        <a-form-item :label-width="formLabelWidth" label="标题">
          <a-input v-model="form.title" read-only/>
        </a-form-item>
        <a-form-item :label-width="formLabelWidth" label="描述">
          <a-input v-model="form.desc" read-only/>
        </a-form-item>
        <a-form-item :label-width="formLabelWidth" label="描述">
          <a-input v-model="form.desc" type="textarea" read-only/>
        </a-form-item>
      </a-form>
      <div slot="footer" class="dialog-footer">
        <a-button type="primary" @click="viewDialogFormVisible = false">关闭</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import COMPLAINT_API from '@api/api_complaint'

export default {
  name: 'ViewComplaint',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      viewDialogFormVisible: false,
      form: {

      }
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
      COMPLAINT_API.viewComplaint(params).then(function(result) {
        if (result.code === 200) {
          that.form = result.map.complaint
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
