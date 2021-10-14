<template>
  <div id="add">
    <el-dialog :visible.sync="viewFlowDiagramDialogFormVisible" title="流程图">
      <el-image :src="url"/>
      <div class="text item">
        {{ '原因 ' + flowDetail.reason + '请假天数' + flowDetail.days + '请假开始时间' + flowDetail.startTime + '请假结束时间' + flowDetail.endTime }}
      </div>

      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in taskList"
          :key="index"
          :icon="item.icon"
          :type="item.type"
          :color="item.color"
          :size="item.size"
          :timestamp="item.timestamp">
          {{ item.name }}
          <el-card>
            <p>办理人 {{ item.assignee }}</p>
            <p>开始时间 {{ item.startTime }} 结束时间 {{ item.endTime }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewFlowDiagramDialogFormVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: 'ViewFlowDiagram',
  components: {},
  props: {
    url: {
      type: String,
      default: function() {
        return ''
      }
    },
    taskList: {
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
      viewFlowDiagramDialogFormVisible: false,
      flowDetail: null,
      active: 0
    }
  },
  watch: {
    // 使用监听
    taskList: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.setActive(newVal)
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
    openDialog: function(row) {
      const that = this
      that.flowDetail = row
      that.viewFlowDiagramDialogFormVisible = true
    },
    closeDialog: function() {
      const that = this
      that.viewFlowDiagramDialogFormVisible = false
    },
    setActive: function(taskList) {
      const that = this
      for (var i = 0; i < taskList.length; i++) {
        if (taskList[i].endTime !== null) {
          that.active = i + 1
          taskList[i].type = 'success'
        } else {
          taskList[i].type = 'danger'
        }
      }
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>

</style>
