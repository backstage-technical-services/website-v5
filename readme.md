<div align="center">

### BTS Website v5

![License](https://img.shields.io/github/license/backstage-technical-services/website-v5?style=flat-square)
[![Workflow Status](https://img.shields.io/github/actions/workflow/status/backstage-technical-services/website-v5/build.yml?branch=main&style=flat-square)](https://github.com/backstage-technical-services/website-v5/actions/workflows/build.yml)
[![Issues](https://img.shields.io/github/issues/backstage-technical-services/website-v5?style=flat-square)](https://github.com/backstage-technical-services/website-v5/issues)

---
</div>

## 🧐 About

This is a work-in-progress build of v5 of the BTS website, which migrates to a Vue.js SPA and Quarkus API.

## 🏁 Getting Started

### Prerequisites

Before you start developing, please make sure you have the following set up on your computer.

> **Note:** While it is possible to use all of these tools and work on the website on Windows, it is strongly
> recommended that you use a Linux VM or the Windows Subsystem for Linux in order to take advantage of the tooling we
> use to simplify some processes.

- [asdf](https://asdf-vm.com/guide/getting-started.html), along with the following plugins:
  - [java](https://github.com/halcyon/asdf-java)
  - [nodejs](https://github.com/asdf-vm/asdf-nodejs)
  - [yarn](https://github.com/twuni/asdf-yarn)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download), along with the following plugins:
  - Quarkus
- SSH key
- GPG key (optional)

See the [prerequisites doc](docs/prerequisites.md) for more details.

### Installing

1. Clone the repository:

   ```shell
   git clone git@github.com:backstage-technical-services/website-v5.git
   ```
   
   This will be downloaded into a `website-v5` folder, in your current directory.

2. Install the correct versions of the tools

   ```shell
   asdf install
   ```

3. Install the UI's dependencies

   ```shell
   yarn --cwd ui install
   ```
   
4. Create an `.env` file in the `api` directory - you can ask the `#website-general` Discord channel for details

## 🎈 Usage

### Using run configurations

If you are using IntelliJ IDEA you can use the included [run configurations][intellij-run-configs] to run the API and
UI, along with the tests and checks:

**API**
- `API - Dev Server`: start the API on port 8080 in development mode. This will use Docker to automatically run and
  configure the database, and will automatically reload when you make changes.
- `API - Tests`: run the unit test suite.
- `API - Lint`: run linting to check for code style errors.

**UI**
- `UI - Dev Server`: start the UI on port 8000 in development mode. This will automatically reload when you make
  changes.
- `UI - Unit Tests`: run the unit test suite.
- `UI - E2E Tests`: run the end-to-end test suite (see [this doc][cypress-app] for details).
- `UI - Lint`: run linting to check for code style errors.
- `UI - Check Types`: check the types are correct.

### Running manually

Alternatively you can manually run these in the terminal; just make sure you have navigated to the correct
directory (`cd api` or `cd ui`):

**API**
- `./gradlew quarkusDev`: start the API in development mode
- `./gradlew test`: run the API's test suite
- `./gradlew detekt`: run linting on the API's code

**UI**
- `yarn dev`: start the UI in development mode
- `yarn test:unit`: run the UI's unit tests and watch for changes
- `yarn test:e2e`: run the end-to-end test suite
- `yarn check:lint`: run linting to check for code style errors
- `yarn check:types`: check the types are correct

## 🚀 CI/CD

This uses GitHub Actions to automatically run checks for pull requests and on merge/push to `main`:

**API**
- Lint code using Detekt
- Run tests

**UI**
- Check the types are valid
- Lint code using ESLint
- Run unit tests
- Run end-to-end (Cypress) tests

## ⛏️ Built Using

**API**
- [Kotlin](https://kotlinlang.org/)
- [Quarkus](https://quarkus.io/)
- [Jackson](https://github.com/FasterXML/jackson)
- [Jacoco](https://www.jacoco.org/jacoco/)
- [Detekt](https://detekt.dev/)

**UI**
- [Vue.js](https://vuejs.org/)
- [Typescript](https://www.typescriptlang.org/)
- [Quasar](https://quasar.dev/)
- [Cypress](https://www.cypress.io/)
- [ESLint](https://eslint.org/)

[intellij-run-configs]: https://www.jetbrains.com/help/idea/run-debug-configuration.html
[cypress-app]: https://docs.cypress.io/guides/core-concepts/cypress-app
