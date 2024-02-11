<template>
  <q-footer class="bg-transparent">
    <div class="footer-row">
      <div class="row container">
        <div class="col-12 col-md-6">
          <footer-about/>
        </div>
        <div class="col-12 col-md-6">
          <footer-social/>
        </div>
      </div>
    </div>
    <div class="footer-row">
      <div class="row container">
        <div class="col-12 col-lg-6">
          <footer-links/>
        </div>
        <div class="col-12 col-lg-6">
          <footer-copyright/>
        </div>
      </div>
    </div>
  </q-footer>
</template>

<script lang="ts" setup>
import FooterAbout from './FooterAbout.vue'
import FooterCopyright from './FooterCopyright.vue'
import FooterLinks from './FooterLinks.vue'
import FooterSocial from './FooterSocial.vue'
</script>

<style lang="scss" scoped>
@import '@/assets/css/mixins';

.q-footer {
  color: #666; /* TODO: can this be a variable? */
  font-size: 0.8125rem;
  line-height: 1.3;

  .footer-row {
    &:nth-child(1) {
      background: #151515;
    }

    &:nth-child(2) {
      background: #121212;
    }
  }

  :deep(a) {
    @include transition(color);
    @include transition(background);

    &, &:visited {
      color: inherit;
    }

    &:hover,
    &:focus {
      color: #aaa;
      outline: none;
      text-decoration: none;
    }
  }

  .row {
    align-items: center;

    & > div {
      padding: 1.5em 1em;
    }
  }
}
</style>
