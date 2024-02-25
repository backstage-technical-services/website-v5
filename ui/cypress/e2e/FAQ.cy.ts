describe('the FAQ page', () => {
  it('is accessible', () => {
    cy.visit('/faq')

    cy.expectTitle('Frequently Asked Questions')
    cy.expectHeader('Frequently Asked Questions')
    cy.expectBreadcrumbs('Frequently Asked Questions')
  })

  it('is accessible from old url', () => {
    cy.visit('/page/faq')

    cy.url().should('eq', `${Cypress.config('baseUrl')}/faq`)
  })
})
