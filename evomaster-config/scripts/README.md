# Scripts — Quick Reference

> For full documentation see [`../README.md`](../README.md).

## Scripts

| Script | Purpose |
|---|---|
| `run-all.sh` | Run EvoMaster for all services and all roles |
| `evomaster-blackbox.sh` | Run EvoMaster for a single service and role |
| `auth-config.sh` | Keycloak credentials and token helpers (sourced, not run directly) |

## Common commands

```bash
# Full experiment — all 12 services × 3 roles (admin, customer, none)
./run-all.sh

# Single role only
./run-all.sh admin
./run-all.sh customer
./run-all.sh none

# Single service
./evomaster-blackbox.sh product admin
./evomaster-blackbox.sh cart customer
./evomaster-blackbox.sh tax none

# Extended budget (1 hour per service)
EVOMASTER_MAX_TIME=3600 EVOMASTER_SEED=42 ./run-all.sh

# Pin EvoMaster Docker image / recorded version (defaults: webfuzzing/evomaster:4.0.0, 4.0.0)
EVOMASTER_IMAGE=webfuzzing/evomaster:4.0.0 EVOMASTER_VERSION=4.0.0 ./evomaster-blackbox.sh product admin
```

## Output location

```
../generated-tests/blackbox/<service>/<role>/
```
