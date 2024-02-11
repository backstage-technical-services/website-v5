<template>
  <q-page class="no-container">
    <carousel
        :images="carouselImages"
        class="container"
        style="margin-top: -2em; padding-left: 0 !important; padding-right: 0 !important;" />

    <hero
        title="Always dedicated. Always professional."
        bg-color="#252525"
        btn-label="Book us now">
      <p>
        We may be students at the University of Bath, but we all strive to provide the same level of professionalism
        that you would expect from those in the industry.
      </p>
      <p>
        We take pride in what we do and will work with you to make your event the best it can be.
      </p>
      <p>
        On- and off-campus; society or professionals; small club nights, through large theatre or dance shows, to
        enormous live events. We do it all.
      </p>
    </hero>

    <hero
        title="Work hard. Play even harder."
        bg-color="none"
        image="/landing/lighting_desk.jpg"
        class="text-grey-3">
      <p>
        We do what we do because we love to do it. We ensure that despite the high level of professionalism we work
        to everyone is having fun. Because that’s when we shine.
      </p>
      <p>
        And when the event is done, we make sure we reward ourselves. After-show parties, society socials and even a
        simple night at the pub; we truly know how to wind down and celebrate. And let’s not forget the infamous
        annual pub tour through the beautiful surrounding countryside.
      </p>
      <p>
        Backstage is not just a society; it’s not just a community; it’s a family.
      </p>
      <p>
        And with great friends, it’ll be one of the best times of your life.
      </p>
    </hero>

    <hero
        title="Open to all, no matter your background."
        bg-color="#1a1a1a"
        btn-label="Join us now"
        btn-color="secondary">
      <p>
        With roles from lighting, sound, video and pyrotechnics, to set design, to stage and event management, we have
        a
        job for everyone. It doesn't matter whether you’ve done this before, or you want to try something new - we'd
        love to have you aboard!
      </p>
      <p>
        Most training is completely free of charge, and you can get nationally recognised qualifications for a
        fraction
        of the price.
      </p>
      <p>
        And even better, you can choose what you want to do. So what are you waiting for?
      </p>
    </hero>

    <hero title="Questions? Get in touch!" bg-color="#000" class="text-grey-7">
      <div class="q-gutter-none">
        <div class="row">
          <div class="col-12 col-md-6">
            <q-icon size="xl" :name="mdiPhone"/>
            <div class="text-h2">+44 (0) 1225 666076</div>
          </div>
          <div class="col-12 col-md-6">
            <q-icon size="xl" :name="mdiEmail"/>
            <div class="text-h2">committee@bts-crew.com</div>
          </div>
        </div>
      </div>
    </hero>
  </q-page>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'
import { useHead } from 'unhead'
import { mdiPhone, mdiEmail } from '@quasar/extras/mdi-v7'

import Carousel from '@/components/Carousel.vue'
import Hero from './Hero.vue'

import carouselImages from '@/data/carousel'

onMounted(() => {
  useHead({ title: 'Home' })
})
</script>

<style lang="scss" scoped>
.text-h2 {
  color: inherit;
  font-size: 26px !important;
  font-weight: 300;
  margin: 0.2em 0 0;
}
</style>
