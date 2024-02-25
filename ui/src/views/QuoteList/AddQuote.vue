<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide" data-cy="quote-add-dialog">
    <q-card class="q-pa-md">
      <q-card-section>
        <div class="text-h1">Add a Quote</div>
      </q-card-section>

      <q-card-section>
        <q-form class="q-gutter-md" @submit="onSubmit" @reset="onDialogCancel" >
          <!-- Date/time -->
          <q-input v-model="formData.date" :rules="dateValidationRules" data-cy="quote-add-date">
            <template #before>
              <q-icon :name="mdiClock" />
            </template>

            <template #append>
              <q-icon :name="mdiCalendar" class="cursor-pointer" data-cy="quote-add-date-btn">
                <q-popup-proxy transition-show="scale" transition-hide="scale" data-cy="quote-add-date-selector">
                  <div class="q-gutter-sm row items-start">
                    <q-date v-model="formData.date" color="secondary" :mask="dateTimeMask" flat cover />
                    <q-time v-model="formData.date" color="secondary" :mask="dateTimeMask" flat cover />
                  </div>
                  <div class="q-pa-sm row items-center justify-end">
                    <q-btn v-close-popup label="Close" color="secondary" flat />
                  </div>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>

          <!-- Culprit -->
          <q-input v-model="formData.culprit" label="Who said it?" :rules="requiredString" data-cy="quote-add-culprit">
            <template #before>
              <q-icon :name="mdiAccount" />
            </template>
          </q-input>

          <!-- Quote -->
          <q-input v-model="formData.quote" label="What was said?" type="textarea" :rules="requiredString" data-cy="quote-add-quote">
            <template #before>
              <q-icon :name="mdiFormatQuoteOpen" />
            </template>
          </q-input>

          <!-- Actions -->
          <q-card-actions align="right">
            <q-btn color="positive" :icon="mdiPlus" label="Create" type="submit" :disable="isSubmitting" :loading="isSubmitting" data-cy="quote-add-submit" />
            <q-btn color="secondary" label="Cancel" type="reset" :disable="isSubmitting" flat />
          </q-card-actions>
        </q-form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useDialogPluginComponent } from 'quasar'
import { mdiAccount, mdiCalendar, mdiClock, mdiFormatQuoteOpen, mdiPlus } from '@quasar/extras/mdi-v7'
import { DateTime } from 'luxon'

import { mapError, useApi } from '@/api'
import type { CreateQuoteRequest } from '@/api/domains/quotes'
import { useNotifications } from '@/composables/notifications'
import { requiredString, type ValidationRules } from '@/helpers/validation'
import { dateTimeFormat, dateTimeMask } from '@/config/dates'

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent()
defineEmits([...useDialogPluginComponent.emits])

const isSubmitting = ref(false)
const dateValidationRules: ValidationRules = [
  (val: string) => DateTime.fromFormat(val, dateTimeFormat).isValid || 'Must be a valid date',
  (val: string) => DateTime.fromFormat(val, dateTimeFormat).diffNow().toMillis() <=0 || 'Cannot be in the future',
]
const formData = reactive({
  culprit: '',
  quote: '',
  date: DateTime.now().toFormat(dateTimeFormat),
})

const api = useApi()
const onSubmit = async() => {
  isSubmitting.value = true

  const request: CreateQuoteRequest = {
    culprit: formData.culprit,
    quote: formData.quote,
    date: DateTime.fromFormat(formData.date, dateTimeFormat),
  }

  return api.quotes.create(request)
    .then(() => {
      const { success } = useNotifications()
      success('Quote created')
      onDialogOK()
    })
    .catch(err => {
      const { error } = useNotifications()
      error(`Could not save quote: ${mapError(err).text}`)
    })
    .then(() => {
      isSubmitting.value = false
    })
}
</script>

<style lang="scss" scoped>
.q-dialog .q-card {
  width: 500px;
}
</style>
