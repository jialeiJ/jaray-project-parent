<template>
  <el-table
    :data="tableData"
    :key="iKey"
    :max-height="tableHeight"
    :highlight-current-row="true"
    border
    size="mini"
    tooltip-effect="dark"
    style="width: 100%;box-shadow: 3px 6px 5px #888888;"
    @selection-change="handleSelectionChange"
    @filter-change="handlerFilterChange"
    @row-click="rowClick">
    <el-table-column
      type="selection"
      width="40"/>
    <template v-for="(item, key) in tableTitle" >
      <el-table-column
        v-if="item.filters"
        :key="key"
        :fixed="item.fixed"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :sortable="item.sort"
        :column-key="item.prop"
        :filters="item.filters"
        :filter-method="filterHandler"
        :formatter="item.formatter"
        :show-overflow-tooltip="true">
        <!-- 加入template主要是有操作一栏， 操作一栏的内容是相同的， 数据不是动态获取的，这里操作一栏的名字定死（operation表示是操作这一列，否则就不是） -->
        <template slot-scope="scope">
          <!-- formatter：自定义过滤器 -->
          <div v-if="item.formatter">
            <span v-if="item.renderComponent == 'tag'">
              <!-- ** start ** 这一块做el-tag标签样式判断。不需要可以去除判断 -->
              <el-popover trigger="hover" placement="top">
                <p>{{ item.label }}: {{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}</p>
                <div slot="reference" class="name-wrapper">
                  <el-tag size="medium">{{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}</el-tag>
                </div>
              </el-popover>
              <!-- ** end ** -->
            </span>
            <span v-else>
              <el-popover trigger="hover" placement="top">
                <p>{{ item.label }}: {{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}</p>
                <div slot="reference" class="name-wrapper">
                  {{ item.formatter(scope.row, item.prop, scope.row[item.prop], key) }}
                </div>
              </el-popover>
            </span>
          </div>
          <div v-else>
            <el-popover trigger="hover" placement="top">
              <p>{{ item.label }}: {{ scope.row[item.prop] }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row[item.prop] }}
              </div>
            </el-popover>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        v-else
        :key="key"
        :fixed="item.fixed"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :sortable="item.sort"
        :column-key="item.prop"
        :formatter="item.formatter"
        :show-overflow-tooltip="true">
        <!-- 加入template主要是有操作一栏， 操作一栏的内容是相同的， 数据不是动态获取的，这里操作一栏的名字定死（operation表示是操作这一列，否则就不是） -->
        <template slot-scope="scope">
          <div v-if="item.operation">
            <el-button
              v-for="(it, index) in item.operation"
              :key="index"
              :type="it.style"
              :icon="it.icon"
              size="mini"
              @click="it.clickFun(scope.row)">{{ it.name }}
            </el-button>
          </div>

          <!-- formatter：自定义过滤器 -->
          <div v-if="item.formatter">
            <span v-if="item.renderComponent == 'tag'">
              <!-- ** start ** 这一块做el-tag标签样式判断。不需要可以去除判断 -->
              <el-popover trigger="hover" placement="top">
                <p>{{ item.label }}: {{ item.formatter(null, null, scope.row[item.prop], null) }}</p>
                <div slot="reference" class="name-wrapper">
                  <el-tag size="medium">{{ item.formatter(null, null, scope.row[item.prop], null) }}</el-tag>
                </div>
              </el-popover>
              <!-- ** end ** -->
            </span>
            <span v-else>
              <el-popover trigger="hover" placement="top">
                <p>{{ item.label }}: {{ item.formatter(null, null, scope.row[item.prop], null) }}</p>
                <div slot="reference" class="name-wrapper">
                  {{ item.formatter(null, null, scope.row[item.prop], null) }}
                </div>
              </el-popover>
            </span>
          </div>
          <div v-else>
            <el-popover trigger="hover" placement="top">
              <p>{{ item.label }}: {{ scope.row[item.prop] }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row[item.prop] }}
              </div>
            </el-popover>
          </div>
        </template>
      </el-table-column>
    </template>
    <el-table-column
      fixed="right"
      label="操作"
      width="90">
      <template slot-scope="scope">
        <el-button type="text" size="mini" @click.stop="handleView(scope.$index, scope.row)">查看</el-button>
        <el-button type="text" size="mini" @click.stop="handleEdit(scope.$index, scope.row)">编辑</el-button>
      </template>
    </el-table-column>
  </el-table>
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
      search: '',
      multipleSelection: [],
      currentPage: 1,
      // 表格高度自适应
      iKey: 1
    }
  },
  watch: {
    tableLoading: function() {
      ++this.iKey
    }
  },
  methods: {
    handleClick: function(row) {
      // console.log(row);
    },
    handleView: function(index, row) {
      // console.log(index, row)
      const that = this
      that.$emit('handleView', row)
    },
    handleEdit: function(index, row) {
      // console.log(index, row)
      const that = this
      that.$emit('handleEdit', row)
    },
    handleSelectionChange: function(val) {
      const that = this
      that.multipleSelection = val
      that.transmitParent()
    },
    handlerFilterChange: function(val) {
      // console.log(val)
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
