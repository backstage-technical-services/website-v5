<template>
  <div>
    <a
        v-for="link in links"
        :key="link.link"
        :href="link.link"
        :title="link.title"
        target="_blank">
      <q-icon size="sm" :name="link.icon" />
    </a>
  </div>
</template>


<script lang="ts" setup>
import { mdiFacebook, mdiInstagram, mdiTwitter, mdiGithub  } from '@quasar/extras/mdi-v7'

type SocialLink = {
  link: string
  title: string
  icon: string
}

const links: SocialLink[] = [
  {
    link: 'https://www.facebook.com/BackstageTechnicalServices',
    title: 'Follow us on Facebook',
    icon: mdiFacebook,
  },
  {
    link: 'https://www.instagram.com/backstageatbath/',
    title: 'Follow us on Instagram',
    icon: mdiInstagram,
  },
  {
    link: 'https://www.twitter.com/backstagetech',
    title: 'Follow us on Twitter',
    icon: mdiTwitter,
  },
  {
    link: 'https://github.com/backstage-technical-services/website-v5/issues',
    title: 'Report an issue with the website',
    icon: mdiGithub,
  },
]
</script>


<style lang="scss" scoped>
@import '@/assets/css/mixins';

$link-size: 40px;

div {
  display: flex;
  justify-content: center;

  & > a {
    background: #222;
    border-radius: #{$link-size/2};
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 1rem;
    height: $link-size;
    margin: 0 0.2em;
    width: $link-size;

    &:hover {
      background: #343434;
      color: #ccc;
      text-decoration: none;
    }

    &:focus {
      outline: none;
    }

    .q-icon {
      margin: 0;
      padding: 0;
    }
  }
}

@include breakpoint-from(md) {
  div {
    justify-content: right;
  }
}
</style>
