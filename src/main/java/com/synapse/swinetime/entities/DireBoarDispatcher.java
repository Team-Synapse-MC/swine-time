package com.synapse.swinetime.entities;

import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.animation.play_behavior.AzPlayBehaviors;

public class DireBoarDispatcher {
    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "idle",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_COMMAND = AzCommand.create(
            "base_controller",
            "walking",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_COMMAND = AzCommand.create(
            "base_controller",
            "running",
            AzPlayBehaviors.LOOP
    );

    private final DireBoarEntity dire_boar;

    public DireBoarDispatcher(DireBoarEntity animatable) {
        this.dire_boar = animatable;
    }

    public void idle() {
        IDLE_COMMAND.sendForEntity(dire_boar);
    }

    public void walk() {
        WALK_COMMAND.sendForEntity(dire_boar);
    }

    public void run() {
        RUN_COMMAND.sendForEntity(dire_boar);
    }
}
