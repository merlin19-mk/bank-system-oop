# Analysis - Fortis Bank System

## Stakeholders
- Customer: performs account operations and requests services.
- Bank Manager: manages customers, approves account requests, reviews reports.
- Bank Operations Team (future): uses audit outputs and statement exports.

## Core Objects
- Customer: identity, contact details, PIN, status.
- Account: mandatory checking + optional account types.
- Transaction: deposit, withdrawal, transfer, fee, interest entries.
- Notification: operational event and alert records.

## Functional Goals
- Streamline customer onboarding and account management.
- Support secure transaction processing with traceability.
- Provide reportability and monthly statements in MVP form.

## Assumptions for MVP
- Single-user console operation.
- File-based persistence.
- Own-account transfers by default.
- Soft-close lifecycle for customers and accounts.
