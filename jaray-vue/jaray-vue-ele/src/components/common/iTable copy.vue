<template>
  <div>
    <el-table
      :data="tableData"
      :height="tableHeight"
      :highlight-current-row="true"
      border
      size="mini"
      style="width: 100%;box-shadow: 3px 6px 5px #888888;"
      @selection-change="handleSelectionChange"
      @filter-change="handlerFilterChange"
      @row-click="rowClick">
      <el-table-column
        type="selection"
        width="40"/>
      <template v-for="(item, key) in tableTitle" >
        <el-table-column
          :key="key"
          :fixed="item.fixed"
          :prop="item.prop"
          :label="item.label"
          :width="item.width"
          :sortable="item.sort"
          :column-key="item.prop"
          :filters="item.filters?item.filters:null"
          :filter-method="item.filters?filterHandler:null"
          :formatter="item.formatter"
          :show-overflow-tooltip="true">
          <!-- 加入template主要是有操作一栏， 操作一栏的内容是相同的， 数据不是动态获取的，这里操作一栏的名字定死（operation表示是操作这一列，否则就不是） -->
          <template slot-scope="scope">
            <span v-if="item.operation">
              <template v-for="(it, index) in item.operation">
                <el-button
                  v-if="it.disabled"
                  :key="index+key"
                  :type="it.style"
                  :width="it.width"
                  :icon="it.icon"
                  size="mini"
                  plain
                  @click.stop="it.clickFun(scope.row)">{{ it.name }}
                </el-button>
              </template>
            </span>
            <!-- formatter：自定义过滤器 -->
            <span v-else-if="item.formatter">
              <span v-if="item.renderComponent == 'tag'">
                <!-- ** start ** 这一块做el-tag标签样式判断。不需要可以去除判断 -->
                <el-tag size="mini">{{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}</el-tag>
                <!-- ** end ** -->
              </span>
              <span v-else>{{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}</span>
            </span>
            <span v-else>{{ scope.row[item.prop] }}</span>
          </template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>

<script>

export default {
  name: 'ITable',
  props: {
    tableTitle: {
      type: Array,
      default: function() {
        return []
      }
    },
    tableData: {
      type: Array,
      default: function() {
        return []
      }
    },
    tableHeight: {
      type: String,
      default: function() {
        return 0
      }
    }
  },
  data() {
    return {
      multipleSelection: [],
      currentPage: 1,
      // 表格高度自适应
      iKey: 0
    }
  },
  watch: {
  },
  mounted: function() {
  },
  methods: {
    handleClick: function(row) {
      console.log(row)
    },
    handleView: function(index, row) {
      console.log(index, row)
      const that = this
      that.$emit('handleView', row)
    },
    handleEdit: function(index, row) {
      console.log(index, row)
      const that = this
      that.$emit('handleEdit', row)
    },
    handleSelectionChange: function(val) {
      const that = this
      that.multipleSelection = val
      that.transmitParent()
    },
    handlerFilterChange: function(val) {
      console.log(val)
    },
    rowClick: function(row) {
      const that = this
      that.$emit('rowClick', row)
    },
    transmitParent() {
      const that = this
      that.$emit('transmitParent', that.multipleSelection)
    },
    filterHandler(value, row, column) {
      const property = column['property']
      return row[property] === value
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
.el-table__fixed-right{
    height: 100% !important;
}
</style>
