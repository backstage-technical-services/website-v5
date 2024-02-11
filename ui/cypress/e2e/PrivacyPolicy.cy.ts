describe('the privacy policy page', () => {
  it('is accessible', () => {
    cy.visit('/legal/privacy-policy')

    cy.title().should('contain', 'Privacy Policy')
    cy.contains('Privacy Policy')
  })

  it('is accessible from old url', () => {
    cy.visit('/page/privacy-policy')

    cy.url().should('eq', `${Cypress.config('baseUrl')}/legal/privacy-policy`)
  })
})
