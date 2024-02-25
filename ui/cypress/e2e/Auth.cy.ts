describe('authentication', () => {
  it('should allow you to log in and log out', () => {
    cy.visit('/')

    cy.login()

    cy.testRoute('')
    cy.showDrawer()
    cy.dataCy('profile-email').contains(Cypress.env('AUTH0_USERNAME'))
    cy.dataCy('logout-btn').should('be.visible')

    cy.dataCy('logout-btn').click()
    cy.showDrawer()
    cy.dataCy('login-btn').should('be.visible')
    cy.dataCy('profile-email').should('not.exist')
  })
})
