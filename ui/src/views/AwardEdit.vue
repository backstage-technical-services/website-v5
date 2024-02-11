<template>
  <q-page>
    <h1>Edit Award</h1>

    <div class="q-pa-md q-mx-auto" style="max-width: 500px">
      <q-form class="q-gutter-md" @submit="onSubmit">
        <!-- Name -->
        <q-skeleton type="QInput" v-if="isLoading" />
        <q-input v-model="formData.name" label="Name" :rules="nameValidation" v-else />

        <!-- Description -->
        <q-skeleton type="QInput" height="150px" v-if="isLoading" />
        <q-input type="textarea" v-model="formData.description" label="Description" v-else />

        <!-- Recurring -->
        <q-skeleton type="QToggle" width="150px" v-if="isLoading" />
        <q-toggle v-model="formData.recurring" label="Automatically recurs" v-else />

        <div class="q-mt-xl">
          <q-btn label="Save" color="positive" class="q-mr-md" type="submit" :disable="isUpdating" :loading="isUpdating" />
          <q-btn label="Cancel" :to="{ name: 'list-awards' }" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import type { UpdateAwardRequest } from '@/api/domains/awards'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import { useNotifications } from '@/composables/notifications'
import { handleError, mapError, useApi } from '@/api'
import type { ValidationRules } from '@/helpers/validation'

const router = useRouter()
const route = useRoute()
const id = computed(() => parseInt(route.params.id as string))

const { setBreadcrumbs } = useBreadcrumbs()
setBreadcrumbs('Awards', 'Edit Award')

const isLoading = ref(true)
const formData = reactive<UpdateAwardRequest>({
  name: '',
  description: '',
  recurring: false,
})
const nameValidation: ValidationRules = [
  (val: string) => val.length > 0 || 'Required',
]

const api = useApi()

const isUpdating = ref(false)
const onSubmit = async() => {
  isUpdating.value = true
  const request: UpdateAwardRequest = {
    name: formData.name,
    description: formData.description,
    recurring: formData.recurring,
  }

  return api.awards.update(id.value, request)
    .then(() => {
      const { success } = useNotifications()
      success('Award updated')
      return router.push({
        name: 'awards:list',
      })
    })
    .catch(err => {
      const { error } = useNotifications()
      error(`Could not update award: ${mapError(err).text}`)
    })
    .then(() => {
      isUpdating.value = false
    })
}

onMounted(() => {
  api.awards.get(id.value)
    .then(response => {
      formData.name = response.name
      formData.description = response.description || ''
      formData.recurring = response.recurring
    })
    .catch(handleError)
    .then(() => {
      isLoading.value = false
    })
})

</script>
