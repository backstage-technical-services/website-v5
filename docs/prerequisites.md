# Prerequisites

## asdf

You will need to install [asdf][install-asdf] along with the following plugins:

- [java](https://github.com/halcyon/asdf-java)
- [nodejs](https://github.com/asdf-vm/asdf-nodejs)
- [yarn](https://github.com/twuni/asdf-yarn)

## Docker

In order to run any auxiliary services (eg, the SMTP server and database) you will need to
have [Docker installed][install-docker].

It's recommended you are familiar with the following:

- [Images and containers][docker-images-vs-containers]
- [Running containers][docker-container-run]
- [Executing commands in containers][docker-container-exec]
- [Docker compose][docker-compose-docs]

If you want to learn more, Docker's documentation is fantastic:

- [Docker][docker-docs]
- [Docker Compose CLI][docker-docs-compose-cli]
- [Docker Compose Files][docker-docs-compose-file]

## IDEs

At the heart of good development is a good IDE (Integrated Development Environment). We recommend
using [IntelliJ IDEA][intellij-idea] by JetBrains; students are able to get their entire suite for free if you provide
proof of registration.

You can also use Microsoft's [VS Code][vscode].

It's also recommended that you install a database management tool, to help with any testing and to back-up or restore
data. You can either use the functionality built into IntelliJ IDEA or [DataGrip][intellij-datagrip], which is
JetBrain's dedicated database client.

## SSH Key

You can follow [this guide][ssh-create] to create an SSH key if you do not already have one,
and [this guide][ssh-github] to add it to your GitHub account.

## GPG Key

- Follow [this guide][gpg-create] to create a new GPG key (make sure it is RSA 4096)
- Follow [this guide][gpg-github] to add it to GitHub
- You can sign a commit by using the `-S` flag
- You can configure git sign all commits for the current repository with

  ```shell
  git config commit.gpgsign true 
  ```
- You can configure git to sign all commits for all repositories with

  ```shell
  git config --global commit.gpgsign true
  ```

[install-asdf]: https://asdf-vm.com/guide/getting-started.html
[install-docker]: https://docs.docker.com/install
[intellij-idea]: https://www.jetbrains.com/idea/download
[intellij-datagrip]: https://www.jetbrains.com/datagrip/download
[vscode]: https://code.visualstudio.com/download
[docker-compose-docs]: https://docs.docker.com/compose
[docker-container-exec]: https://docs.docker.com/engine/reference/commandline/container_exec
[docker-docs-compose-file]: https://docs.docker.com/compose/compose-file
[docker-docs-compose-cli]: https://docs.docker.com/compose/reference
[docker-docs]: https://docs.docker.com/engine/reference/commandline/cli
[docker-container-run]: https://docs.docker.com/engine/reference/commandline/container_run
[docker-images-vs-containers]: https://stackoverflow.com/a/23736802
[ssh-create]: https://docs.github.com/en/github/authenticating-to-github/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent
[ssh-github]: https://docs.github.com/en/github/authenticating-to-github/adding-a-new-ssh-key-to-your-github-account
[gpg-create]: https://docs.github.com/en/github/authenticating-to-github/generating-a-new-gpg-key
[gpg-github]: https://docs.github.com/en/github/authenticating-to-github/adding-a-new-gpg-key-to-your-github-account
