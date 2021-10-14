<template>
  <div id="add">
    <el-dialog :visible.sync="viewFlowDiagramDialogFormVisible" title="新增">
      <el-image :src="url"/>
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
    }},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      viewFlowDiagramDialogFormVisible: false
    }
  },
  watch: {

  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      that.viewFlowDiagramDialogFormVisible = true
    },
    closeDialog: function() {
      const that = this
      that.viewFlowDiagramDialogFormVisible = false
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>

</style>
