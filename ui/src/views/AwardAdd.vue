<template>
  <q-page>
    <h1>{{ mode === 'create' ? 'Add' : 'Suggest' }} Award</h1>

    <div class="q-pa-md q-mx-auto" style="max-width: 500px">
      <q-form class="q-gutter-md" @submit="onSubmit">
        <!-- Name -->
        <q-input v-model="formData.name" label="Name" :rules="requiredString" />

        <!-- Description -->
        <q-input
            type="textarea"
            v-model="formData.description"
            label="Description"
            :rules="requiredString"
            hint="Provide a good description that explains what the award is for"/>

        <!-- Recurring -->
        <q-toggle v-model="formData.recurring" label="Automatically recurs every year" />

        <div class="q-mt-xl">
          <q-btn label="Save" color="positive" class="q-mr-md" type="submit" :disable="isSubmitting" :loading="isSubmitting" />
          <q-btn label="Cancel" :to="{ name: 'awards:list' }" />
        </div>
      </q-form>
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { requiredString } from '@/helpers/validation'
import { useBreadcrumbs } from '@/composables/breadcrumbs'
import { useNotifications } from '@/composables/notifications'
import type { CreateAwardRequest } from '@/api/domains/awards'
import { mapError, useApi } from '@/api'

type Mode = 'create' | 'suggest'
const props = defineProps<{
  mode: Mode
}>()

const { setBreadcrumbs } = useBreadcrumbs()
setBreadcrumbs('Awards', props.mode === 'create' ? 'Add' : 'Suggest')

const isSubmitting = ref(false)
const formData = reactive<CreateAwardRequest>({
  name: '',
  description: '',
  recurring: false,
})

const api = useApi()
const router = useRouter()
const { error, success } = useNotifications()
const onSubmit = async() => {
  isSubmitting.value = true

  const createFn = props.mode === 'create' ? api.awards.create : api.awards.suggest
  const createdVerb = props.mode === 'create' ? 'created' : 'suggested'
  const request: CreateAwardRequest = {
    name: formData.name,
    description: formData.description,
    recurring: formData.recurring,
  }
  return createFn(request)
    .then(() => {
      success(`Award ${createdVerb}`)
      return router.push({
        name: 'awards:list',
      })
    })
    .catch(err => {
      error(`Could not ${createdVerb} award: ${mapError(err).text}`)
    })
    .then(() => {
      isSubmitting.value = false
    })
}
</script>
