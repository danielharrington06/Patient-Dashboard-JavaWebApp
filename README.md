# Patient Data Java Web App

This Java Web App uses the MVC (Model View Controller) design pattern, separated under `model/`, `servlets/` and `webapp/`, respectively. This design pattern means that model and view only interact with View, rather than needing to know about each other.

# Basic Features
- `Column` and `DataFrame` classes, to hold the dataset
- CSV loading via `CSVLoader`
- `Model` class with singleton pattern
- Search operations
- Data operations, enabled by filtering and sorting (for example, sort by ascending date of birth)
- Add, edit, delete patients, with file persistence
- Export the current dataset as JSON
- Statistics page, showing numerical insights and graphs

# Advanced Features
- Input validation for add/edit patient record (client and server side)
- Pagination, to handle large datasets without overwhelming the browser
- Indexing of user ID, for efficient accessing of the row number
- Filtering (narrows results by useful criteria such as gender, marital status, ethnicity etc.)
- Server-side session state tracking - server tracks the current page, filters and search string so that when patient records are viewed and edited, returning to the patient list retains the search parameters
- Dataset switching from within the web app


# ==Need to Mention==
- why util classes
- how model structured