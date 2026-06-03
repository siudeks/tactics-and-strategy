package game.screens;

import game.engine.GameRuntime;
import game.engine.MovementPlayback;
import game.engine.PhaseStepResult;
import game.engine.RuntimePhase;
import game.engine.TurnPhase;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class BattlefieldPhasePlaybackController {
    private static final float DEFAULT_PHASE_NOTIFICATION_SECONDS = 3.0f;
    private static final float DEFAULT_MOVEMENT_PLAYBACK_SECONDS = 0.75f;
    private static final float DEFAULT_PHASE_TRANSITION_SECONDS = 0f;

    private final float phaseNotificationSeconds;
    private final float movementPlaybackSeconds;
    private final float phaseTransitionSeconds;

    private PlaybackState playbackState;
    private boolean turnCompletedThisFrame;

    BattlefieldPhasePlaybackController() {
        this(DEFAULT_PHASE_NOTIFICATION_SECONDS, DEFAULT_MOVEMENT_PLAYBACK_SECONDS, DEFAULT_PHASE_TRANSITION_SECONDS);
    }

    BattlefieldPhasePlaybackController(float phaseNotificationSeconds,
                                       float movementPlaybackSeconds,
                                       float phaseTransitionSeconds) {
        if (phaseNotificationSeconds <= 0f) {
            throw new IllegalArgumentException("phaseNotificationSeconds must be > 0");
        }
        if (movementPlaybackSeconds <= 0f) {
            throw new IllegalArgumentException("movementPlaybackSeconds must be > 0");
        }
        if (phaseTransitionSeconds < 0f) {
            throw new IllegalArgumentException("phaseTransitionSeconds must be >= 0");
        }
        this.phaseNotificationSeconds = phaseNotificationSeconds;
        this.movementPlaybackSeconds = movementPlaybackSeconds;
        this.phaseTransitionSeconds = phaseTransitionSeconds;
        this.playbackState = new IdleState();
        this.turnCompletedThisFrame = false;
    }

    void start(GameRuntime runtime) {
        Objects.requireNonNull(runtime, "runtime must not be null");
        if (isActive()) {
            throw new IllegalStateException("Phase playback is already active");
        }
        runtime.beginTurnExecution();
        playbackState = new TurnCommitState();
        turnCompletedThisFrame = false;
    }

    void advance(GameRuntime runtime, float deltaSeconds) {
        Objects.requireNonNull(runtime, "runtime must not be null");
        turnCompletedThisFrame = false;
        float remainingSeconds = Math.max(0f, deltaSeconds);

        while (true) {
            switch (playbackState) {
                case IdleState ignored -> {
                    return;
                }
                case TurnCommitState ignored -> {
                    playbackState = new PhaseNotificationState(currentTurnPhase(runtime), 0f);
                    continue;
                }
                case PhaseNotificationState notificationState -> {
                    if (remainingSeconds <= 0f) {
                        return;
                    }
                    float nextElapsed = notificationState.elapsedSeconds() + remainingSeconds;
                    if (nextElapsed < phaseNotificationSeconds) {
                        playbackState = new PhaseNotificationState(notificationState.phase(), nextElapsed);
                        return;
                    }
                    remainingSeconds = nextElapsed - phaseNotificationSeconds;
                    PhaseStepResult stepResult = runtime.advanceTurnExecution();
                    playbackState = postStepState(stepResult);
                    continue;
                }
                case MovementPlaybackState movementState -> {
                    if (remainingSeconds <= 0f) {
                        return;
                    }
                    float nextElapsed = movementState.elapsedSeconds() + remainingSeconds;
                    if (nextElapsed < movementPlaybackSeconds) {
                        playbackState = new MovementPlaybackState(
                            movementState.playback(),
                            nextElapsed,
                            movementState.completedStep()
                        );
                        return;
                    }
                    remainingSeconds = nextElapsed - movementPlaybackSeconds;
                    playbackState = new PhaseTransitionState(movementState.completedStep(), 0f);
                    continue;
                }
                case PhaseTransitionState transitionState -> {
                    if (phaseTransitionSeconds == 0f) {
                        playbackState = nextPlaybackState(runtime, transitionState.completedStep());
                        continue;
                    }
                    if (remainingSeconds <= 0f) {
                        return;
                    }
                    float nextElapsed = transitionState.elapsedSeconds() + remainingSeconds;
                    if (nextElapsed < phaseTransitionSeconds) {
                        playbackState = new PhaseTransitionState(transitionState.completedStep(), nextElapsed);
                        return;
                    }
                    remainingSeconds = nextElapsed - phaseTransitionSeconds;
                    playbackState = nextPlaybackState(runtime, transitionState.completedStep());
                }
            }
        }
    }

    boolean isActive() {
        return !(playbackState instanceof IdleState);
    }

    InteractionLockState interactionLockState() {
        return playbackState.lockState();
    }

    boolean hudActionsEnabled() {
        return !interactionLockState().blocksHudActions();
    }

    boolean shouldAcceptEndTurn() {
        return !interactionLockState().blocksEndTurn();
    }

    Optional<BattlefieldScreen.PhaseOverlayRenderContract> phaseOverlayRenderContract() {
        TurnPhase overlayPhase = playbackState.overlayPhase();
        if (overlayPhase == null) {
            return Optional.empty();
        }
        return Optional.of(new BattlefieldScreen.PhaseOverlayRenderContract(
            BattlefieldScreen.phaseOverlayDimColor(),
            BattlefieldScreen.phaseOverlayLabel(overlayPhase)
        ));
    }

    @Nullable MovementPlaybackRenderState movementPlaybackRenderState() {
        if (!(playbackState instanceof MovementPlaybackState movementState)) {
            return null;
        }
        return new MovementPlaybackRenderState(
            movementState.playback(),
            movementState.elapsedSeconds() / movementPlaybackSeconds
        );
    }

    boolean consumeTurnCompletedThisFrame() {
        boolean completed = turnCompletedThisFrame;
        turnCompletedThisFrame = false;
        return completed;
    }

    private PlaybackState postStepState(PhaseStepResult stepResult) {
        if (stepResult.phase() == RuntimePhase.SIMULTANEOUS_MOVE && hasAnimatedMovement(stepResult.movementPlayback())) {
            return new MovementPlaybackState(stepResult.movementPlayback(), 0f, stepResult);
        }
        return new PhaseTransitionState(stepResult, 0f);
    }

    private PlaybackState nextPlaybackState(GameRuntime runtime, PhaseStepResult completedStep) {
        if (completedStep.turnCompleted()) {
            turnCompletedThisFrame = true;
            return new IdleState();
        }
        return new PhaseNotificationState(currentTurnPhase(runtime), 0f);
    }

    private static boolean hasAnimatedMovement(List<MovementPlayback> playback) {
        return playback.stream().anyMatch(MovementPlayback::moved);
    }

    private static TurnPhase currentTurnPhase(GameRuntime runtime) {
        return runtime.currentRuntimePhase().turnPhase();
    }

    private sealed interface PlaybackState permits IdleState, TurnCommitState, PhaseNotificationState, MovementPlaybackState, PhaseTransitionState {
        InteractionLockState lockState();

        @Nullable TurnPhase overlayPhase();
    }

    private record IdleState() implements PlaybackState {
        @Override
        public InteractionLockState lockState() {
            return InteractionLockState.NONE;
        }

        @Override
        public @Nullable TurnPhase overlayPhase() {
            return null;
        }
    }

    private record TurnCommitState() implements PlaybackState {
        @Override
        public InteractionLockState lockState() {
            return InteractionLockState.TURN_COMMIT;
        }

        @Override
        public @Nullable TurnPhase overlayPhase() {
            return null;
        }
    }

    private record PhaseNotificationState(TurnPhase phase, float elapsedSeconds) implements PlaybackState {
        PhaseNotificationState {
            Objects.requireNonNull(phase, "phase must not be null");
        }

        @Override
        public InteractionLockState lockState() {
            return InteractionLockState.PHASE_NOTIFICATION;
        }

        @Override
        public @Nullable TurnPhase overlayPhase() {
            return phase;
        }
    }

    private record MovementPlaybackState(List<MovementPlayback> playback,
                                         float elapsedSeconds,
                                         PhaseStepResult completedStep) implements PlaybackState {
        MovementPlaybackState {
            playback = List.copyOf(Objects.requireNonNull(playback, "playback must not be null"));
            Objects.requireNonNull(completedStep, "completedStep must not be null");
        }

        @Override
        public InteractionLockState lockState() {
            return InteractionLockState.MOVEMENT_PLAYBACK;
        }

        @Override
        public @Nullable TurnPhase overlayPhase() {
            return null;
        }
    }

    private record PhaseTransitionState(PhaseStepResult completedStep, float elapsedSeconds) implements PlaybackState {
        PhaseTransitionState {
            Objects.requireNonNull(completedStep, "completedStep must not be null");
        }

        @Override
        public InteractionLockState lockState() {
            return InteractionLockState.PHASE_TRANSITION;
        }

        @Override
        public @Nullable TurnPhase overlayPhase() {
            return null;
        }
    }
}