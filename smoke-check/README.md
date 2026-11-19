# Smoke check

Replays a fixed set of requests against a running application and compares the normalised responses with the ones recorded from the original stack (Java 8, Elasticsearch 7.10, Logstash 7.10, MySQL 8, with the provided dump). It covers trademark, design, dispute, Nice classification and patent pages and three Excel exports.

Needs only a JDK 11 or newer (the provided JDK 21 works):

```
java smoke-check/SmokeCheck.java check                         # http://localhost:8080/pdb
java smoke-check/SmokeCheck.java check http://host:port/pdb    # another address
java smoke-check/SmokeCheck.java check http://localhost:8080/pdb tm-details ds-export   # only these requests
```

Exit code 0 means every request matched; 1 means at least one differs, with the first differing lines printed. HTML pages are compared as links plus visible text (scripts, styles, comments, whitespace and session ids ignored); Excel files as the text of their rows.

`requests.txt` lists the requests (`name`, tab, path), `expected/` holds the recorded responses. The patent pages return no rows because the dump contains no patent data; they only prove that the pages render and the patent database is reachable. A difference is not automatically a defect: judge it, and say in `NOTES.md` which differences are intended.
