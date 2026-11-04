#!/bin/sh
# Potrošnja tokena za Haiku leće (izvođenje 2026-11-01).
D="C:/Users/workspace2/.claude/projects/C--Projects-time-off-tracker/cd7e1878-7971-4267-81de-799ca4070350/subagents"
PYTHONIOENCODING=utf-8 python "$(dirname "$0")/tokeni.py" \
  mapa=$D/agent-a45259a0d337f9af6.jsonl \
  1-2=$D/agent-a48d12e7fb2cb29d4.jsonl \
  3-10=$D/agent-a06c0f17c9f3d599e.jsonl \
  4-5=$D/agent-a9ff0edc9f90e10f7.jsonl \
  6-8=$D/agent-a7ef8719b1155ae31.jsonl \
  7-9=$D/agent-ad54d2e7da5bbfd0b.jsonl
