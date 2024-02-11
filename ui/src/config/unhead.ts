import { createHead, useHead } from 'unhead'

createHead()
useHead({
  titleTemplate: (title?: string) => (title ? `${title} :: ` : '') + 'Backstage Technical Services',
})