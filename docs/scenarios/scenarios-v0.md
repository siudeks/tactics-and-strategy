# Scenario Pack v0

## Scenario Template
- ID
- Title
- Theater Context
- Primary Objectives
- Secondary Objectives
- Failure Conditions
- Operational Constraints
- Completion Trigger
- Requirements Trace IDs

---

## S01 - Border at Dawn
- Theater Context: Frontier opening operation.
- Primary Objectives:
  - Hold two designated border control points.
- Secondary Objectives:
  - Keep losses below scenario threshold.
- Failure Conditions:
  - Both border points lost at the same time.
- Operational Constraints:
  - Limited operational depth in early turns.
- Completion Trigger:
  - Survive required number of turns while maintaining objective control.
- Requirements Trace IDs:
  - REQ-LOOP-001, REQ-MAP-001, REQ-SCEN-001, REQ-HUD-001

## S02 - Convoy Intercept
- Theater Context: Mobile interdiction operation.
- Primary Objectives:
  - Capture or neutralize enemy supply convoy.
- Secondary Objectives:
  - Extract at least one reconnaissance element.
- Failure Conditions:
  - Convoy exits battlefield operational zone.
- Operational Constraints:
  - Time pressure and route uncertainty.
- Completion Trigger:
  - Convoy status resolved before extraction window closes.
- Requirements Trace IDs:
  - REQ-TURN-001, REQ-SUP-001, REQ-SCEN-001

## S03 - Supply Corridor
- Theater Context: Mid-campaign logistics defense.
- Primary Objectives:
  - Maintain continuous supply corridor between base and front sector.
- Secondary Objectives:
  - Reclaim one contested waypoint.
- Failure Conditions:
  - Supply corridor interrupted beyond allowed tolerance.
- Operational Constraints:
  - Multi-box control dependency.
- Completion Trigger:
  - Corridor continuity preserved through scenario duration.
- Requirements Trace IDs:
  - REQ-SUP-001, REQ-MAP-001, REQ-UNIT-001

## S04 - Night Counterstroke
- Theater Context: Limited-visibility recapture attempt.
- Primary Objectives:
  - Recover lost sector from previous operation.
- Secondary Objectives:
  - End mission with morale advantage.
- Failure Conditions:
  - Sector not recovered before turn limit.
- Operational Constraints:
  - Reduced visibility and reaction windows.
- Completion Trigger:
  - Sector control verified at end-of-turn validation.
- Requirements Trace IDs:
  - REQ-CBT-001, REQ-SCEN-001, REQ-HUD-001

## S05 - Break the Line
- Theater Context: Late-campaign breakthrough action.
- Primary Objectives:
  - Break defensive line and secure key terrain node.
- Secondary Objectives:
  - Preserve core formation combat readiness.
- Failure Conditions:
  - No breakthrough within operational limit.
- Operational Constraints:
  - High resistance and stacking pressure.
- Completion Trigger:
  - Key terrain captured and held through validation phase.
- Requirements Trace IDs:
  - REQ-STACK-001, REQ-CBT-001, REQ-SCEN-001

## S06 - Final Sector
- Theater Context: End-state control operation.
- Primary Objectives:
  - Secure three strategic sectors simultaneously.
- Secondary Objectives:
  - Prevent command-chain disruption.
- Failure Conditions:
  - Two sectors lost or supply collapse event.
- Operational Constraints:
  - Concurrent objective defense.
- Completion Trigger:
  - Simultaneous control state confirmed at mission close.
- Requirements Trace IDs:
  - REQ-SUP-001, REQ-MAP-001, REQ-SCEN-001, REQ-HUD-001

---

## Traceability Index
- REQ-LOOP-001: Core gameplay loop
- REQ-TURN-001: Turn sequence and validation
- REQ-MAP-001: Hex map, terrain, control points
- REQ-UNIT-001: Formation-based units and states
- REQ-SUP-001: Supply continuity and penalties
- REQ-STACK-001: Unit concentration constraints
- REQ-CBT-001: Contextual combat resolution
- REQ-SCEN-001: Scenario objective/failure model
- REQ-HUD-001: Operational readability and HUD state
