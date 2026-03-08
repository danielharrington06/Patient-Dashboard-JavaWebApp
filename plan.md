# Upcoming features
- sort search results
- deal with search term "abington" which returns patients with current address matching but also birthplace
- add filters such as for gender, whether patient is alive, ethnicity, race, marital status
- requirement 7: functionality like oldest person, people living in same place

# Something to consider
- use git branches
```bash
# create new branch for a feature
git checkout -b feature/{featurename}
# first push on new branch
git push -u origin feature/{featurename}
# when done do the following
git checkout main
git merge --no-ff feature/{featurename} -m "{message}"
# or
git merge --no-ff --no-edit feature/{featurename}
git branch -d feature/{featurename}
```

# Plan for search
2. filter
3. sort

sorting:
    clear sorting button
    make it so you can click on the entire box to sort, not just name