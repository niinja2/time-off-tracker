#!/bin/sh
# Potrošnja tokena: Haiku, 10 integrator leća, svaka u svojoj sesiji (izvođenje 2026-11-01).
D="C:/Users/workspace2/.claude/projects/C--Projects-time-off-tracker/cd7e1878-7971-4267-81de-799ca4070350/subagents"
PYTHONIOENCODING=utf-8 python "$(dirname "$0")/lece-integrator/tokeni.py" --model=haiku \
  01=$D/agent-ae4dd2bc8432fd33c.jsonl \
  02=$D/agent-aa9f26dee792dca17.jsonl \
  03=$D/agent-a1b29a71decd743e2.jsonl \
  04=$D/agent-ae23c180b31c6f845.jsonl \
  05=$D/agent-aa296d29d8ff1c518.jsonl \
  06=$D/agent-ae4f2ec3ff975d23b.jsonl \
  07=$D/agent-a3da78e9f586806ad.jsonl \
  08=$D/agent-abd18008c66590943.jsonl \
  09=$D/agent-af2303e7009fc87a0.jsonl \
  10=$D/agent-a8d56e616a4eb1794.jsonl
