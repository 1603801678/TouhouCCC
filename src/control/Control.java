package control;

import template.Characters;
import template.Spell;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

@Deprecated
public final class Control {
    private static Control mainControl;
    public static Control getMainControl() {
        if(mainControl==null)mainControl = new Control();
        return mainControl;
    }

    static class Location {
        private int col;
        private int row;
        public Location(int col, int row) {
            this.setCol(col);
            this.setRow(row);
        }

        @Override
        public int hashCode() {
            return 3*col+row;
        }

        @Override
        public boolean equals(Object o) {
            return this.hashCode()==o.hashCode();
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            if(col<0||col>5)throw new IllegalArgumentException("col cannot be " + col);
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            if(row<0||row>2)throw new IllegalArgumentException("row cannot be " + row);
            this.row = row;
        }
    }

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private TreeMap<Characters,Location>[] Arrange = new TreeMap[2];
    private HashSet<String>[] Arrangename = new HashSet[2];
    private TreeMap<Characters,Action> actions = new TreeMap<>();
    private int round = 0;
    private boolean start = false;
    private Characters leftPresent;
    private Characters rightPrestnt;

    private Control() {
        Arrange[LEFT] = new TreeMap<>();
        Arrange[RIGHT] = new TreeMap<>();
        Arrangename[LEFT] = new HashSet<>();
        Arrangename[RIGHT] = new HashSet<>();
    }

    public boolean isArrangeFull(int party) {
        if(Arrange[party].size()>=5){
            return true;
        }
        else return false;
    }

    public boolean addArrangeTest(Location l, int party) {
        if(isArrangeFull(party)){
            return false;
        }
        if(Arrange[party].values().contains(l)){
            return false;
        }
        if(l.getCol()>(party*3+2)||l.getCol()<party*3){
            return false;
        }
        return true;
    }

    public boolean addArrangeTest(String name, Location l, int party) {
        if(addArrangeTest(l,party)&&testNameConflict(name,party))return true;
        else return false;
    }

    public boolean testNameConflict(Characters com, TreeMap<Characters,?> map){
        for(Characters c:map.keySet()) {
            if(c.getName().equals(com.getName()))return false;
        }
        return true;
    }

    public boolean testNameConflict(String name, int party){
        if(Arrangename[party].contains(name))return false;
        else return true;
    }

    public boolean addArrange(Characters c, Location l, int party) {
        if(addArrangeTest(c.getName(),l,party)==false)return false;
        Arrange[party].put(c,l);
        Arrangename[party].add(c.getName());
        testStrat();
        return true;
    }

    public boolean testStrat() {
        if(start==true)return true;
        if(Arrange[LEFT].size()==5&&Arrange[RIGHT].size()==5){
            start = true;
            return true;
        }
        return false;
    }

    private void testAction() {
        if(actions.size()==Arrange[0].size()+Arrange[1].size()){
            action();
        }
    }

    class Action {
        private Location move;
        private Spell spell;

        public Action(Location move, Spell spell) {
            this.move = move;
            this.spell = spell;
        }

        public Location getMove() {
            return move;
        }

        public void setMove(Location move) {
            this.move = move;
        }

        public Spell getSpell() {
            return spell;
        }

        public void setSpell(Spell spell) {
            this.spell = spell;
        }
    }

    private void action() {
        actions.forEach((K,V)->{
            Location move = V.getMove();
            if(move!=null){
                if(move.getCol()<3)Arrange[LEFT].replace(K,move);
                else Arrange[RIGHT].replace(K,move);
            }

        });
    }

    private void deleteArrange(String name, int party) {
        Arrangename[party].remove(name);
        for(Characters c:Arrange[party].keySet()) {
            if(c.getName().equals(name))Arrange[party].remove(c);
        }
    }

    public boolean getStart() {return start;}
}
