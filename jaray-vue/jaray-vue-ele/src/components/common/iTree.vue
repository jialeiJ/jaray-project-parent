<template>
  <el-tree
    ref="tree"
    :data="treeData"
    :props="defaultProps"
    :height="treeHeight"
    size="mini"
    show-checkbox
    node-key="id"
    highlight-current
    @check="handleCheck">
    <span slot-scope="{ node, data }" class="custom-tree-node">
      <span>{{ node.label }}</span>
      <span v-if="isShow">
        <el-button
          type="text"
          size="mini"
          @click="() => append(data)">
          Append
        </el-button>
        <el-button
          type="text"
          size="mini"
          @click="() => remove(node, data)">
          Delete
        </el-button>
      </span>
    </span>
  </el-tree>
</template>

<script>

export default {
  name: 'ITree',
  props: {
    treeData: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      tileTreeData: [],
      isShow: false,
      treeHeight: 0
    }
  },
  watch: {
    // 使用监听
    treeData: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.tileTree(newVal)
        }
      },
      immediate: true, // 立即执行
      deep: true // 深度监听
    }
  },
  created: function() {
    // 渲染前执行,无法获取到父组件的传值
  },
  mounted: function() {
    // 渲染后执行,无法获取到父组件的传值
  },
  methods: {
    handleCheck(data, checked, indeterminate) {
      const that = this
      const keys = []
      const checkedKeys = that.$refs.tree.getCheckedKeys()
      const halfCheckedKeys = that.$refs.tree.getHalfCheckedKeys()
      const allKeys = checkedKeys.concat(halfCheckedKeys)
      that.tileTreeData.forEach(function(item, index) {
        if (allKeys.indexOf(item.id) > -1) {
          if (keys.indexOf(item.parentId) === -1 && item.parentId) {
            keys.push(item.parentId)
          }
          keys.push(item.id)
        }
      })

      that.$emit('transmitParentKeys', keys)
    },
    allChecked() {
      const that = this
      const keys = []
      that.tileTreeData.forEach(function(item, index) {
        const i = keys.indexOf(item.parentId)
        if (i === -1 && item.parent_id) {
          keys.push(item.parent_id)
        }
        const j = keys.indexOf(item.id)
        if (j === -1 && item.id) {
          keys.push(item.id)
        }
      })
      that.$refs.tree.setCheckedKeys(keys)
      that.$emit('transmitParentKeys', keys)
    },
    // 重置
    resetChecked(keys) {
      const that = this
      // 移除父id
      that.tileTreeData.forEach(function(item, index) {
        const i = keys.indexOf(item.parent_id)
        if (i > -1) {
          // keys.splice(i,1)
        }
      })
      that.$refs.tree.setCheckedKeys(keys)
      that.handleCheck()
    },
    // 清空
    clearChecked() {
      const that = this
      that.$refs['tree'].setCheckedKeys([])
      that.handleCheck()
    },
    // 平铺数据
    tileTree(treeData) {
      const that = this
      treeData.forEach(function(item, index) {
        that.tileTreeData.push(item)
        if (item.children.length) {
          that.tileTree(item.children)
        }
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
}
</style>
