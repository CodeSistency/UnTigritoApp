## Database best practices for mobile apps

- **Data Model Design**: Design models that support mobile app requirements (offline sync, real-time updates)
- **Timestamps**: Include created_at and updated_at timestamps on all tables for sync and auditing
- **Soft Deletes**: Use soft deletes for data that might need to be restored or synced
- **Indexing Strategy**: Index columns used in mobile app queries, especially for filtering and sorting
- **Data Relationships**: Design relationships that support efficient mobile queries and offline sync
- **Migration Strategy**: Plan migrations carefully to support mobile app version compatibility
- **Performance**: Optimize queries for mobile app usage patterns (smaller result sets, efficient joins)
- **Data Integrity**: Use database constraints to ensure data consistency across mobile and web clients
- **Sync Support**: Design tables to support incremental sync with mobile clients
- **Backup Strategy**: Implement regular backups with point-in-time recovery capabilities
