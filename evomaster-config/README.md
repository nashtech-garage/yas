# EvoMaster Black-Box Testing — YAS

Automated REST API test generation for the [YAS (Yet Another Shop)](https://github.com/nashtech-garage/yas) microservices using [EvoMaster](https://www.evomaster.org) in black-box mode.

EvoMaster discovers and tests endpoints by reading live OpenAPI specs (`/v3/api-docs`) at runtime — no intermediate spec files needed. Tests are generated as JUnit 5 and organized by service and authentication role, making results directly comparable across experimental conditions.

---

## Folder structure

```
evomaster-config/
├── scripts/
│   ├── auth-config.sh          # Keycloak credentials and token helper functions
│   ├── evomaster-blackbox.sh   # Run EvoMaster for a single service + role
│   ├── run-all.sh              # Orchestrate the full experiment (all services × all roles)
│   └── README.md               # Quick reference
└── generated-tests/
    └── blackbox/
        ├── <service>/
        │   ├── admin/          # Results for admin role
        │   ├── customer/       # Results for customer role
        │   └── none/           # Results without authentication
        └── experiment_<timestamp>.jsonl   # Aggregated experiment summary
```

---

## Prerequisites

### 1. YAS environment running

```bash
# Create the external network required by docker-compose.o11y.yml
docker network create yas-network

# Start all services
cd /path/to/yas
docker compose \
  -f docker-compose.yml \
  -f docker-compose.o11y.yml \
  up -d
```

Wait until all containers are healthy before running EvoMaster.

### 2. `/etc/hosts` entries

Both the host and the EvoMaster Docker container need to reach YAS services by hostname:

```
127.0.0.1  identity api.yas.local
```

### 3. Docker with EvoMaster image

```bash
docker pull webfuzzing/evomaster
```

---

## Quick start

### Run the full experiment (all services × all roles)

```bash
cd evomaster-config/scripts
./run-all.sh
```

This runs **12 services × 3 roles = 36 EvoMaster executions** and writes results to `generated-tests/blackbox/`.

### Run a single service

```bash
./evomaster-blackbox.sh <service> [role]

# Examples
./evomaster-blackbox.sh product admin
./evomaster-blackbox.sh cart customer
./evomaster-blackbox.sh tax none
```

---

## Authentication roles

YAS uses [Keycloak](https://www.keycloak.org) as its identity provider (OAuth2/OIDC). EvoMaster obtains tokens via the Resource Owner Password Credentials (ROPC) flow.

| Role | User | Token scopes | Endpoints covered |
|---|---|---|---|
| `admin` | `admin` / `password` | `ADMIN` + `CUSTOMER` | `/backoffice/**` and `/storefront/**` |
| `customer` | `user` / `password` | `CUSTOMER` only | `/storefront/carts/**`, `/storefront/customer/**` |
| `none` | — | no token | `permitAll` public endpoints |

> The `customer` user (`user`/`password`) does not exist in YAS by default. It is created automatically by `auth-config.sh` (part of this test setup) via the Keycloak Admin API before the first `customer` role run.

### Credential origins

| Credential | Value | Origin |
|---|---|---|
| `admin` application user | `admin` / `password` | Pre-loaded by YAS via `identity/realm-export.json` on first Keycloak boot |
| `customer` application user | `user` / `password` | **Created by this setup** (`auth-config.sh` → `ensure_customer_user_exists`) |
| Keycloak Admin Console | `admin` / `admin` | Defined in YAS `docker-compose.yml` (`KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD`) |
| OAuth2 client | `backoffice-bff` | Defined in YAS `identity/realm-export.json`; secret hardcoded in `auth-config.sh` |

The `customer` user is not part of the original YAS setup. `ensure_customer_user_exists` in `auth-config.sh` calls the Keycloak Admin API to check if the user exists and creates it if not. Keycloak automatically assigns the `CUSTOMER` role via the `default-roles-yas` composite role.

To verify or inspect users and clients, access the Keycloak Admin Console at `http://identity` (realm `Yas`) using `admin`/`admin`.

To manually create the `customer` user without running EvoMaster:

```bash
# Source the config to load the helper functions
source scripts/auth-config.sh
ensure_customer_user_exists
```

---

## Available services

| Service | Context path | Description |
|---|---|---|
| `product` | `/product` | Product catalog, brands, categories, attributes |
| `media` | `/media` | Media file upload and management |
| `customer` | `/customer` | Customer profiles and addresses |
| `cart` | `/cart` | Shopping cart |
| `rating` | `/rating` | Product ratings and reviews |
| `order` | `/order` | Order management |
| `payment` | `/payment` | Payment processing |
| `location` | `/location` | Countries, states and districts |
| `inventory` | `/inventory` | Stock and warehouse management |
| `tax` | `/tax` | Tax classes and rates |
| `promotion` | `/promotion` | Discount rules and coupons |
| `search` | `/search` | Elasticsearch-based product search |

### Why we don't run EvoMaster for `/backoffice` and `/storefront`

In YAS there are two different things:

1. **Backoffice and Storefront BFFs** — Java apps (`backoffice-bff`, `storefront-bff`) exposed as hostnames `backoffice` and `storefront` in nginx. They are session-based BFFs for the UIs and expose only a couple of endpoints (e.g. `/authentication/user`). They **do not** expose OpenAPI (`/v3/api-docs`), so EvoMaster black-box cannot target them.

2. **API gateway** — `api.yas.local` routes only by **microservice** path: `/product/`, `/cart/`, `/customer/`, etc. There are no routes `/backoffice/` or `/storefront/` on the gateway; those prefixes live **inside** each microservice (e.g. `/product/backoffice/brands`, `/product/storefront/products`, `/cart/storefront/carts`).

We already cover the backoffice and storefront **API surface** by running EvoMaster per microservice with the right role: **admin** hits both `/backoffice/**` and `/storefront/**` endpoints, and **customer** hits `/storefront/**` only. So the same endpoints that the BFFs call are the ones we exercise.

### Mapping of selected endpoints

Endpoints you care about map to services and full URLs on `api.yas.local` as follows. All are covered when running EvoMaster for the given service with the indicated role(s).

| Your endpoint | Service | Full URL on api.yas.local | Role(s) |
|---|---|---|---|
| GET /backoffice/ratings/latest/{count} | `rating` | `GET /rating/backoffice/ratings/latest/{count}` | admin |
| GET /storefront/customer/profile | `customer` | `GET /customer/storefront/customer/profile` | admin, customer |
| GET /storefront/cart/items | `cart` | `GET /cart/storefront/cart/items` | admin, customer |
| POST /backoffice/products | `product` | `POST /product/backoffice/products` | admin |
| POST /storefront/ratings | `rating` | `POST /rating/storefront/ratings` | admin, customer |
| POST /storefront/sampledata | `sampledata` | `POST /sampledata/storefront/sampledata` | storefront (no auth or customer) — **not in default service list** |
| DELETE /backoffice/ratings/{id} | `rating` | `DELETE /rating/backoffice/ratings/{id}` | admin |
| DELETE /backoffice/warehouses/{id} | `inventory` | `DELETE /inventory/backoffice/warehouses/{id}` | admin |
| PUT /backoffice/products/subtract-quantity | `product` | `PUT /product/backoffice/products/subtract-quantity` | admin |
| PUT /backoffice/state-or-provinces/{id} | `location` | `PUT /location/backoffice/state-or-provinces/{id}` | admin |

**Note:** `sampledata` is not in the default list of services in `run-all.sh`. The nginx gateway exposes `api.yas.local/sampledata/`. To run EvoMaster for it, add `sampledata` to the service map in `evomaster-blackbox.sh` and to the `SERVICES` array in `run-all.sh`, then run `./evomaster-blackbox.sh sampledata none` (or the role that matches the app’s security rules).

---

## Environment variables

All variables are optional and apply to both `run-all.sh` and `evomaster-blackbox.sh`.

| Variable | Default | Description |
|---|---|---|
| `EVOMASTER_MAX_TIME` | `60` | Max search time per service in seconds |
| `EVOMASTER_RATE` | `60` | Max requests per minute sent to the SUT |
| `EVOMASTER_SEED` | *(random)* | Fixed seed for reproducible runs |
| `YAS_API_URL` | `http://api.yas.local` | Base URL of the API gateway |
| `KEYCLOAK_URL` | `http://identity` | Keycloak base URL |

### Example — longer budget for scientific runs

```bash
EVOMASTER_MAX_TIME=3600 EVOMASTER_SEED=42 ./run-all.sh
```

---

## Output files

Each `<service>/<role>/` directory contains:

| File | Description |
|---|---|
| `run-info.json` | Run metadata: service, role, timestamp, config, and results summary |
| `statistics.csv` | EvoMaster coverage statistics over time (one row per snapshot) |
| `evomaster.log` | Full EvoMaster stdout/stderr |
| `EvoMaster_<service>_<role>_successes.java` | Tests that received HTTP 2xx responses |
| `EvoMaster_<service>_<role>_faults.java` | Tests with detected potential faults (5xx, unexpected responses) |
| `EvoMaster_<service>_<role>_others.java` | Remaining generated tests |
| `report.json` | Machine-readable coverage report |
| `index.html` | Human-readable HTML report (open with `webreport.py` or `webreport.command`) |

The `experiment_<timestamp>.jsonl` file at the root of `generated-tests/blackbox/` contains one JSON line per run with `service`, `role`, `status`, and `elapsed_seconds` — useful for aggregate analysis.

---

## Scientific experiment notes

- **Reproducibility**: set `EVOMASTER_SEED` to a fixed integer to get deterministic results across runs.
- **Budget**: the default `60s` is for quick validation only. For meaningful coverage data, use at least `1h` (`3600`).
- **Rate limiting**: `EVOMASTER_RATE=60` (1 req/s) avoids overwhelming the SUT. Increase carefully.
- **Stateful side effects**: EvoMaster creates, modifies, and deletes data during the search. Run against a dedicated test environment, not production.
- **Role comparison**: running the same service under all three roles allows direct comparison of endpoint reachability and fault detection rates across authentication contexts.

---

## Known issues

### `network yas-network declared as external, but could not be found`

`docker-compose.o11y.yml` declares the network as `external: true`. It must exist before `docker compose up`:

```bash
docker network create yas-network
```

> *_Note:_* For the first run, the storefront and the backoffice might not work as expected. Stop all the containers (Ctrl + C) and run docker compose up again.

---

## Services excluded from the experiment

The following services could not be tested with EvoMaster because they fail to start in the current YAS setup. The root causes are bugs in YAS itself, not in the test infrastructure.

### `payment` — Liquibase migration failure

The service crashes on startup with:

```
ERROR: column "is_enabled" of relation "payment_provider" does not exist
```

**Root cause:** `db.changelog-master.yaml` runs all DDL changelogs before all data changelogs (via `includeAll`). `ddl/changelog-0004.sql` renames the column `is_enabled` to `enabled`, but `data/changelog-0001-provider.sql` and `data/changelog-0002-provider.sql` still reference `is_enabled` in their `INSERT` statements. By the time the data migrations run, the column has already been renamed, causing the migration to fail and the application to abort.

**Possible fix:** rename `is_enabled` → `enabled` in `data/changelog-0001-provider.sql` and `data/changelog-0002-provider.sql`.

---

### `search` — Spring Data Elasticsearch incompatibility with Elasticsearch 8.x

The service crashes on startup with:

```
DataAccessResourceFailureException: node: http://elasticsearch:9200/, status: 400,
[es/indices.exists] Expecting a response body, but none was sent
```

**Root cause:** the running Elasticsearch instance is version **8.6.2**. In ES 8.x, the `HEAD /<index>` API (used to check if an index exists) returns only a status code with no response body. The Spring Data Elasticsearch client version used by YAS still expects a body in the response — behavior from the ES 7.x API — causing the `ProductRepository` bean instantiation to fail and the application context to abort.

**Possible fix:** either upgrade the `spring-data-elasticsearch` / `co.elastic.clients:elasticsearch-java` dependency in `search/pom.xml` to a version compatible with ES 8.6.x, or downgrade the Elasticsearch image in `docker-compose.yml` to a 7.x version compatible with the current client.
