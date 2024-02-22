<template>
  <!-- Logged in -->
  <template v-if="isAuthenticated">
    <q-item-label header>My account</q-item-label>

    <q-item to="/profile" clickable v-ripple>
      <q-item-section avatar>
        <q-icon :name="mdiAccount"/>
      </q-item-section>

      <q-item-section>My profile</q-item-section>
    </q-item>

    <q-item to="/profile/events" clickable v-ripple>
      <q-item-section avatar>
        <q-icon :name="mdiCalendar"/>
      </q-item-section>

      <q-item-section>My events</q-item-section>
    </q-item>

    <q-item clickable v-ripple @click="logout">
      <q-item-section avatar>
        <q-icon :name="mdiLogout" />
      </q-item-section>

      <q-item-section>Log out</q-item-section>
    </q-item>
  </template>

  <!-- Not logged in -->
  <template v-else>
    <q-item clickable v-ripple @click="login">
      <q-item-section avatar>
        <q-icon :name="mdiLogin"/>
      </q-item-section>

      <q-item-section>Log in</q-item-section>
    </q-item>
  </template>


</template>

<script lang="ts" setup>
import { mdiLogin, mdiLogout, mdiAccount, mdiCalendar, mdiStar } from '@quasar/extras/mdi-v7'
import { useAuth0 } from '@auth0/auth0-vue'
import { login, logout } from '@/config/auth'

const { isAuthenticated } = useAuth0()
</script>

<style lang="scss" scoped>

</style>
