describe('the terms and conditions page', () => {
  it('is accessible', () => {
    cy.visit('/legal/terms-conditions')

    cy.title().should('contain', 'Terms and Conditions')
    cy.contains('Terms and Conditions')
  })

  it('is accessible from old url', () => {
    cy.visit('/contact/book/terms')

    cy.url().should('eq', `${Cypress.config('baseUrl')}/legal/terms-conditions`)
  })
})
