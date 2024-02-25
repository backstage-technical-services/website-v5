describe('the quotes board', () => {
  beforeEach(() => {
    cy.login()
    cy.visit('/quotes-board')
  })

  it('should be accessible', () => {
    cy.expectTitle('Quotes Board')
    cy.expectBreadcrumbs('Quotes Board')
    cy.dataCy('quote-add-btn').should('be.visible')

    cy.dataCy('quote-list')
      .find('.q-card')
      .should('have.length.at.least', 1)

    cy.dataCy('quote-list')
      .dataCy('quote-upvote-btn')
      .should('have.length.at.least', 1)

    cy.dataCy('quote-list')
      .dataCy('quote-downvote-btn')
      .should('have.length.at.least', 1)
  })

  it('allows you to add a quote', () => {
    cy.dataCy('quote-add-btn').click()

    cy.dataCy('quote-add-dialog').should('be.visible')
    cy.dataCy('quote-add-date-btn').click()
    cy.dataCy('quote-add-date-selector').should('be.visible')
    cy.dataCy('quote-add-culprit').type('The culprit')
    cy.dataCy('quote-add-quote').type('A really embarrassing or funny quote')
    cy.dataCy('quote-add-submit').click()

    cy.notification('Quote created')

    cy.dataCy('quote-item-details').contains('The culprit')
    cy.dataCy('quote-item-details').contains('A really embarrassing or funny quote')
  })

  it('allows you to delete a quote', () => {
    cy.dataCy('quote-list')
      .get('.q-card')
      .first()
      .dataCy('quote-delete-btn')
      .click()

    cy.withinDialog((e) => {
      cy.dataCy('confirm-delete').click()
    })

    cy.notification('Quote deleted')
  })
})
