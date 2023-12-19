package org.dreambot.framework;


import org.dreambot.api.script.AbstractScript;

public abstract class Leaf<T extends Script> {
    public abstract boolean isValid();

    public abstract int onLoop();
}
