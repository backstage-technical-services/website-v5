<template>
  <div class="q-pa-md col-12 col-md-6 col-lg-4">
    <q-card flat>
      <q-item class="q-pt-md">
        <q-item-section class="q-mb-md">
          <!-- Name -->
          <div class="text-h5">
            <q-icon :name="mdiTrophy" class="q-mr-sm"/>
            {{ award.name }}
          </div>

          <!-- Suggested by -->
          <span class="text-caption text-grey-6" v-if="award.suggestedBy">
            Suggested by: {{ award.suggestedBy.name }}
          </span>
        </q-item-section>
      </q-item>

      <!-- Description -->
      <q-card-section class="text-grey-5 text-small q-pt-none">
          {{ award.description }}
      </q-card-section>

      <q-separator/>
      <q-card-actions>
        <!-- Is approved -->
        <q-icon size="sm" class="q-ml-xs q-mr-sm" :color="approvedColour" :name="approvedIcon">
          <q-tooltip v-if="award.approved">Approved</q-tooltip>
          <q-tooltip v-else>Needs approval before it can be used</q-tooltip>
        </q-icon>

        <!-- Recurs -->
        <q-icon size="sm" :color="recurringColour" :name="recurringIcon">
          <q-tooltip v-if="award.recurring">Automatically recurs every year</q-tooltip>
        </q-icon>

        <!-- Admin actions -->
        <q-space />
        <q-btn label="Approve" @click="approveAward" flat v-if="canApprove && !award.approved"/>
        <q-btn label="Edit" :to="{name:'awards:edit', params: { id: award.id } }" flat v-if="canEdit"/>
        <q-btn label="Delete" @click="deleteAward" flat v-if="canDelete"/>
      </q-card-actions>
    </q-card>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { mdiCheckCircle, mdiAutorenew, mdiAutorenewOff, mdiTrophy, mdiCloseCircleOutline } from '@quasar/extras/mdi-v7'

import type { AwardResponse } from '@/api/domains/awards'
import { mapError, useApi } from '@/api'
import { permissions } from '@/config/auth'
import { useNotifications } from '@/composables/notifications'
import { can } from '@/helpers/auth'
import { Dialog } from 'quasar'

const props = defineProps<{ award: AwardResponse }>()
const emit = defineEmits(['refresh'])

const canEdit = computed(() => can(permissions.awards.edit))
const canApprove = computed(() => can(permissions.awards.approve))
const canDelete = computed(() => can(permissions.awards.delete))

const approvedIcon = computed(() => props.award.approved ? mdiCheckCircle : mdiCloseCircleOutline)
const approvedColour = computed(() => props.award.approved ? 'grey-6' : 'grey-9')
const recurringIcon = computed(() => props.award.recurring ? mdiAutorenew : mdiAutorenewOff)
const recurringColour = computed(() => props.award.recurring ? 'grey-6' : 'grey-9')

const api = useApi()
const approveAward = () => api.awards.approve(props.award.id)
  .then(() => {
    const { success } = useNotifications()
    success('Award approved')
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Failed to approve award: ${mapError(err).text}`)
  })
const deleteAward = () => Dialog.create({
  title: 'Confirm',
  message: 'Are you sure you want to delete this award?',
  persistent: true,
  ok: {
    color: 'positive',
  },
  cancel: {
    color: 'secondary',
    flat: true,
  },
}).onOk(() => api.awards.delete(props.award.id)
  .then(() => {
    const { success } = useNotifications()
    success('Award deleted')
    emit('refresh')
  })
  .catch(err => {
    const { error } = useNotifications()
    error(`Failed to delete award: ${mapError(err).text}`)
  }))
</script>
