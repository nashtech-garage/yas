# Evidence: Week 2 Developer Test Flow (Jenkins CI Build)

- **Evidence ID**: EVID-01 / EVID-05
- **Status**: `UNVERIFIED_RUNTIME`

---

## 1. What Evidence to Capture
* Jenkins pipeline trigger execution logs for the branch `project02/test-commit-id-image`.
* Logs from the "Detect Changed Services" stage verifying the detection of changes in the `cart` service.
* Build steps executing docker build command with commit-id tag.

---

## 2. Exact UI Path or Command
1. Open Jenkins Console UI.
2. Select the Job: `yas` / Multibranch Pipeline.
3. Select the Branch tab: `project02/test-commit-id-image`.
4. Click on the latest build number (e.g., `#1`).
5. Click **Console Output** or open Blue Ocean view.

---

## 3. Expected Result
* Log output should show:
  ```text
  Checking changes for branch project02/test-commit-id-image
  ...
  Affected service detected: cart
  ...
  [cart] Running shell script: docker build -t <dockerhub-username>/cart:13f4c2a2 -f cart/Dockerfile cart/
  ...
  [cart] Running shell script: docker push <dockerhub-username>/cart:13f4c2a2
  ```

---

## 4. Actual Result Placeholder
```text
[INSERT ACTUAL JENKINS CONSOLE LOGS HERE ONCE PIPELINE RUNS]
```

---

## 5. Screenshot Placeholder
*(Insert screenshot of Jenkins build history and console output here)*
```text
[IMAGE PLACEHOLDER: evidence/jenkins/branch-ci-build.png]
```
