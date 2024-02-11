import { Notify } from 'quasar'
import { mdiMinusCircle, mdiCheck } from '@quasar/extras/mdi-v7'

export const useNotifications = () => {
  const notAllowed = () => Notify.create({
    type: 'warning',
    icon: mdiMinusCircle,
    message: 'Sorry, you aren\'t allowed to do that',
    textColor: 'white',
  })

  const accessNotAllowed = () => Notify.create({
    type: 'warning',
    icon: mdiMinusCircle,
    message: 'Sorry, you aren\'t allowed to access that',
    textColor: 'white',
  })

  const success = (message: string) => Notify.create({
    type: 'positive',
    icon: mdiCheck,
    message,
  })

  const error = (message: string) => Notify.create({
    type: 'negative',
    message,
  })

  const notify = Notify.create

  return {
    notify,
    accessNotAllowed,
    notAllowed,
    success,
    error,
  }
}
