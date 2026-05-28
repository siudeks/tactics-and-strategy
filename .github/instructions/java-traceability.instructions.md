---
description: "Use when modifying Java source or Java tests in this repository. Reminds the agent to identify affected REQ IDs and keep docs/engine/traceability-matrix.md aligned with the code change. Keywords: java, traceability, requirement ids, req, traceability-matrix, docs update."
name: "Java Traceability Reminder"
applyTo: ["core/src/main/java/**/*.java", "core/src/test/java/**/*.java", "headless/src/main/java/**/*.java", "headless/src/test/java/**/*.java", "lwjgl3/src/main/java/**/*.java", "lwjgl3/src/test/java/**/*.java"]
---
# Java Traceability Reminder

- When changing Java production code or Java tests, identify the affected requirement IDs before finalizing the task.
- Check whether existing mappings already exist in `docs/engine/traceability-matrix.md`.
- If the code or tests change the implementation evidence for a requirement, update `docs/engine/traceability-matrix.md` in the same change.
- If no requirement mapping can be established with confidence, stop and ask the user for confirmation instead of guessing.
- Mention affected REQ IDs explicitly in your task summary when they are known.
