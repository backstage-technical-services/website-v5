describe('the landing page', () => {
  it('renders correctly', () => {
    cy.visit('/')
    cy.title().should('contain', 'Home')

    cy.get('.q-carousel').should('have.length', 1)

    cy.contains('Book us now')
  })
})


