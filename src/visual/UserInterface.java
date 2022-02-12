package visual;

import com.Com;
import com.RandomCom;
import control.Controller;
import template.Characters;
import template.ConstantTool;
import template.Spell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * ??????????
 * @author ldj
 * @version 1.0.alpha
 */

public class UserInterface extends JFrame {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public Controller controller;
    private Com com;

    //frame components
    private JTextArea[] charState = new JTextArea[2];
    private JLabel gameState;
    private JPanel actionPanel;
    private JButton[] action = new JButton[4];
    private JPanel mainPanel;
    private JButton[][][] map = new JButton[2][3][3];

    //used when choosing characters
    private int lockCol = -1;
    private int lockRow = -1;
    private Characters[] lockCharacter = null;

    public UserInterface() throws HeadlessException {
        super("TouhouCCC");
        controller = Controller.getController();
        com = new RandomCom(controller, RIGHT);
        init();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1200,600);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    private void init() {
        charState[LEFT] = new JTextArea("Your characters:\n");
        charState[RIGHT] = new JTextArea("Enemy's characters:\n");
        //gameState = new JLabel(String.format("round:%d action:%s winner:%d",
        //        controller.getRound(), controller.getPresentCha(LEFT), controller.getWinner()));
        gameState = new JLabel("Preparing: click on the left to choose characters");
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(2,2));
        for(int i = 0; i < 4; i++) {
            action[i] = new JButton("<html> <br> <br> </html>");
            actionPanel.add(action[i]);
            action[i].addActionListener(new actionListener(i));
        }
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 7));
        /*for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 3; j++) {
                for(int k = 0; k < 3; k++) {
                    map[i][j][k] = new JButton();
                }
            }
        }*/
        for(int j = 0; j < 3; j++) {
            for(int k = 0; k < 3; k++) {
                map[LEFT][j][k] = new JButton();
                map[LEFT][j][k].addActionListener(new mapListener(j, k));
            }
        }
        for(int j = 0; j < 3; j++) {
            for(int k = 0; k < 3; k++) {
                map[RIGHT][j][k] = new JButton();
            }
        }
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 7; j++) {
                if(j == 3)mainPanel.add(new JLabel());
                if(j < 3)mainPanel.add(map[LEFT][2-j][i]);
                if(j > 3)mainPanel.add(map[RIGHT][j-4][i]);
            }
        }
        this.add(gameState,BorderLayout.PAGE_START);
        this.add(mainPanel,BorderLayout.CENTER);
        this.add(actionPanel,BorderLayout.PAGE_END);
        this.add(charState[LEFT],BorderLayout.LINE_START);
        this.add(charState[RIGHT],BorderLayout.LINE_END);
    }

    public void mapClick(int col, int row) {
        if(controller.getWinner() != -1) return;
        if(controller.isStart()) {
            if(controller.isPutFull(LEFT))return;
            controller.putMove(LEFT, col, row);
            if(controller.isPutFull(LEFT)){
                com.action();
                if(controller.getWinner() != -1) return;
                updateMap();
            } else updatePut();
        } else {
            if(lockCol==-1){
                if(!controller.isOccupied(LEFT, col, row) && !controller.isCharFull(LEFT)) {
                    lockCol = col;
                    lockRow = row;
                    lockCharacter = controller.randomCharacter(LEFT);
                    for(int i = 0; i < 4; i++) {
                        action[i].setText(ConstantTool.multiLine(lockCharacter[i].toString()));
                    }
                    //this.repaint();
                }
            }
        }
    }

    class mapListener implements ActionListener {
        private int col;
        private int row;

        public mapListener(int col, int row) {
            this.col = col;
            this.row = row;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mapClick(col, row);
        }
    }

    public void actionClick(int num) {
        if(controller.getWinner() != -1) return;
        if(controller.isStart()) {
            if(controller.isPutFull(LEFT))return;
            Spell[] spells = controller.getPresentSpell(LEFT);
            if(spells == null)return;
            if(num == 3)return;
            controller.putSpell(LEFT, spells[num]);
            if(controller.isPutFull(LEFT)){
                com.action();
                if(controller.getWinner() != -1) return;
                updateMap();
            } else updatePut();
        } else {
            if(lockCharacter!=null){
                controller.addChar(lockCharacter[num], LEFT, lockCol, lockRow);
                map[LEFT][lockCol][lockRow].setText(lockCharacter[num].getAbbreviation());
                charState[LEFT].setText(charState[LEFT].getText()+lockCharacter[num].toString()+"\n");

                for(int i = 0; i < 4; i++) {
                    action[i].setText("<html> <br> <br> </html>");
                }

                lockCol = -1;
                lockRow = -1;
                lockCharacter = null;

                if(controller.isCharFull(LEFT)){
                    com.choseCharacter();
                    if(controller.isStart() == false)throw new RuntimeException("Com has not finish choosing");
                    //draw start
                    String[][] enemymap = controller.getAbvMap()[RIGHT];
                    for(int j = 0; j < 3; j++) {
                        for(int k = 0; k < 3; k++) {
                            if(enemymap[j][k] != null) map[RIGHT][j][k].setText(enemymap[j][k]);
                        }
                    }
                    charState[RIGHT].setText("Enemy's characters:\n" + ConstantTool.arrayFormat(Arrays.toString(controller.getList()[RIGHT])));
                    gameState.setText(String.format("round:%d action:%s winner:%d",
                            controller.getRound(), controller.getPresentCha(LEFT), controller.getWinner()));
                    Spell[] spell = controller.getPresentSpell(LEFT);
                    if(spell == null){
                        for(int i = 0; i < 4; i++) {
                            action[i].setText("<html> <br>Move<br> </html>");
                        }
                    } else {
                        for(int i = 0; i < 3; i++) {
                            action[i].setText(ConstantTool.multiLine(spell[i].toString()));
                        }
                        action[3].setText("<html> <br>Move<br> </html>");
                    }
                }
                //this.repaint();
            }
        }
    }

    class actionListener implements ActionListener {
        private int num;

        public actionListener(int num) {
            this.num = num;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            actionClick(num);
        }
    }

    private void updateMap() {
        String[][][] abvmap = controller.getAbvMap();
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 3; j++) {
                for(int k = 0; k < 3; k++) {
                    if(abvmap == null) map[i][j][k].setText("");
                    else map[i][j][k].setText(abvmap[i][j][k]);
                }
            }
        }
        //partly same with upper code
        charState[LEFT].setText("Your characters:\n" + ConstantTool.arrayFormat(Arrays.toString(controller.getList()[LEFT])));
        charState[RIGHT].setText("Enemy's characters:\n" + ConstantTool.arrayFormat(Arrays.toString(controller.getList()[RIGHT])));
        gameState.setText(String.format("round:%d action:%s winner:%d",
                controller.getRound(), controller.getPresentCha(LEFT), controller.getWinner()));
        Spell[] spell = controller.getPresentSpell(LEFT);
        if(spell == null){
            for(int i = 0; i < 4; i++) {
                action[i].setText("<html> <br>Move<br> </html>");
            }
        } else {
            for(int i = 0; i < 3; i++) {
                action[i].setText(ConstantTool.multiLine(spell[i].toString()));
            }
            action[3].setText("<html> <br>Move<br> </html>");
        }
    }

    private void updatePut() {
        String[][] wantedmap = controller.getWantedMap(LEFT);
        for(int j = 0; j < 3; j++) {
            for(int k = 0; k < 3; k++) {
                if(wantedmap[j][k] != null) map[LEFT][j][k].setText(wantedmap[j][k]);
                else map[LEFT][j][k].setText("");
            }
        }
        gameState.setText(String.format("round:%d action:%s winner:%d",
                controller.getRound(), controller.getPresentCha(LEFT), controller.getWinner()));
        Spell[] spell = controller.getPresentSpell(LEFT);
        if(spell == null){
            for(int i = 0; i < 4; i++) {
                action[i].setText("<html> <br>Move<br> </html>");
            }
        } else {
            for(int i = 0; i < 3; i++) {
                action[i].setText(ConstantTool.multiLine(spell[i].toString()));
            }
            action[3].setText("<html> <br>Move<br> </html>");
        }
    }

}
