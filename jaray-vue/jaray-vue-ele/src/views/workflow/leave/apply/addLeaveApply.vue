<template>
  <div id="add">
    <el-dialog :visible.sync="addDialogFormVisible" title="新增">
      <el-form :model="addForm">
        <el-form-item :label-width="formLabelWidth" label="请假类型">
          <el-select v-model="addForm.leaveType" placeholder="请选择">
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
              v-model="addForm.startAndEndDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始月份"
              end-placeholder="结束月份"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假天数">
          <el-col :span="24">
            <el-input v-model="addForm.days" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="请假事由">
          <el-col :span="24">
            <el-input
              :rows="2"
              v-model="addForm.reason"
              type="textarea"
              placeholder="请输入内容"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-col :span="24">
            <el-input v-model="addForm.createBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-col :span="24">
            <el-date-picker
              v-model="addForm.createTime"
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
            <el-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-col :span="24">
            <el-date-picker
              v-model="addForm.lastUpdateTime"
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
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addLeaveApply">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LEAVE_APPLY_API from '@api/api_leave_apply'

export default {
  name: 'AddLeaveApply',
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
      addDialogFormVisible: false,
      addForm: {},
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
    'addForm.startAndEndDate': function(nval, oval) {
      const that = this
      if (nval) {
        console.log(that.addForm.startAndEndDate[0])
        console.log(nval[1])
        var sDate = new Date(that.addForm.startAndEndDate[0])
        var eDate = new Date(that.addForm.startAndEndDate[1])
        var times = eDate.getTime() - sDate.getTime()
        that.addForm.startTime = that.$moment(sDate).format('YYYY-MM-DD HH:mm:ss')
        that.addForm.endTime = that.$moment(eDate).format('YYYY-MM-DD HH:mm:ss')
        that.addForm.days = Math.floor(times / (86400 * 1000)) + 1
      } else {
        that.addForm.days = 0
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
    closeDialog: function() {
      const that = this
      that.addDialogFormVisible = false
    },
    handleChange: function(value) {
      const that = this
      if (value) {
        that.addForm.deptId = value[value.length - 1]
      }
    },
    calcDays: function(event) {
      if (this.sizeForm.sDate !== '' && this.sizeForm.eDate !== '') {
        var sDate = new Date(this.sizeForm.sDate)
        var eDate = new Date(this.sizeForm.eDate)
        var times = eDate.getTime() - sDate.getTime()
        this.sizeForm.days = Math.floor(times / (86400 * 1000)) + 1
      } else {
        this.sizeForm.days = 0
      }
    },
    addLeaveApply: function() {
      const that = this
      const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
      // 定义请求参数
      const params = { ...that.addForm, ...userInfo, ...{ userName: userInfo.name }}
      // 调用接口
      LEAVE_APPLY_API.addLeaveApply(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，添加成功',
            type: 'success'
          })
          that.addDialogFormVisible = false
          that.addForm = {}
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
