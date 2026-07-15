# Contributing to BasicX

BasicX uses GitHub issues and pull requests.

## Development requirements

- Java 25
- The checked-in Gradle wrapper
- A disposable Paper 26.2 experimental server for integration testing

## Pull requests

1. Create a focused branch from the default branch.
2. Add regression tests for behavior changes.
3. Run `./gradlew clean build --warning-mode all`.
4. Run the relevant steps in `docs/SMOKE_TEST.md`.
5. Update configuration, permissions, and documentation when behavior changes.
6. Open a pull request using the repository template.

Do not commit server files, secrets, generated build output, or production player data.

## Bug reports

Include the exact Paper build, Java version, BasicX version, optional plugin versions, reproducible steps, expected behavior, actual behavior, and a sanitized log excerpt.

## Security

Do not disclose vulnerabilities in a public issue. Follow [SECURITY.md](SECURITY.md).

By contributing, you agree that your contribution is licensed under the repository's MIT License.
