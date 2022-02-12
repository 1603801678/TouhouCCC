package template;

import control.Controller;

/**
 * 描述一个spell的攻击模式
 * @author ldj
 * @version 1.0.alpha
 */

public enum Range {
    Single(false){
        @Override
        public int[][] getAffected(int row, String[][] map) {
            int i = 0;
            while(map[i][row]==null){
                i++;
                if(i>2) return null;
            }
            int[][] r = {{i,row}};
            return r;
        }
    },
    Enemyall(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            int[][] r = new int[9][2];
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    r[3*i+j][Controller.COL] = i;
                    r[3*i+j][Controller.ROW] = j;
                }
            }
            return r;
        }
    },
    Vertical(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            if(Single.getAffected(row, map) == null) return null;
            int col = Single.getAffected(row, map)[0][Controller.COL];
            int[][] r = {{col,0},{col,1},{col,2}};
            return r;
        }
    },
    Horizontal(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            int[][] r = {{0,row},{1,row},{2,row}};
            return r;
        }
    },
    PlusShape(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            if(Single.getAffected(row, map) == null) return null;
            int[] target = Single.getAffected(row, map)[0];
            int[][] r = {{target[0],target[1]},
                    {target[0]+1,target[1]},
                    {target[0]-1,target[1]},
                    {target[0],target[1]+1},
                    {target[0],target[1]-1}};
            return r;
        }
    },
    XShape(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            if(Single.getAffected(row, map) == null) return null;
            int[] target = Single.getAffected(row, map)[0];
            int[][] r = {{target[0],target[1]},
                    {target[0]+1,target[1]+1},
                    {target[0]-1,target[1]+1},
                    {target[0]-1,target[1]-1},
                    {target[0]+1,target[1]-1}};
            return r;
        }
    },
    Front(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            int i = 0;
            while(map[i][0]==null&&map[i][1]==null&&map[i][2]==null)i++;
            int[][] r = {{0,i},{1,i},{2,i}};
            return r;
        }
    },
    Back(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            int i = 2;
            while(map[i][0]==null&&map[i][1]==null&&map[i][2]==null)i--;
            int[][] r = {{0,i},{1,i},{2,i}};
            return r;
        }
    },
    Self(false) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            return null;
        }
    },
    Selfall(true) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            return null;
        }
    },
    All(true) {
        @Override
        public int[][] getAffected(int row, String[][] map) {
            return Enemyall.getAffected(row, map);
        }
    },
    ;

    public final boolean selfall;

    Range(boolean isSelfall) {
        selfall = isSelfall;
    }

    abstract public int[][] getAffected(int row, String[][] map);

    static public Range getRangeFromString(String in) {
        switch (in) {
            case "single":
                return Single;
            case "enemyall":
                return Enemyall;
            case "-":
                return Horizontal;
            case "|":
                return Vertical;
            case "+":
                return PlusShape;
            case "X":
                return XShape;
            case "front":
                return Front;
            case "back":
                return Back;
            case "self":
                return Self;
            case "selfall":
                return Selfall;
            case "all":
                return All;
            default:
                throw new IllegalArgumentException(in);
        }
    }

}
