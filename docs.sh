#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./init_docs.sh <domain> <task>
#
# Example:
#   ./init_docs.sh storage_sync retry_recovery

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <domain> <task>"
  exit 1
fi

DOMAIN_RAW="$1"
TASK_RAW="$2"

sanitize() {
  local input="$1"
  input="$(echo "$input" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
  input="${input// /_}"
  echo "$input" | sed 's/[^A-Za-z0-9._-]/_/g'
}

DOMAIN="$(sanitize "$DOMAIN_RAW")"
TASK="$(sanitize "$TASK_RAW")"
NOW="$(date '+%Y-%m-%d:%H:%M')"

ROOT_DIR="${PWD}"
DOCS_DIR="$ROOT_DIR/docs"

USE_CASE_DIR="$DOCS_DIR/use-case-harvests/$DOMAIN/$TASK"
PRODUCT_DIR="$DOCS_DIR/product-specs/$DOMAIN/$TASK"
DESIGN_DIR="$DOCS_DIR/design-docs/$DOMAIN/$TASK"
EXEC_ACTIVE_DIR="$DOCS_DIR/exec-plans/active/$DOMAIN/$TASK"
EXEC_COMPLETED_DIR="$DOCS_DIR/exec-plans/completed/$DOMAIN/$TASK"
STANDARDS_DIR="$DOCS_DIR/standards"

mkdir -p \
  "$USE_CASE_DIR" \
  "$PRODUCT_DIR" \
  "$DESIGN_DIR" \
  "$EXEC_ACTIVE_DIR" \
  "$EXEC_COMPLETED_DIR" \
  "$STANDARDS_DIR"

write_if_missing() {
  local file="$1"
  local content="$2"
  if [[ ! -f "$file" || ! -s "$file" ]]; then
    printf "%s\n" "$content" >"$file"
  fi
}

write_if_missing "$USE_CASE_DIR/use-case-harvest.md" "# Properties
owner: Codex
status: blocked
title: $TASK
domain: $DOMAIN
coverage_gate: NO
next_step: revise-harvest
last_updated: $NOW

# Prompt Interpretation
- User Goal:
- Requested Actions:
- Constraints:
- Expected Outcome:
- Explicit Non-goals:

# Candidate Use Cases

# Confirmed Use Cases

# Coverage Mapping

# Coverage Gate
- Ready for Event Storming: NO
- Why:
- Blocking Conditions:

# Blocking Unknowns

# Needs Review

# Rejected Use Cases

# Missing-but-Plausible Use Cases

# Next Revision Focus

# Oracle Handoff
- Allowed To Proceed: NO
- Confirmed Use Cases for Oracle:
- Assumptions Forbidden for Oracle:
- User Approval Required Before Orchestration: YES
"

write_if_missing "$PRODUCT_DIR/domain-boundary.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Domain Boundary
"

write_if_missing "$PRODUCT_DIR/use-cases.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Use Cases
"

write_if_missing "$DESIGN_DIR/event-storming.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Event Storming
"

write_if_missing "$DESIGN_DIR/aggregate-design.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Aggregate Design
"

write_if_missing "$DESIGN_DIR/bounded-context.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Bounded Context
"

write_if_missing "$DESIGN_DIR/detailed-design.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Detailed Design
"

write_if_missing "$EXEC_ACTIVE_DIR/plan.md" "# Properties
status: draft
coverage_gate: NO
title: $TASK
domain: $DOMAIN
source_use_case_harvest: ../../../use-case-harvests/$DOMAIN/$TASK/use-case-harvest.md
last_updated: $NOW

# Entry Points
- [Use Case Harvest](../../../use-case-harvests/$DOMAIN/$TASK/use-case-harvest.md)
- [Domain Boundary](../../../product-specs/$DOMAIN/$TASK/domain-boundary.md)
- [Use Cases](../../../product-specs/$DOMAIN/$TASK/use-cases.md)
- [Event Storming](../../../design-docs/$DOMAIN/$TASK/event-storming.md)
- [Aggregate Design](../../../design-docs/$DOMAIN/$TASK/aggregate-design.md)
- [Bounded Context](../../../design-docs/$DOMAIN/$TASK/bounded-context.md)
- [Detailed Design](../../../design-docs/$DOMAIN/$TASK/detailed-design.md)

# Purpose / Big Picture

# Progress
- [ ] Use-case harvest approved
- [ ] Domain boundary defined
- [ ] Use cases defined
- [ ] Event storming completed
- [ ] Aggregate design completed
- [ ] Bounded context finalized
- [ ] Detailed design completed
- [ ] Implementation completed
- [ ] Verification completed
- [ ] Documentation synced

# Surprises & Discoveries

# Decision Log

# Context and Orientation

# Event Storming Summary

# Aggregate Design Summary

# Bounded Context Summary

# Plan of Work

# Concrete Steps

# Validation and Acceptance

# Idempotence and Recovery

# Documentation Impact

# Change Log
- $NOW / initial scaffold created
"

write_if_missing "$EXEC_ACTIVE_DIR/implementation-log.md" "# Properties
status: draft
domain: $DOMAIN
task: $TASK
last_verified: $NOW

# Implementation Log
"

write_if_missing "$STANDARDS_DIR/event-storming.md" "# Event Storming Standard
"
write_if_missing "$STANDARDS_DIR/aggregate-design.md" "# Aggregate Design Standard
"
write_if_missing "$STANDARDS_DIR/coding-rules.md" "# Coding Rules
"

echo "Initialized docs structure:"
echo "  $USE_CASE_DIR"
echo "  $PRODUCT_DIR"
echo "  $DESIGN_DIR"
echo "  $EXEC_ACTIVE_DIR"
echo "  $EXEC_COMPLETED_DIR"
echo "  $STANDARDS_DIR"
