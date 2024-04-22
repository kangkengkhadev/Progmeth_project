package util;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public class InputUtility {
    private static ArrayList<KeyCode> keyPressed = new ArrayList<>();

    public static boolean getKeyPressed(KeyCode keycode) {
        return keyPressed.contains(keycode);
    }
    public static void setKeyPressed(KeyCode keycode,boolean pressed) {
        if(pressed){
            if(!keyPressed.contains(keycode)){
                keyPressed.add(keycode);
            }
        }else{
            keyPressed.remove(keycode);
        }
    }
}
