<template>
  <a-pagination
    :current="currentPage"
    :page-size="pageSize"
    :page-size-options="['1', '5', '10', '20', '50', '100', '200', '300', '400']"
    :total="total"
    size="small"
    show-size-changer
    show-quick-jumper
    @change="pageNumberChange"
    @showSizeChange="onShowSizeChange" />
</template>

<script>

export default {
  name: 'IPagination',
  props: {
    total: {
      type: Number,
      default: function() {
        return 0
      }
    },
    currentPage: {
      type: Number,
      default: function() {
        return 1
      }
    },
    pageSize: {
      type: Number,
      default: function() {
        return 10
      }
    }
  },
  data() {
    return {
    }
  },
  methods: {
    onShowSizeChange(current, pageSize) {
      console.log(`每页 ${pageSize} 条`)
      const that = this
      that.$emit('handleSizeChange', pageSize)
    },
    pageNumberChange(pageNumber) {
      console.log(`当前页: ${pageNumber}`)
      const that = this
      that.$emit('handleCurrentChange', pageNumber)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
