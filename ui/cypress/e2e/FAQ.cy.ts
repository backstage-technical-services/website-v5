describe('the FAQ page', () => {
  it('is accessible', () => {
    cy.visit('/faq')

    cy.title().should('contain', 'Frequently Asked Questions')
    cy.contains('Frequently Asked Questions')
  })

  it('is accessible from old url', () => {
    cy.visit('/page/faq')

    cy.url().should('eq', `${Cypress.config('baseUrl')}/faq`)
  })
})
