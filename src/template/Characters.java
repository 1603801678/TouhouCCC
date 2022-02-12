package template;

import java.io.*;
import java.util.ArrayList;

/**
 * 代表一个角色，所有角色都继承或直接实现（直接实现的代码还没写）此类
 * @author ldj
 * @version 1.0.alpha
 */

public class Characters implements Comparable {
    private String name;
    private int hp;
    private int mp;
    private int atk;
    private int def;
    private int tec;
    private int spd;
    private String abbreviation;
    private String race;
    private ArrayList<Spell> spells = new ArrayList<>();

    public Characters(String filename) {
        File file = new File(ConstantTool.rootpath, "characters\\" + filename + "\\" + filename +".txt");
        if(!file.exists()){
            System.err.println("alert: cannot find file" + filename +", so default template is used");
            return;
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            try {
                line = br.readLine();
                while(!line.equals("符卡: ")&&line!=null) {
                    String[] in = line.split(": ");
                    switch (in[0]) {
                        case "人物名":
                            name = in[1];
                            break;
                        case "属性":
                            String[] argu = in[1].split(" ");
                            hp = Integer.valueOf(argu[0]);
                            mp = Integer.valueOf(argu[1]);
                            atk = Integer.valueOf(argu[2]);
                            def = Integer.valueOf(argu[3]);
                            tec = Integer.valueOf(argu[4]);
                            spd = Integer.valueOf(argu[5]);
                            break;
                        case "缩写":
                            abbreviation = in[1];
                            break;
                        case "种族":
                            race = in[1];
                            break;
                        default:
                            throw new IllegalArgumentException(in[0]);
                    }
                    line = br.readLine();
                }

                while(line!=null) {
                    String spellname = br.readLine();
                    if(spellname==null)break;
                    String argu = br.readLine();
                    if(argu==null)break;
                    spells.add(new Spell(spellname,argu.split(" "),this.getClass()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                br.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return  "人物名:(" + abbreviation + ")" + name + "[" + race + "] \n" +
                "hp:" + hp +
                " mp:" + mp + " \n" +
                "atk:" + atk +
                " def:" + def +
                " tec:" + tec +
                " spd:" + spd + /*"\n" +
                "spells:\n" + spells.toString().replaceAll(", ","") +*/ "\n" ;
    }

    @Override
    public int compareTo(Object o) {
        assert o instanceof Characters :"fail to compare a Character to" + o.toString();
        Characters com = (Characters) o;
        return com.spd - this.spd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getTec() {
        return tec;
    }

    public void setTec(int tec) {
        this.tec = tec;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }
}
