<template>
  <!--在视频外面加一个容器-->
  <div id="view" class="input_video">
    <a-modal
      :title="title"
      :visible.sync="viewDialogFormVisible"
      :destroy-on-close="destroyOnClose"
      @ok="handleOk"
      @cancel="handleCancel">
      <div class="player-container">
        <vue-core-video-player v-if="showFlag" :src="src" @loadedmetadata="loaded($event)" @play="playFunc($event)" @pause="pauseFunc($event)"/>
      </div>
    </a-modal>
  </div>
</template>

<script>
import aliPlayer from '@components/common/aliPlayer'

export default {
  name: 'ViewVideo',
  components: { 'ali-player': aliPlayer },
  props: { },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      viewDialogFormVisible: false,
      destroyOnClose: true,
      showFlag: false,
      title: '',
      src: ''

    }
  },
  created: function() {

  },
  mounted: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      that.title = row.name
      that.src = row.videoAddr
      that.viewDialogFormVisible = true
      that.showFlag = true
      console.log(row)
    },
    handleOk(e) {
      const that = this
      that.showFlag = false
      that.viewDialogFormVisible = false
      that.confirmLoading = false
    },
    handleCancel(e) {
      const that = this
      that.viewDialogFormVisible = false
    },
    loaded() {
      // your code
    },
    playFunc() {
      // your code
    },
    pauseFunc() {
      // your code
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
    .input_video{
        width: 400px;
        height: 400px;
        margin: 0 auto;

    }
</style>
