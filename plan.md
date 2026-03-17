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