<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="请假类型">
          <el-select v-model="editForm.leaveType" placeholder="请选择">
            <el-option
              v-for="item in leaveOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假时间">
          <el-col :span="24">
            <el-date-picker
              v-model="editForm.startAndEndDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始月份"
              end-placeholder="结束月份"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假天数">
          <el-col :span="24">
            <el-input v-model="editForm.days" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假事由">
          <el-col :span="24">
            <el-input
              :rows="2"
              v-model="editForm.reason"
              type="textarea"
              placeholder="请输入内容"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-col :span="24">
            <el-input v-model="editForm.createBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-col :span="24">
            <el-date-picker
              v-model="editForm.createTime"
              :picker-options="pickerOptions"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-col :span="24">
            <el-input v-model="editForm.lastUpdateBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-col :span="24">
            <el-date-picker
              v-model="editForm.lastUpdateTime"
              :picker-options="pickerOptions"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editLeaveApply">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LEAVE_APPLY_API from '@api/api_leave_apply'

export default {
  name: 'EditLeaveApply',
  components: {},
  props: {
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
      editDialogFormVisible: false,
      editForm: {},
      leaveOptions: [{
        value: '0',
        label: '倒休'
      }, {
        value: '1',
        label: '病假'
      }, {
        value: '2',
        label: '事假'
      }],
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
      }
    }
  },
  watch: {
    'editForm.startAndEndDate': function(nval, oval) {
      const that = this
      if (nval) {
        console.log(that.editForm.startAndEndDate[0])
        console.log(nval[1])
        var sDate = new Date(that.editForm.startAndEndDate[0])
        var eDate = new Date(that.editForm.startAndEndDate[1])
        var times = eDate.getTime() - sDate.getTime()
        that.editForm.startTime = that.$moment(sDate).format('YYYY-MM-DD HH:mm:ss')
        that.editForm.endTime = that.$moment(eDate).format('YYYY-MM-DD HH:mm:ss')
        that.editForm.days = Math.floor(times / (86400 * 1000)) + 1
      } else {
        that.editForm.days = 0
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
      LEAVE_APPLY_API.viewLeaveApplyById(params).then(function(result) {
        if (result.code === 200) {
          that.editForm = result.map.leaveApply

          const startTime = that.$moment(result.map.leaveApply.startTime)
          const endTime = that.$moment(result.map.leaveApply.endTime)
          that.$set(that.editForm, 'startAndEndDate', [startTime, endTime])

          that.editDialogFormVisible = true
        }
      })
    },
    editLeaveApply: function() {
      const that = this
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      LEAVE_APPLY_API.updateLeaveApplyById(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，编辑成功',
            type: 'success'
          })
          that.editDialogFormVisible = false
        }
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-select,.el-cascader {
    display: block;
    position: relative;
}

/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
