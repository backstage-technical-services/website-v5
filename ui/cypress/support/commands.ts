/// <reference types="cypress" />

declare global {
  namespace Cypress {
    // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
    interface Chainable {
      login: (username?: string, password?: string) => Chainable
      showDrawer: () => Chainable
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
  },
)
