package com.mygdx.game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Marcello395 on 2/22/18.
 */
public class GridGenerator {
    String[][] board;

    BufferedWriter bwriter = new BufferedWriter(new FileWriter("/Users/Marcello395/desktop/inputs.txt"));

    public GridGenerator(String[][] b) throws IOException {
        board = b;
    }

    public void generateFile() {
        try {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    board[i][j] = Integer.toString((int) (Math.random() * 10));
                    if (board[i][j].equals("0")) {
                        board[i][j] = "F";
                    }
                    bwriter.write(board[i][j] + " ");
                }
                bwriter.newLine();
            }

            bwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
