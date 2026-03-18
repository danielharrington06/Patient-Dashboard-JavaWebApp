# Patient Data Java Web App

This Java Web App uses the MVC (Model View Controller) design pattern, separated under `model/`, `servlets/` and `webapp/`, respectively. This design pattern means that model and view only interact with View, rather than needing to know about each other.

# Compile and Run
- Run in the terminal:
```bash
mvn clean compile exec:exec
```
- Then visit:
```
http://localhost:8080
```

# Features
- Requirements 1-10 implemented
- Input validation for add/edit patient record, both client and server side
- Pagination, to handle large datasets without overwhelming the browser
- Indexing of patient ID, for efficient accessing of the row number
- Results filtering to narrow results by useful criteria such as gender, marital status, ethnicity etc
- Server-side session state tracking, tracking the current page, filters and search string so after viewing/editing a record, these search parameters are retained
- Dataset switching from within the web app

# Design

# ==Need to Mention==
- why util classes
- how model structured
- made things private
- extract class refactoring - delegation