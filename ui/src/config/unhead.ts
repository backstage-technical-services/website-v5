import { createHead, useHead } from '@unhead/vue'

const head = createHead()
useHead({
  titleTemplate: (title?: string) => (title ? `${title} :: ` : '') + 'Backstage Technical Services',
})

export default head
