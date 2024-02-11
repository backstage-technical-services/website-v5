<template>
  <div class="hero" :style="heroStyles">
    <div class="hero-inner">
      <div class="container">
        <div class="text-h1 text-white">{{ title }}</div>

        <slot></slot>

        <q-btn v-if="showButton" :color="btnColor" :label="btnLabel" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

type Props = {
  title: string
  bgColor: string

  btnColor: string
  btnLabel?: string

  image?: string
}
const props = withDefaults(defineProps<Props>(), {
  btnColor: 'primary',
})
const showButton = computed(() => props.btnLabel !== undefined)

const heroStyles = computed(() => ({
  'background-color': props.image === undefined ? props.bgColor : undefined,
  'background-image': `url('${props.image}')`,
}))
</script>

<style lang="scss" scoped>
.hero {
  background-position: center bottom;
  background-repeat: no-repeat;
  background-size: 100% 100%;
  color: #888;
  display: flex;
  flex-direction: column;
  padding: 1.5rem 0 2rem 0;
  text-align: center;

  .text-h1 {
    color: #b3b3b3;
    font-size: 1.875rem;
    font-weight: 100;
    line-height: 1.4;
    margin-top: 0;
  }

  .q-btn {
    font-size: 1.125rem;
    margin: 0.5em auto 0 auto;
    padding: 0.5em 1.5em;
  }

  :deep(p) {
    font-size: 1.125rem;
    font-weight: 300;
    line-height: 1.4;
    margin: 1.5em 0;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
