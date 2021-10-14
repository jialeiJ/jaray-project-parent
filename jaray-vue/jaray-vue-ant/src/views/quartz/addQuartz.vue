<template>
  <div id="add">
    <a-modal
      :visible.sync="addDialogFormVisible"
      title="新增"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="addForm">
        <a-form-model-item label="任务实现类">
          <a-input v-model="addForm.className"/>
        </a-form-model-item>
        <a-form-model-item label="任务名称">
          <a-input v-model="addForm.jobName"/>
        </a-form-model-item>
        <a-form-model-item label="任务组名">
          <a-input v-model="addForm.jobGroupName"/>
        </a-form-model-item>
        <a-form-model-item label="时间表达式">
          <a-input v-model="addForm.jobTime"/>
        </a-form-model-item>
        <a-form-model-item label="任务状态">
          <a-input v-model="addForm.jobStatus"/>
        </a-form-model-item>
        <a-form-model-item label="任务描述">
          <a-input v-model="addForm.description"/>
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="addDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="addJob">确 定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import QUARTZ_API from '@api/api_quartz'

export default {
  name: 'AddQuartz',
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
    }
  },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      addDialogFormVisible: false,
      addForm: {}
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function() {
      const that = this
      that.addDialogFormVisible = true
    },
    addJob: function() {
      const that = this
      // 定义请求参数
      const params = that.addForm
      // 调用接口
      QUARTZ_API.addJob(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message.success('恭喜你，添加成功')
          that.addDialogFormVisible = false
          that.addForm = {}
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
<style lang="scss" scoped>
::v-deep .el-select,.el-cascader {
    display: block;
    position: relative;
}

::v-deep .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
