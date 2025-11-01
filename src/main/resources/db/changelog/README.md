# Database Changelog

This directory contains Liquibase changelog files with automatic discovery.

## Structure

- `db.changelog-master.xml` - Master changelog that automatically includes all SQL files
- `sql/` - Directory containing all SQL changelog files
  - `001-init-company-data.sql` - Initial company master data setup

## How It Works

The master changelog uses `<includeAll>` to automatically discover and include all SQL files in the `sql/` directory that match the pattern `001-*.sql`. This means:

1. **No hardcoding**: New files are automatically picked up
2. **Ordered execution**: Files are executed in alphabetical order
3. **Pattern matching**: Only files matching `001-*.sql` pattern are included

## Adding New Migrations

1. Create a new SQL file in the `sql/` directory with format: `XXX-description.sql` where XXX is the next sequential number
2. Add the Liquibase formatted SQL header:
   ```sql
   --liquibase formatted sql
   
   --changeset author:changeset-id
   --comment: Description of the change
   ```
3. Add your SQL statements
4. Optionally add rollback statements:
   ```sql
   --rollback [SQL statements for rollback]
   ```
5. The file will be automatically discovered and executed on next application startup

## Configuration

The current Liquibase configuration in application.yml:
```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true
    default-schema: public
```

## Benefits

- **Automatic discovery**: No need to manually update master files
- **Scalable**: Easy to add new migrations
- **Organized**: All SQL files in dedicated directory
- **Flexible**: Easy to change file patterns if needed

## Sample Data

The initial migration creates two sample companies:
1. Demo Company Pvt Ltd (GSTIN: 29ABCDE1234F1Z5)
2. Tech Solutions Pvt Ltd (GSTIN: 29XYZAB5678G2H9)
