package com.example.godin.proyecto_ed2;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.*;
/**
 *
 * @author godin
 */
public class zigzag {

    public int levelthis;
    public String textthis;

    public String codezigzag(String text, int level) {
        String out = "";
        int size_waves = level * 2 - 2;

        int leftover_characters = (text.length() % size_waves);
        int miss_characters;

        if (leftover_characters == 0) {

            miss_characters = 0;
        } else {
            miss_characters = size_waves - leftover_characters;
        }

        if (miss_characters != 0) {
            for (int i = 0; i < miss_characters; i++) {
                text += " ";
            }
        }

        Character[] charcatersarray = ArrayUtils.toObject(text.toCharArray());
        ArrayList<Character> characters = new ArrayList<>(Arrays.asList(charcatersarray));

        int number_waves = text.length() / size_waves;

        for (int i = 0; i < level; i++) {

            if (i == 0) {
                for (int j = 0; j < number_waves; j++) {
                    out = out + characters.get(j * size_waves);
                }
            } else if (i == level - 1) {
                for (int j = level - 1; j <= text.length(); j = j + size_waves) {
                    out = out + characters.get(j);
                }
            } else {

                for (int j = i; j <= number_waves * size_waves; j = j + size_waves) {
                    out = out + characters.get(j) + characters.get(j + (size_waves - i * 2));

                }
            }

        }
        return out;

    }

    public String decodezigzag(String encodetext, int level) {

        int size_waves = level * 2 - 2;
        int number_waves = encodetext.length() / size_waves;
        int leftover_characters = (encodetext.length() % size_waves);
        int miss_characters = size_waves - leftover_characters;
        ArrayList<ArrayList<Character>> divisions = new ArrayList<>();

        String out = "";
        ArrayList<Character> element;
        Character[] elementarray;
        elementarray = ArrayUtils.toObject((encodetext.substring(0, number_waves).toCharArray()));
        element =new ArrayList<>( Arrays.asList(elementarray));

        divisions.add(element);
        for (int i = number_waves; i <= encodetext.length() - number_waves * 3; i = i + number_waves * 2) {

            elementarray = ArrayUtils.toObject(encodetext.substring(i, i + number_waves * 2).toCharArray());
            element =new ArrayList<>( Arrays.asList(elementarray));

            divisions.add(element);

        }
        elementarray = ArrayUtils.toObject(encodetext.substring(encodetext.length() - number_waves, encodetext.length()).toCharArray());
        element =new ArrayList<>( Arrays.asList(elementarray));

        divisions.add(element);

        int iterations = 0;

        while (iterations < number_waves) {
            boolean llego_al_final=false;
            int cont = 0;
            int index = 0;
            while (cont != size_waves) {
                out += divisions.get(index).remove(0);

                if (index == (divisions.size() - 1)) {
                    llego_al_final=true;
                }

                if(llego_al_final){
                    index--;
                }else{
                    index++;
                }
                cont++;
            }
            iterations++;

        }

        return out;
    }

}
