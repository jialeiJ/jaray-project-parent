<template>
  <a-table
    :pagination="false"
    :data-source="tableData"
    :row-key="(row,index)=> row.id?row.id:index"
    :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
    :custom-row="onClickRow"
    :scroll="{ x: 1500, y: 'calc(95vh - 300px)' }"
    bordered
    size="small"
    expand-row-by-click
    style="margin-bottom: 12px">
    <template v-for="(item, itemIndex) in tableTitle">
      <a-table-column
        :key="item.key"
        :title="item.title"
        :sorter="item.sort"
        :filters="item.filters"
        :data-index="item.dataIndex"
        :fixed="item.fixed"
        :width="item.width"
        :ellipsis="true"
        filter-multiple>
        <!-- formatter：自定义过滤器 -->
        <template slot-scope="text, record">
          <template v-if="item.formatter">
            <template v-if="item.tagRender != undefined">
              <!-- ** start ** 这一块做el-tag标签样式判断。不需要可以去除判断 -->
              <a-tag :color="item.tagRender(record, item.key, record[item.key], itemIndex)" size="mini">{{ item.formatter(record, item.key, record[item.key], itemIndex) }}</a-tag>
              <!-- ** end ** -->
            </template>
            <template v-else>
              <a-popover :title="item.title">
                <template slot="content">
                  {{ item.formatter(record, item.key, record[item.key], itemIndex) }}
                </template>
                <template>
                  {{ item.formatter(record, item.key, record[item.key], itemIndex) }}
                </template>
              </a-popover>
            </template>
          </template>
          <template v-else-if="item.operation">
            <template v-for="(it, index) in item.operation">
              <a :key="itemIndex+index" @click.stop="it.clickFun(record)">{{ it.name }}</a>
              <a-divider v-if="index !== (item.operation.length-1)" :key="index" type="vertical" />
            </template>
          </template>
          <template v-else>
            <a-popover :title="item.title">
              <template slot="content">
                {{ record[item.key] }}
              </template>
              <template>
                {{ record[item.key] }}
              </template>
            </a-popover>
          </template>
        </template>
      </a-table-column>
    </template>
  </a-table>
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
      selectedRowKeys: [],
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
    onSelectChange: function(selectedRowKeys) {
      const that = this
      that.selectedRowKeys = selectedRowKeys
      that.transmitParent()
    },
    onClickRow(record, index) {
      const that = this
      return {
        on: {
          click: () => {
            that.$emit('row_click', record)
          }
        }
      }
    },
    transmitParent() {
      const that = this
      that.$emit('transmit_parent', that.selectedRowKeys)
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
/deep/ .ant-table-fixed-left table, .ant-table-fixed-right table{
    width: min-content;
}
</style>
