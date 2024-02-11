<template>
  <ul>
    <li v-for="(link, index) in links" :key="index">
      <router-link :to="link.link">{{ link.text }}</router-link>
      <span v-if="index !== links.length - 1">/</span>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import type { RouteLocationRaw } from 'vue-router'

type FooterLink = {
  text: string;
  link: RouteLocationRaw;
}

const links: FooterLink[] = [
  { text: 'Contact Us', link: '/contact' },
  { text: 'Book Us', link: '/book-us' },
  { text: 'FAQ', link: { name: 'faq' } },
  { text: 'Terms and Conditions', link: { name: 'terms-conditions' } },
  { text: 'Privacy Policy', link: { name: 'privacy-policy' } },
]
</script>

<style lang="scss" scoped>
@import '@/assets/css/mixins';

ul {
  margin: 0;
  padding: 0;
  text-align: center;

  li {
    display: inline-block;
    list-style-type: none;
    margin: 0;
    padding: 0;

    span {
      margin: 0 0.75em;
    }
  }
}

@include breakpoint-from(lg) {
  ul {
    text-align: left;
  }
}
</style>
