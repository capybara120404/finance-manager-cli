# finance-manager-cli

Small command-line application to manage personal finances (expenses, incomes, reports). Java + Maven project.

## Requirements

- JDK 21+
- Maven 3.8+

## Installation

Clone the repository:

```bash
  git clone https://github.com/capybara120404/finance-manager-cli.git
  cd finance-manager-cli
```

## Build

1. Build the project and create an executable jar:
    - `mvn clean package`

2. The built artifact will be in `target/`:
    - `target/finance-manager-cli-1.0-SNAPSHOT.jar`

## Run

- Run the jar:
    - `java -jar target/finance-manager-cli-1.0-SNAPSHOT.jar <command> [options]`

- Run from source (Maven exec):
    - `mvn exec:java -Dexec.mainClass="com.daniil.financemanager.Main" -Dexec.args="<command> [options]"`

## CLI Examples

- Show help:
    - `java -jar target/finance-manager-cli-1.0-SNAPSHOT.jar help`

## Project architecture

Source layout:

- `src/main/java` - application code
    - `com.daniil.financemanager.cli` - CLI entrypoint
    - `com.daniil.financemanager.domain.model` - domain models (Transaction, Category, Budget, User, Wallet)
    - `com.daniil.financemanager.domain.service` - business logic
    - `com.daniil.financemanager.domain.repository` - repository interfaces
    - `com.daniil.financemanager.infrastructure.repository` - in-memory implementations
- `src/test/java` - unit tests
- `pom.xml` - Maven build configuration

## Running tests

- Run all tests:
    - `mvn test`

- Run a single test class:
    - `mvn -Dtest=ClassNameTest test`

## License

MIT
