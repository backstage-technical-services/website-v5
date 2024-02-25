/// <reference types="cypress" />

declare global {
  namespace Cypress {
    // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
    interface Chainable {
      login: (username?: string, password?: string) => Chainable
      showDrawer: () => Chainable
      expectTitle: (title: string) => Chainable
      expectHeader: (header: string) => Chainable
      expectBreadcrumbs: (...crumbs: string[]) => Chainable
      notification: (expectedText: string) => Chainable
    }
  }
}

export {}

Cypress.Commands.add('showDrawer', () => {
  cy.dataCy('toggle-drawer-btn').click()
})

Cypress.Commands.add(
  'login',
  (
    username: string = Cypress.env('AUTH0_USERNAME'),
    password: string = Cypress.env('AUTH0_PASSWORD'),
  ) => {
    cy.visit('/')
    cy.showDrawer()
    cy.dataCy('login-btn').click()
    cy.origin(
      Cypress.env('AUTH0_DOMAIN'),
      { args: { username, password } },
      ({ username, password }) => {
        cy.get('input#username').type(username)
        cy.get('input#password').type(password, { log: false, force: true })
        cy.contains('button[value=default]', 'Continue').click()
      },
    )
    cy.expectTitle('Home')
  },
)

Cypress.Commands.add('expectTitle', (title: string) => {
  return cy.title().should('contain', title)
})

Cypress.Commands.add('expectHeader', header => {
  cy.get('.q-page > h1').contains(header)
})

Cypress.Commands.add('expectBreadcrumbs', (...crumbs: string[]) => {
  crumbs.forEach(crumb => {
    cy.dataCy('breadcrumbs').contains(crumb)
  })
})

Cypress.Commands.add('notification', (expectedText) => {
  cy.get('.q-notification').contains(expectedText)
})
