# Upcoming features
- take out:
  - check through all classes and check method visibility
  - check model doesnt need stuff extracted any more

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