# Evidence: Week 2 Docker Hub Commit-ID Tagged Image

- **Evidence ID**: EVID-02
- **Status**: `UNVERIFIED_RUNTIME`

---

## 1. What Evidence to Capture
* Screenshot or API return of the Docker Hub repository under your workspace namespace (e.g. `<namespace>/cart`).
* Listing of tags verifying the existence of tag `13f4c2a2` corresponding to the test commit.

---

## 2. Exact UI Path or Command
1. Open a web browser and navigate to `https://hub.docker.com/`.
2. Log in and go to: `https://hub.docker.com/r/<namespace>/cart/tags`.

---

## 3. Expected Result
* The tag `13f4c2a2` should be present under the `cart` repository tag list.
* The "Last updated" timestamp should match the test run time.

---

## 4. Actual Result Placeholder
```text
[INSERT DOCKER HUB REGISTRY METADATA OR API RESPONSE HERE]
```

---

## 5. Screenshot Placeholder
*(Insert screenshot showing the commit ID tag pushed to Docker Hub)*
```text
[IMAGE PLACEHOLDER: evidence/dockerhub/commit-tag.png]
```
