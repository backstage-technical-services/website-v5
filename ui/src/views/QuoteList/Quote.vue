<template>
  <div class="q-pa-md col-12">
    <q-card class="bg-transparent" flat>
      <q-card-section horizontal>
        <!-- Voting -->
        <q-card-section class="column justify-center text-center q-pa-none" style="min-width: 2.5em;">
          <q-btn
              :icon="mdiChevronUp"
              size="md"
              :color="quote.userVote === 'UPVOTE' ? undefined : 'grey-8'"
              class="q-pa-none"
              @click="onUpvote"
              flat
              v-if="canVote">
            <q-tooltip>Upvote</q-tooltip>
          </q-btn>
          <div :class="{ 'text-body2': true, 'text-grey-6': quote.rating === 0, 'text-red-4': quote.rating < 0, 'text-green-4': quote.rating > 0 }">{{ quote.rating }}</div>
          <q-btn
              :icon="mdiChevronDown"
              size="md"
              :color="quote.userVote === 'DOWNVOTE' ? undefined : 'grey-8'"
              class="q-pa-none"
              @click="onDownvote"
              flat
              v-if="canVote">
            <q-tooltip>Downvote</q-tooltip>
          </q-btn>
        </q-card-section>

        <!-- Quote -->
        <q-card-section class="col-grow row items-start q-pl-none">
          <div class="col-auto">
            <q-icon class="col" size="xl" color="grey-9" :name="mdiFormatQuoteOpen" />
          </div>

          <div class="col-grow">
            <div class="q-pt-sm q-pb-md" style="white-space: pre-wrap">{{ quote.quote }}</div>

            <div class="text-caption text-grey-5">&mdash; {{ quote.culprit }}, {{ quote.date.toRelative() }}</div>
          </div>
        </q-card-section>

        <!-- Delete -->
        <q-card-actions class="text-grey-6 items-start" v-if="canDelete">
          <q-btn :icon="mdiDelete" size="md" color="red-8" class="q-mt-md" @click="onDelete" flat v-if="canDelete">
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

import type { DefaultQuoteResponse } from '@/api/domains/quotes'
import { mapError, useApi } from '@/api'
import { useNotifications } from '@/composables/notifications'
import { can } from '@/helpers/auth'
import { permissions } from '@/config/auth'
import { Dialog } from 'quasar'

const props = defineProps<{ quote: DefaultQuoteResponse}>()
const { quote } = toRefs(props)
const emit = defineEmits(['refresh'])

const canVote = computed(() => can(permissions.quotes.vote))
const canDelete = computed(() => can(permissions.quotes.delete))

const api = useApi()
const onUpvote = () => api.quotes.upvote(quote.value.id)
  .then(() => {
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Could not upvote quote: ${mapError(err).text}`)
  })
const onDownvote = () => api.quotes.downvote(quote.value.id)
  .then(() => {
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Could not downvote quote: ${mapError(err).text}`)
  })
const onDelete = () => Dialog.create({
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
