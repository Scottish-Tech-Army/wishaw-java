# Local DB Access

## H2 profile

When the backend runs with the default `h2` profile, it uses an in-memory H2 database.

1. Start the backend on `http://localhost:8080`.
2. Open `http://localhost:8080/h2-console` in the browser.
3. Use these connection settings:
   - JDBC URL: `jdbc:h2:mem:wishaw;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
   - User Name: `sa`
   - Password: leave blank
4. Click `Connect`.

Useful tables:

- `USER_ACCOUNTS`
- `USER_PROFILES`
- `SPORTS`
- `TOURNAMENTS`
- `TOURNAMENT_PARTICIPANTS`
- `MATCH_RECORDS`
- `NOTIFICATION_RECORDS`

Example queries:

```sql
SELECT * FROM USER_ACCOUNTS;
SELECT * FROM USER_PROFILES;
SELECT * FROM SPORTS;
SELECT * FROM TOURNAMENTS;
SELECT * FROM TOURNAMENT_PARTICIPANTS;
```

Age-related fields added for local inspection:

- `USER_PROFILES.DATE_OF_BIRTH`
- `SPORTS.MIN_AGE`
- `SPORTS.MAX_AGE`

Example age-limit query:

```sql
SELECT s.ID, s.NAME, s.MIN_AGE, s.MAX_AGE
FROM SPORTS s;
```

## Postgres profile

When the backend runs with the `postgres` profile, the defaults are:

- Host: `localhost`
- Port: `5432`
- Database: `wishaw`
- User: `wishaw`
- Password: `wishaw`

Example `psql` command:

```powershell
psql -h localhost -U wishaw -d wishaw
```

Then run the same SQL queries shown above.