# Patient Data Java Web App

This Java Web App allows a user such as a Hospital Administrator to access and manage (synthetic) data about patients. All records are loaded into a `DataFrame` from `CSV` and can be searched, filtered and sorted. Patients can be added, edited and deleted. A statistics page details useful information about the current dataset. It is possible to change the dataset from the home page.

# Features
- **Requirements 1-10** implemented
- **Input validation** for add/edit patient record (both client and server side)
- **Pagination**, to handle large datasets without overwhelming the browser
- **Indexing** of patient ID, for more efficient access of the row
- **Results filtering** to narrow results by useful criteria such as gender, marital status, ethnicity etc
- **Server-side session state tracking**, so after viewing/editing a record, search parameters are remembered
- **Dataset switching** from within the web app, without needing to recompile

# Design
- Uses the **MVC** (Model View Controller) design pattern, separated under `model/`, `servlets/` and `webapp/`, respectively. 
- `DataFrame` uses a `List` of `Column`s and has an index for more efficient row access
- Servlets in the controller part of MVC use methods from `Model`, some of which are delegated, such as those done by `Statistics`
- Encapsulation followed
- Code made reusable and maintainable through abstraction
- `uk.ac.ucl.util` package holds utility classes with static methods

# Compile and Run
- Run in the terminal:
```bash
mvn clean compile exec:exec
```
- Then visit:
```
http://localhost:8080
```