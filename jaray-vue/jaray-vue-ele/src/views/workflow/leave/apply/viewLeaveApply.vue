<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="请假类型">
          <el-select v-model="viewForm.leaveType" placeholder="请选择" disabled>
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
              v-model="viewForm.startAndEndDate"
              disabled
              type="daterange"
              range-separator="至"
              start-placeholder="开始月份"
              end-placeholder="结束月份"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假天数">
          <el-col :span="24">
            <el-input v-model="viewForm.days" autocomplete="off" disabled/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假事由">
          <el-col :span="24">
            <el-input
              :rows="2"
              v-model="viewForm.reason"
              disabled
              type="textarea"
              placeholder="请输入内容"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-col :span="24">
            <el-input v-model="viewForm.createBy" autocomplete="off" disabled/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-col :span="24">
            <el-date-picker
              v-model="viewForm.createTime"
              :picker-options="pickerOptions"
              disabled
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-col :span="24">
            <el-input v-model="viewForm.lastUpdateBy" autocomplete="off" disabled/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-col :span="24">
            <el-date-picker
              v-model="viewForm.lastUpdateTime"
              :picker-options="pickerOptions"
              disabled
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"/>
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LEAVE_APPLY_API from '@api/api_leave_apply'

export default {
  name: 'ViewLeaveApply',
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
      viewDialogFormVisible: false,
      viewForm: {},
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
          that.viewForm = result.map.leaveApply

          const startTime = that.$moment(result.map.leaveApply.startTime)
          const endTime = that.$moment(result.map.leaveApply.endTime)
          that.$set(that.viewForm, 'startAndEndDate', [startTime, endTime])

          that.viewDialogFormVisible = true
        }
      })
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
