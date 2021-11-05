<template>
  <a-tree
    ref="tree"
    :replace-fields="replaceFields"
    :tree-data="treeData"
    :checked-keys="checkedKeys"
    :selected-keys="selectedKeys"
    :height="treeHeight"
    checkable
    @expand="onExpand"
    @check="onCheck"
    @select="onSelect">
    <span slot-scope="{ node, data }" class="custom-tree-node">
      <span>{{ node }} - {{ data }}</span>
      <span>
        <a-button
          type="text"
          size="small"
          @click="() => append(data)">
          Append
        </a-button>
        <a-button
          type="text"
          size="small"
          @click="() => remove(node, data)">
          Delete
        </a-button>
      </span>
    </span>
  </a-tree>
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
      replaceFields: {
        children: 'children',
        title: 'title',
        key: 'value'
      },
      checkedKeys: [],
      selectedKeys: [],
      tileTreeData: [],
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
    onExpand(expandedKeys) {
      console.log('onExpand', expandedKeys)
      this.expandedKeys = expandedKeys
      this.autoExpandParent = false
    },
    onCheck(checkedKeys, event) {
      const that = this
      console.log('checkedKeys', checkedKeys)
      console.log('halfCheckedKeys', event.halfCheckedKeys)

      const keys = []
      const allKeys = checkedKeys.concat(event.halfCheckedKeys)
      that.tileTreeData.forEach(function(item, index) {
        if (allKeys.indexOf(item.id) > -1) {
          if (keys.indexOf(item.parentId) === -1 && item.parentId) {
            keys.push(item.parentId)
          }
          keys.push(item.id)
        }
      })

      that.resetChecked(checkedKeys)
      that.$emit('transmit_parent_keys', keys)
    },
    onSelect(selectedKeys, info) {
      console.log('onSelect', info)
      this.selectedKeys = selectedKeys
    },
    allChecked() {
      const that = this
      const keys = []
      that.tileTreeData.forEach(function(item, index) {
        const j = keys.indexOf(item.id)
        if (j === -1) {
          keys.push(item.id)
        }
      })
      that.checkedKeys = keys
      // that.$refs.tree.setCheckedKeys(keys);
      that.$emit('transmit_parent_keys', keys)
    },
    // 重置
    resetChecked(keys) {
      const that = this
      // 移除父id
      that.tileTreeData.forEach(function(item, index) {
        const i = keys.indexOf(item.parentId)
        if (i > -1) {
          // keys.splice(i,1)
        }
      })
      that.checkedKeys = keys
      // that.$refs.tree.setCheckedKeys(keys);
      // that.handleCheck()
    },
    // 清空
    clearChecked() {
      const that = this
      that.checkedKeys = []
      // that.$refs['tree'].setCheckedKeys([]);
      // that.handleCheck()
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
