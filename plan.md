# Upcoming features

- add filters such as for gender, whether patient is alive, ethnicity, race, marital status
- sort search results
- pagination
- improve headings on patient record page
- split search terms by spaces and return results that match on each individual term
- deal with search term "abington" which returns patients with current address matching but also birthplace
- requirement 7: functionality like oldest person, people living in same place

# Something to consider
- use git branches
```bash
# create new branch for a feature
git checkout -b feature/{featurename}
# when done do the following
git checkout main
git merge feature/{featurename}
git branch -d feature/{featurename}
```

```bash
main
 ├── feature/search-improvements
 ├── feature/filtering
 ├── feature/pagination
 ├── feature/analytics
```

# Plan for search
1. search
2. filter
3. sort
4. paginate