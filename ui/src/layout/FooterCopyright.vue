<template>
  <p>&copy; Backstage Technical Services {{ currentYear }}. All rights reserved.</p>
  <p>Generously supported by the University of Bath Alumni Fund.</p>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

const currentYear = ref(new Date().getFullYear())
</script>

<style lang="scss" scoped>
@import '@/assets/css/mixins';

p {
  line-height: 1;
  margin: 0.5em 0;
  text-align: center;

  &:nth-child(2) {
    color: #444;
  }
}

@include breakpoint-from(lg) {
  p {
    text-align: right;
  }
}
</style>
