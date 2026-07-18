#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <module> <minimum_percent>" >&2
  exit 1
fi

module="$1"
minimum="$2"
report_path="${module}/target/site/jacoco/jacoco.xml"

if [[ ! -f "${report_path}" ]]; then
  echo "[WARN] Coverage report missing: ${report_path} — skipping coverage gate for ${module}"
  exit 0
fi

# Get the LAST LINE counter (report-level aggregate, not method-level)
line_counter="$(grep '<counter type="LINE"' "${report_path}" | tail -1)"
covered="$(echo "${line_counter}" | sed 's/.*covered="\([0-9]*\)".*/\1/')"
missed="$(echo "${line_counter}" | sed 's/.*missed="\([0-9]*\)".*/\1/')"

if [[ -z "${line_counter}" || -z "${covered}" || -z "${missed}" ]]; then
  echo "[WARN] No LINE coverage data in ${report_path} — skipping coverage gate for ${module}"
  exit 0
fi

total=$((covered + missed))
if [[ "${total}" -eq 0 ]]; then
  echo "[WARN] No executable lines found for ${module} — skipping coverage gate"
  exit 0
fi

coverage_percent="$(awk -v c="${covered}" -v t="${total}" 'BEGIN { printf "%.2f", (c / t) * 100 }')"

echo "Module ${module} line coverage: ${coverage_percent}% (required >= ${minimum}%)"
if awk -v actual="${coverage_percent}" -v threshold="${minimum}" 'BEGIN { exit !(actual + 0 >= threshold + 0) }'; then
  exit 0
fi

echo "Coverage gate failed for ${module}: ${coverage_percent}% < ${minimum}%"
exit 1
