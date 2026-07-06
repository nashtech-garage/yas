# Service Mesh Tasks (to be done with Partner B)

- Configure **AuthorizationPolicy** (example: only allow `order-service` to call `product-service`, block other services).

- Install **Kiali**, expose the dashboard, and capture the **Topology flow chart**.

- Write a detailed **Test plan** (test scenarios with `curl` commands from a pod).

- Execute tests: record success logs (allow), failure logs (deny), and logs demonstrating **retry** (send a request that triggers a 500 error and observe automatic retries).
