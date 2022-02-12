package com;

import control.Controller;

/**
 * 此类代表人机对战中的计算机
 * @author ldj
 * @version 1.0.alpha
 */

public abstract class Com {
    private Controller controller;
    private int party;

    public Com(Controller controller, int party) {
        this.controller = controller;
        this.party = party;
    }

    abstract public void choseCharacter();

    abstract public void action();

    public Controller getController() {
        return controller;
    }

    public int getParty() {
        return party;
    }
}
