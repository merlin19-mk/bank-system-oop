# MVP Release Notes (v1.0.0)

Developer: Franck Merlin

## Completed Scope (Part II)
- Layered Java console architecture (presentation, business, data).
- Customer onboarding with mandatory checking account.
- Additional account request and manager approval workflow.
- Transactions: deposit, withdrawal, and own-account transfers.
- Checking transaction fee rule after monthly free transaction quota.
- Transaction history and account balance inquiry.
- Manager report generation and customer/account closure operations.
- File persistence snapshot, notifications, audit log, and statement generation.

## Deferred Scope (Part III)
- Database-backed repository implementation.
- Public static CRUD bridges in business classes targeting database layer.
- GUI client replacing or complementing console presentation.

## Delivered Part III Starter
- Database abstraction introduced (`DatabaseGateway`).
- In-memory database implementation for development (`InMemoryDatabaseGateway`).
- Repository CRUD classes for customer/account/transaction records.
- Static CRUD bridge points for customer, account, and transaction business models.

## Migration Path to Part III
1. Introduce repository interfaces in `business` layer.
2. Keep `FileDataStore` as one implementation and add DB implementations.
3. Refactor `BankService` construction with dependency injection.
4. Add GUI app that reuses existing business services.
