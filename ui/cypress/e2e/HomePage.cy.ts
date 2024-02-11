describe('the home page', () => {
  it('visits the app root url', () => {
    cy.visit('/')
    cy.title().should('contain', 'Backstage Technical Services')
  })
})
