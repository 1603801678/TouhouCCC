package com;

import control.Controller;

import java.util.Random;

/**
 * 一个随机的计算机
 * @author ldj
 * @version 1.0.alpha
 */

public class RandomCom extends Com{
    private Random random;
    public RandomCom(Controller controller, int party) {
        super(controller, party);
        random = new Random();
    }

    @Override
    public void choseCharacter() {
        while(!getController().isCharFull(getParty())) {
            int col = random.nextInt(3);
            int row = random.nextInt(3);
            while(getController().isOccupied(getParty(), col, row)) {
                col = random.nextInt(3);
                row = random.nextInt(3);
            }
            getController().addChar(getController().randomCharacter(getParty())[0],getParty(),col,row);
        }
    }

    @Override
    public void action() {
        while (!getController().isPutFull(getParty())) {
            if(getController().getPresentSpell(getParty())==null || random.nextDouble() > 0.8) {
                String[][] map = getController().getWantedMap(getParty());
                int col = random.nextInt(3);
                int row = random.nextInt(3);
                while(map[col][row]!=null) {
                    col = random.nextInt(3);
                    row = random.nextInt(3);
                }
                getController().putMove(getParty(), col, row);
            } else {
                getController().putSpell(getParty(), getController().getPresentSpell(getParty())[0]);
            }
        }
    }
}
