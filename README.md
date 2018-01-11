# trendtracker

## Environment

Set `DB_HOST`, `DB_PORT` (defaults "5432"), `DB_NAME`, `DB_USER`, and
`DB_PASSWORD` for the Postgres database.

When running the jar, select desired port with `PORT` env var, or `java
-Dport=10559 -jar ...`.


## Development

`boot dev` will start a dev system repl in the `user` namespace. `(in-ns
'boot.user)` and then `(start-repl)` will connect to the browser repl (to be
improved).
