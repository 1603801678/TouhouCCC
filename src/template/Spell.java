package template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代表一个spell
 * @author ldj
 * @version 1.0.alpha
 */

public class Spell {
    private String name;
    private int atk;
    private int consume;
    private int target;
    private Range range;
    private int selfHpChange;
    private int selfAtkChange;
    private int selfDefChange;
    private int selfTecChange;
    private int selfSpdChange;
    private int enemyAtkChange;
    private int enemyDefChange;
    private int enemyTecChange;
    private int enemySpdChange;
    private Method invoke;
    private String comment = "";

    public Spell(String name, String[] argu, Class in) {
        this.name = name;
        atk = Integer.valueOf(argu[0]);
        consume = Integer.valueOf(argu[1]);
        target = Integer.valueOf(argu[2]);
        range = Range.getRangeFromString(argu[3]);
        selfHpChange = Integer.valueOf(argu[4]);
        selfAtkChange = Integer.valueOf(argu[5]);
        selfDefChange = Integer.valueOf(argu[6]);
        selfTecChange = Integer.valueOf(argu[7]);
        selfSpdChange = Integer.valueOf(argu[8]);
        enemyAtkChange = Integer.valueOf(argu[9]);
        enemyDefChange = Integer.valueOf(argu[10]);
        enemyTecChange = Integer.valueOf(argu[11]);
        enemySpdChange = Integer.valueOf(argu[12]);
        if(!argu[13].equals("null")){
            try {
                invoke = in.getMethod(argu[13]);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if(argu.length>14)comment = argu[14];
    }

    public void spellInvoke() {
        if(invoke == null)return;
        try {
            invoke.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "Spell name:" + name + '\n' +
                "atk:" + atk +
                " consume:" + consume +
                " target:" + target +
                " range:" + range +
                " selfHpChange:" + selfHpChange +
                " self:" + selfAtkChange +
                " " + selfDefChange +
                " " + selfTecChange +
                " " + selfSpdChange +
                " enemy:" + enemyAtkChange +
                " " + enemyDefChange +
                " " + enemyTecChange +
                " " + enemySpdChange + "\n" +
                comment + "\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public int getSelfHpChange() {
        return selfHpChange;
    }

    public void setSelfHpChange(int selfHpChange) {
        this.selfHpChange = selfHpChange;
    }

    public int getSelfAtkChange() {
        return selfAtkChange;
    }

    public void setSelfAtkChange(int selfAtkChange) {
        this.selfAtkChange = selfAtkChange;
    }

    public int getSelfDefChange() {
        return selfDefChange;
    }

    public void setSelfDefChange(int selfDefChange) {
        this.selfDefChange = selfDefChange;
    }

    public int getSelfTecChange() {
        return selfTecChange;
    }

    public void setSelfTecChange(int selfTecChange) {
        this.selfTecChange = selfTecChange;
    }

    public int getSelfSpdChange() {
        return selfSpdChange;
    }

    public void setSelfSpdChange(int selfSpdChange) {
        this.selfSpdChange = selfSpdChange;
    }

    public int getEnemyAtkChange() {
        return enemyAtkChange;
    }

    public void setEnemyAtkChange(int enemyAtkChange) {
        this.enemyAtkChange = enemyAtkChange;
    }

    public int getEnemyDefChange() {
        return enemyDefChange;
    }

    public void setEnemyDefChange(int enemyDefChange) {
        this.enemyDefChange = enemyDefChange;
    }

    public int getEnemyTecChange() {
        return enemyTecChange;
    }

    public void setEnemyTecChange(int enemyTecChange) {
        this.enemyTecChange = enemyTecChange;
    }

    public int getEnemySpdChange() {
        return enemySpdChange;
    }

    public void setEnemySpdChange(int enemySpdChange) {
        this.enemySpdChange = enemySpdChange;
    }
}
