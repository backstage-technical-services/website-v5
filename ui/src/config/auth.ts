import type { RouteLocationRaw } from 'vue-router'
import { createAuth0 } from '@auth0/auth0-vue'
import { LocalStorage } from 'quasar'

export const redirectPath = '/auth/redirect'
const redirectUrl = `${window.location.origin}${redirectPath}`
const redirectStorageKey = 'authRedirectUrl'
const storeRedirect = () => {
  LocalStorage.set(redirectStorageKey, window.location.pathname + window.location.search)
}
export const getRedirect = (): string | RouteLocationRaw => {
  const redirect = LocalStorage.getItem<string>(redirectStorageKey)

  if (redirect === null) {
    return { name: 'home' }
  } else {
    LocalStorage.remove(redirectStorageKey)
    return redirect
  }
}

const auth0 = createAuth0({
  domain: import.meta.env.VITE_AUTH0_DOMAIN,
  clientId: import.meta.env.VITE_AUTH0_CLIENT_ID,
  cacheLocation: 'localstorage',
  authorizationParams: {
    redirect_uri: redirectUrl, // eslint-disable-line camelcase
    audience: import.meta.env.VITE_API_URL,
  },
})

export const logout = () => {
  storeRedirect()

  return auth0.logout({
    logoutParams: {
      returnTo: redirectUrl,
    },
  })
}

export const login = () => {
  storeRedirect()

  return auth0.loginWithRedirect()
}

export default auth0
export const permissions = {
  quotes: {
    list: 'quote:read',
    add: 'quote:create',
    like: 'quote:like',
    delete: 'quote:delete',
  },
}
