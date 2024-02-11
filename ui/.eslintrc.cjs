/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution')

module.exports = {
  root: true,
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/eslint-config-typescript',
  ],
  overrides: [
    {
      files: [
        'cypress/e2e/**/*.{cy,spec}.{js,ts,jsx,tsx}',
      ],
      extends: [
        'plugin:cypress/recommended',
      ],
    },
    {
      files: [
        'src/**/*.spec.ts',
      ],
      rules: {
        '@typescript-eslint/ban-ts-comment': 'off',
      },
    },
  ],
  parserOptions: {
    ecmaVersion: 'latest',
  },
  rules: {
    'no-console': ['warn'],
    'no-debugger': ['warn'],
    eqeqeq: ['error', 'smart'],
    curly: ['error'],
    camelcase: ['error'],
    indent: ['warn', 2],
    quotes: ['error', 'single'],
    // 'no-unused-vars': 'off', // Disable the eslint rule in favour of the typescript-eslint rule
    'space-before-function-paren': ['warn', 'never'],
    'object-curly-spacing': ['warn', 'always'],
    'array-bracket-spacing': ['warn', 'never'],
    'comma-dangle': ['warn', 'always-multiline'],

    // Typescript rules
    // '@typescript-eslint/array-type': ['error', 'array'],
    '@typescript-eslint/ban-ts-comment': 'error',
    '@typescript-eslint/consistent-generic-constructors': ['error', 'constructor'],
    '@typescript-eslint/consistent-indexed-object-style': ['error', 'record'],
    '@typescript-eslint/consistent-type-assertions': ['error', {
      assertionStyle: 'as',
      objectLiteralTypeAssertions: 'allow-as-parameter',
    }],
    '@typescript-eslint/consistent-type-definitions': ['warn', 'type'],
    '@typescript-eslint/method-signature-style': ['warn', 'property'],


    // Vue.js rules
    'vue/multi-word-component-names': ['off'],
  },
}
