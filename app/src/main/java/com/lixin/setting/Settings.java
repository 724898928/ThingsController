package com.lixin.setting;

import com.lixin.gameInterface.FileIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by li on 2018/9/3.
 */

public class Settings {
    public static boolean soundEnabled = true;
    public static int[] highscores = new int[]{100, 80, 50, 30, 10};

    public static void load(FileIO fileIO) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(fileIO.readFile(".setiing")));
            soundEnabled = Boolean.parseBoolean(in.readLine());
            for (int i = 0; i < 5; i++) {
                highscores[i] = Integer.parseInt(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save(FileIO fileIO) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(fileIO.writeFile(".setiing")));
            out.write(Boolean.toString(soundEnabled));
            for (int i = 0; i < 5; i++) {
                out.write(Integer.toString(highscores[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
