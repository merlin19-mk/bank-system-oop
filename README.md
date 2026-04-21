# Fortis Bank System - Console Application

Developer: Franck Merlin  
Version: v1.0.0 (MVP)  

## Project Scope
This repository contains a solo implementation for the Fortis Bank case study.
The goal is to deliver a functional Java console MVP with layered architecture.

## MVP Boundary
- Included: Part II (business, data file handler, presentation console app)
- Deferred: Part III full database + graphical application

## Architecture Layers
- Business layer: banking entities and service orchestration
- Data layer: file persistence and repository contracts
- Presentation layer: console menus and workflows

## Implemented MVP Features
- Customer onboarding with mandatory checking account creation.
- Multi-account request/approval flow for savings, credit, currency, and line of credit accounts.
- Deposit, withdraw, and same-customer transfer operations.
- Transaction history tracking by account.
- Checking transaction quota with automatic fee application after free monthly operations.
- Manager operations for customer listing, approval handling, closure, and report generation.
- Notification/audit event capture and monthly statement export.
- File-based snapshot persistence in `data/`.

## Run and Build
- Compile (Maven): `mvn -DskipTests compile`
- Run app: `mvn -DskipTests exec:java -Dexec.mainClass=com.fortis.bank.FortisBank`
- Fallback compile (without Maven, PowerShell): `javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName })`

## Data Output
- Runtime snapshot files are written under `data/`:
- `customers.csv`
- `accounts.csv`
- `transactions.csv`
- `pending_requests.csv`
- `notifications.log`
- `audit.log`
- Monthly statements are generated under `data/statements/`.

## Release State
- MVP status: complete for Part II (console + file persistence).
- Deferred to future phase: Part III database layer and graphical client.
- Release notes: [MVP Release Notes](docs/release/README.md)

## Documentation Map
- [Analysis](docs/analysis/README.md)
- [Use Case Diagram](docs/uml/use-case/README.md)
- [Class Diagram](docs/uml/class/README.md)
- [Activity Diagrams](docs/uml/activity/README.md)
