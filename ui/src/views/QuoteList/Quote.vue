<template>
  <div class="q-pa-md col-12">
    <q-card class="bg-transparent" flat>
      <q-card-section horizontal>
        <q-card-section class="column justify-center text-center q-pa-none" style="min-width: 2.5em;">
          <q-btn :icon="mdiChevronUp" size="md" color="grey-6" class="q-pa-none" @click="likeQuote" flat v-if="canLike">
            <q-tooltip>Upvote</q-tooltip>
          </q-btn>
          <div class="text-body2 text-grey-6">{{ quote.rating }}</div>
          <q-btn :icon="mdiChevronDown" size="md" color="grey-6" class="q-pa-none" @click="dislikeQuote" flat v-if="canLike">
            <q-tooltip>Downvote</q-tooltip>
          </q-btn>
        </q-card-section>

        <q-card-section class="col-grow row items-start q-pl-none">
          <div class="col-auto">
            <q-icon class="col" size="xl" color="grey-9" :name="mdiFormatQuoteOpen" />
          </div>

          <div class="col-grow">
            <div class="q-pt-sm q-pb-md" style="white-space: pre-wrap">{{ quote.quote }}</div>

            <div class="text-caption text-grey-5">&mdash; {{ quote.culprit }}, {{ quote.date.toRelative() }}</div>
          </div>
        </q-card-section>

        <q-card-actions class="text-grey-6 items-start" v-if="canDelete">
          <q-btn :icon="mdiDelete" size="md" class="q-mt-md" @click="deleteQuote" flat v-if="canDelete">
            <q-tooltip>Delete</q-tooltip>
          </q-btn>
        </q-card-actions>
      </q-card-section>


    </q-card>
  </div>
</template>

<script lang="ts" setup>
import { computed, toRefs } from 'vue'
import {
  mdiChevronDown,
  mdiChevronUp,
  mdiDelete,
  mdiFormatQuoteOpen,
} from '@quasar/extras/mdi-v7'

import type { QuoteResponse } from '@/api/domains/quotes'
import { mapError, useApi } from '@/api'
import { useNotifications } from '@/composables/notifications'
import { can } from '@/helpers/auth'
import { permissions } from '@/config/auth'
import { Dialog } from 'quasar'

const props = defineProps<{ quote: QuoteResponse}>()
const { quote } = toRefs(props)
const emit = defineEmits(['refresh'])

const canLike = computed(() => can(permissions.quotes.like))
const canDelete = computed(() => can(permissions.quotes.delete))

const api = useApi()
const likeQuote = () => api.quotes.like(quote.value.id)
  .then(() => {
    const { success } = useNotifications()
    success('Upvoted quote')
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Could not upvote quote: ${mapError(err).text}`)
  })
const dislikeQuote = () => api.quotes.dislike(quote.value.id)
  .then(() => {
    const { success } = useNotifications()
    success('Downvoted quote')
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Could not downvote quote: ${mapError(err).text}`)
  })
const deleteQuote = () => Dialog.create({
  title: 'Confirm',
  message: 'Are you sure you want to delete this quote?',
  persistent: true,
  ok: {
    color: 'positive',
  },
  cancel: {
    color: 'secondary',
    flat: true,
  },
}).onOk(() => api.quotes.delete(quote.value.id)
  .then(() => {
    const { success } = useNotifications()
    success('Quote deleted')
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Could not delete quote: ${mapError(err).text}`)
  }))
</script>
