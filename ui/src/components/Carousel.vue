<template>
  <q-carousel v-model="slide" :autoplay="interval * 1000" height="500px" arrows infinite animated>
    <q-carousel-slide v-for="(image, index) in props.images" :key="index" :name="index" :img-src="image.url">
      <div class="caption absolute-bottom text-center text-grey-5 q-py-md" v-if="image.text">
        {{ image.text }}
      </div>
    </q-carousel-slide>
  </q-carousel>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

export type CarouselImage = {
  url: string
  text?: string
}

type Props = {
  images: CarouselImage[]
  interval: number
}
const props = withDefaults(defineProps<Props>(), {
  interval: 10,
})

const slide = ref(0)
</script>

<style lang="scss" scoped>
.q-carousel__slide {
  .caption {
    background: rgba(0, 0, 0, 0.5);
    font-weight: 300;
  }
}
</style>
