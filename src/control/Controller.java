package control;

import template.Characters;
import template.ConstantTool;
import template.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * 游戏的控制核心
 * @author ldj
 * @version 1.0.alpha
 */

public final class Controller {
    /**单例设计模式*/
    private static Controller controller;
    /**单例设计模式*/
    public static Controller getController() {
        if(controller==null)controller = new Controller();
        return controller;
    }
    /**单例设计模式*/
    private Controller() {};

    /**左方角色*/
    public static final int LEFT = 0;
    /**右方角色*/
    public static final int RIGHT = 1;
    public static final int COL = 0;
    public static final int ROW = 1;
    private String[][][] abvMap= new String[2][3][3];//party,col,row
    private String[][] abvlist = new String[2][5];//party,num
    private Characters[][] list= new Characters[2][5];//party,num
    private int[][][] location = new int[2][5][2];//party,num,location
    private int round = 0;
    private boolean start = false;
    //attaking
    private Characters[] actionSequence = new Characters[10];
    private int[] sequenceParty = new int[10];
    private Action[] actions = new Action[10];
    //player controlling
    private Characters[][] presentCha = new Characters[2][5];//party,num
    private int[] point = new int[2];//party
    private Spell[][] presentSpell = new Spell[2][3];//party,choice
    private Action[][] presentActions = new Action[2][5];
    private String[][][] wantedMap = new String[2][3][3];//party,col,row

    private int winner = -1;
    
    static class Action {
        private Spell spell;
        private int[] move;

        public Action(Spell spell, int[] move) {
            this.spell = spell;
            this.move = move;
        }

        public Spell getSpell() {
            return spell;
        }

        public int[] getMove() {
            return move;
        }
    }

    public boolean isCharFull(int party) {
        if(occupiedLength(abvlist[party])>=5)return true;
        else return false;
    }

    private void testStart() {
        if(isCharFull(LEFT)&&isCharFull(RIGHT)){
            start = true;
            putInit();
        }
    }

    public int contains(int party, String abv) {
        for(int i = 0; i < 5; i++) {
            if(abvlist[party][i]==null)continue;
            else if(abvlist[party][i].equals(abv))return i;
        }
        return -1;
    }

    public boolean isOccupied(int party, int col, int row) {
        if(abvMap[party][col][row]==null)return false;
        else return true;
    }

    public boolean addChar(Characters c, int party, int col, int row){
        if(start==true){
            System.err.println("The game is already started");
            return false;
        }
        if(isCharFull(party)){
            System.err.println("You have max 5 characters");
            return false;
        }
        if(contains(party, c.getAbbreviation())!=-1){
            System.err.println("this character is already on map: " + c.getName());
            return false;
        }
        if(isOccupied(party,col,row)){
            System.err.printf("this location is occupied: (%d, %d)\n", col, row);
            return false;
        }

        int i = 0;
        while(abvlist[party][i]!=null)i++;
        abvlist[party][i] = c.getAbbreviation();
        list[party][i] = c;
        abvMap[party][col][row] = abvlist[party][i];
        location[party][i][COL] = col;
        location[party][i][ROW] = row;

        testStart();
        return true;
    }

    private void deleteChar(int party, int col, int row) {
        String abv = abvMap[party][col][row];
        int i = 0;
        while(abvlist[party][i] == null || !abvlist[party][i].equals(abv))i++;

        abvMap[party][col][row] = null;
        abvlist[party][i] = null;
        list[party][i] = null;
    }
    
    private void attacking() {
        initAttacking();
        for(int i = 0; i < actionSequence.length && actionSequence[i] != null; i++) {
            int selfparty = sequenceParty[i];
            int enemyparty = selfparty==0 ? 1 : 0;
            if(contains(selfparty, actionSequence[i].getAbbreviation()) == -1)continue;
            int[] selflocation =
                    location[selfparty][contains(selfparty, actionSequence[i].getAbbreviation())];
            Spell spell = actions[i].getSpell();
            if(spell!=null){
                System.out.printf("%d:%s use %s\n", sequenceParty[i], actionSequence[i].getName(), spell.getName());
                actionSequence[i].setMp(actionSequence[i].getMp() - spell.getConsume());
                spell.spellInvoke();
                //attack
                int[][] target = spell.getRange().getAffected(selflocation[ROW],abvMap[enemyparty]);
                if(target!=null){
                    for(int[] loc : target) {
                        if(loc[COL]>=0&&loc[COL]<3&&
                                loc[ROW]>=0&&loc[ROW]<3&&
                                abvMap[enemyparty][loc[COL]][loc[ROW]]!=null) 
                            attackOne(enemyparty, loc[COL], loc[ROW], spell, actionSequence[i]);
                    }
                }
                //self aid
                if(spell.getRange().selfall){
                    for(int j = 0; j < 3; j++) {
                        for(int k = 0; k < 3; k++) {
                            if(abvMap[selfparty][j][k]!=null)
                                aidOne(selfparty, j, k, spell);
                        }
                    }
                } else aidOne(actionSequence[i], spell);
            }
            //move
            int[] newlocation = actions[i].getMove();
            if(newlocation!=null){
                System.out.printf("%d:%s move to (%d,%d)\n", sequenceParty[i], actionSequence[i].getName(), newlocation[0], newlocation[1]);
                abvMap[selfparty][selflocation[COL]][selflocation[ROW]] = null;
                abvMap[selfparty][newlocation[COL]][newlocation[ROW]] = actionSequence[i].getAbbreviation();
                int l = contains(selfparty, actionSequence[i].getAbbreviation());
                location[selfparty][l] = newlocation;
            }
        }
        if(TestFinish())return;
        //mp+3
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 5; j++) {
                if(list[i][j]!=null)list[i][j].setMp(list[i][j].getMp()+3);
            }
        }
        round++;
        putInit();
    }

    private boolean TestFinish() {
        int leftLeft = occupiedLength(abvlist[LEFT]);
        int rightLeft = occupiedLength(abvlist[RIGHT]);
        if(leftLeft==0){
            winner = LEFT;
            return true;
        }
        if(rightLeft==0){
            winner = RIGHT;
            return true;
        }
        return false;
    }

    private void initAttacking() {
        int charnum = presentCha[LEFT].length + presentCha[RIGHT].length;
        actionSequence = new Characters[charnum];
        sequenceParty = new int[charnum];
        actions = new Action[charnum];

        int i = 0;
        for(; i < presentCha[LEFT].length; i++) {
            actionSequence[i] = presentCha[LEFT][i];
            sequenceParty[i] = LEFT;
            actions[i] = presentActions[LEFT][i];
        }
        for(int j = 0; j < presentCha[RIGHT].length; i++, j++) {
            actionSequence[i] = presentCha[RIGHT][j];
            sequenceParty[i] = RIGHT;
            actions[i] = presentActions[RIGHT][j];
        }

        for(int l = charnum-1; l >= 1 ; l--) {
            for(int k = l-1; k >= 0; k--) {
                if(actionSequence[l].getSpd()>actionSequence[k].getSpd()){
                    ConstantTool.change(actionSequence, l, k);
                    ConstantTool.change(actions, l, k);
                    int temp = sequenceParty[l];
                    sequenceParty[l] = sequenceParty[k];
                    sequenceParty[k] = temp;
                }
            }
        }
    }

    private void putInit() {
        presentCha[LEFT] = cleanNull(list[LEFT]);
        presentCha[RIGHT] = cleanNull(list[RIGHT]);
        Arrays.sort(presentCha[LEFT]);
        Arrays.sort(presentCha[RIGHT]);
        point[LEFT] = 0;
        point[RIGHT] = 0;
        presentSpell[LEFT] = randomSpell(presentCha[LEFT][point[LEFT]]);
        presentSpell[RIGHT] = randomSpell(presentCha[RIGHT][point[RIGHT]]);
        presentActions = new Action[2][5];
        wantedMap = abvMap.clone();
    }

    private Spell[] randomSpell(Characters characters) {
        ArrayList<Spell> able = (ArrayList<Spell>) characters.getSpells().clone();
        able.removeIf(spell -> spell.getConsume() > characters.getMp());
        if(able.isEmpty())return null;
        Random random = new Random();
        Spell[] r = new Spell[3];
        for(int i = 0; i < 3; i++) {
            r[i] = able.get(random.nextInt(able.size()));
        }
        return r;
    }

    public Characters[] randomCharacter(int party) {
        Set<String> able = ConstantTool.charRegister.keySet();
        able.removeIf(abv -> contains(party,abv)!=-1);
        if(able.isEmpty())throw new RuntimeException("number of characters are not enough");
        Object[] available = able.toArray();
        Random random = new Random();
        Characters[] r = new Characters[4];
        for(int i = 0; i < 4; i++) {
            r[i] = ConstantTool.getCharacter(available[random.nextInt(available.length)].toString());
        }
        return r;
    }

    public boolean isPutFull(int party) {
        if(point[party] >= presentCha[party].length)return true;
        else return false;
    }

    public void putSpell(int party, Spell spell) {
        if(start==false)return;
        if(isPutFull(party))return;
        if(presentSpell[party]==null)return;
        for(Spell s : presentSpell[party]) {
            if(s.getName()==spell.getName()){
                presentActions[party][point[party]] =  new Action(s,null);
                point[party]++;
                if(!isPutFull(party)) presentSpell[party] = randomSpell(presentCha[party][point[party]]);
                testAttacking();
                return;
            }
        }
    }

    public void putMove(int party, int col, int row) {
        if(start==false)return;
        if(isPutFull(party))return;
        if(col<0||col>2||row<0||row>2)return;
        int[] selflocation =
                location[party][contains(party, presentCha[party][point[party]].getAbbreviation())];
        if(wantedMap[party][col][row]==null || (col==selflocation[COL] && row==selflocation[ROW]) ){
            wantedMap[party][selflocation[COL]][selflocation[ROW]] = null;
            wantedMap[party][col][row] = presentCha[party][point[party]].getAbbreviation();

            presentActions[party][point[party]] =  new Action(null, new int[]{col,row});
            point[party]++;
            if(!isPutFull(party)) presentSpell[party] = randomSpell(presentCha[party][point[party]]);
            testAttacking();
        }
    }

    private void testAttacking() {
        if(isPutFull(LEFT)&&isPutFull(RIGHT)){
            attacking();
        }
    }

    private void attackOne(int party, int col, int row, Spell spell, Characters owner) {
        Characters characters = list[party][contains(party,abvMap[party][col][row])];
        if(characters.getTec()*100<=spell.getTarget()* owner.getTec()){
            int trueAtk = spell.getAtk()*owner.getAtk() - characters.getDef();
            characters.setHp(characters.getHp()-trueAtk);
            if(characters.getHp() <= 0){
                deleteChar(party, col, row);
                return;
            }

            
            int finalAtk = characters.getAtk() - spell.getEnemyAtkChange();
            characters.setAtk(finalAtk > 0 ? finalAtk : 0);

            int finalDef = characters.getDef() - spell.getEnemyDefChange();
            characters.setDef(finalDef > 0 ? finalDef : 0);

            int finalTec = characters.getTec() - spell.getEnemyTecChange();
            characters.setTec(finalTec > 0 ? finalTec : 0);

            int finalSpd = characters.getSpd() - spell.getEnemySpdChange();
            characters.setSpd(finalSpd > 0 ? finalSpd : 0);
        }
    }

    private void aidOne(int party, int col, int row, Spell spell) {
        aidOne(list[party][contains(party, abvMap[party][col][row])], spell);
    }
    private void aidOne(Characters characters, Spell spell) {
        characters.setHp(characters.getHp() + spell.getSelfHpChange());
        characters.setAtk(characters.getAtk() + spell.getSelfAtkChange());
        characters.setDef(characters.getDef() + spell.getSelfDefChange());
        characters.setTec(characters.getTec() + spell.getSelfTecChange());
        characters.setSpd(characters.getSpd() + spell.getSelfSpdChange());
    }

    public static <T> int occupiedLength(T[] t) {
        int count = 0;
        for(int i = 0; i < t.length; i++) {
            if(t[i]!=null)count++;
        }
        return count;
    }

    @Deprecated
    public static <T> T[] cleanNull(T[] t) {
        Object[] clean = new Object[occupiedLength(t)];
        int count = 0;
        for(int i = 0; i < t.length; i++) {
            if(t[i]!=null){
                clean[count] = t[i];
                count++;
            }
        }
        return (T[])clean;
    }

    public static Characters[] cleanNull(Characters[] t) {
        Characters[] clean = new Characters[occupiedLength(t)];
        int count = 0;
        for(int i = 0; i < t.length; i++) {
            if(t[i]!=null){
                clean[count] = t[i];
                count++;
            }
        }
        return clean;
    }

    public String[][][] getAbvMap() {
        return abvMap;
    }

    public String[][] getAbvlist() {
        return abvlist;
    }

    public Characters[][] getList() {
        return list;
    }

    public int getRound() {
        return round;
    }

    public boolean isStart() {
        return start;
    }

    public Characters getPresentCha(int party) {
        return presentCha[party][point[party]];
    }

    public Spell[] getPresentSpell(int party) {
        return presentSpell[party];
    }

    public String[][] getWantedMap(int party) {
        return wantedMap[party];
    }

    public int getWinner() {
        return winner;
    }
}