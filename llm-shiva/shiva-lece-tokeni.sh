#!/bin/sh
# Potrošnja tokena za Sonnet shiva leće (izvođenje 2026-11-01).
D="C:/Users/workspace2/.claude/projects/C--Projects-time-off-tracker/cd7e1878-7971-4267-81de-799ca4070350/subagents"
PYTHONIOENCODING=utf-8 python "$(dirname "$0")/lece-integrator/tokeni.py" --model=sonnet \
  q-d-r=$D/agent-a3b87d56eca98a0f1.jsonl \
  log-proces=$D/agent-a7a4b8821f183c058.jsonl \
  ui-uloge=$D/agent-a1de657a294e294ea.jsonl \
  readme-konfig=$D/agent-a933a8bc3385a0593.jsonl
