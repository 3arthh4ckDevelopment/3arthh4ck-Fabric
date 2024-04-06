package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.TextColor;

import java.util.ArrayList;
import java.util.List;

public class TextRenderUtil {

    public static List<String> wrapWords(String text, double width)
    {
        List<String> result = new ArrayList<>();

        if (Managers.TEXT.getStringWidth(text) > width || text.contains("\n"))
        {
            String[] words = text.split(" ");
            StringBuilder current = new StringBuilder();
            char lastColorCode = 65535;
            for (String word : words)
            {
                String[] lineBreak = splitWithEmptyWords(word);
                if (lineBreak.length > 1)
                {
                    if (Managers.TEXT.getStringWidth(current + lineBreak[0] + " ") >= width)
                    {
                        result.add(current.toString());
                        current = new StringBuilder()
                                .append(TextColor.SECTIONSIGN)
                                .append(lastColorCode)
                                .append(lineBreak[0]);
                    }
                    else
                    {
                        current.append(lineBreak[0]);
                    }

                    char checkColorCode = getLastColorCode(lineBreak[0]);
                    if (checkColorCode != ' ')
                    {
                        lastColorCode = checkColorCode;
                    }

                    for (int i = 1; i < lineBreak.length; i++)
                    {
                        result.add(current.toString());
                        current = new StringBuilder()
                                .append(TextColor.SECTIONSIGN)
                                .append(lastColorCode)
                                .append(lineBreak[i]);

                        if (i == lineBreak.length - 1)
                        {
                            current.append(" ");
                        }

                        checkColorCode = getLastColorCode(lineBreak[i]);
                        if (checkColorCode != ' ')
                        {
                            lastColorCode = checkColorCode;
                        }
                    }

                    continue;
                }

                char checkColorCode = getLastColorCode(word);
                if (checkColorCode != ' ') {
                    lastColorCode = checkColorCode;
                }

                if (Managers.TEXT.getStringWidth(current + word + " ") < width) // ???
                {
                    current.append(word).append(" ");
                }
                else
                {
                    result.add(current.toString());
                    current = new StringBuilder()
                            .append(TextColor.SECTIONSIGN)
                            .append(lastColorCode)
                            .append(word)
                            .append(" ");
                }
            }

            if (!current.isEmpty())
            {
                if (Managers.TEXT.getStringWidth(current.toString()) < width)
                {
                    result.add(TextColor.SECTIONSIGN
                            + ""
                            + lastColorCode
                            + current
                            + " ");
                }
                else
                {
                    result.addAll(formatString(current.toString(), width));
                }
            }
        }
        else
        {
            result.add(text);
        }

        return result;
    }

    private static Character getLastColorCode(String word) {
        char lastColorCode = ' ';
        for (int i = 0; i < word.length(); i++)
        {
            char c = word.charAt(i);
            if (c == TextColor.SECTIONSIGN && i + 1 < word.length())
            {
                lastColorCode = word.charAt(i + 1);
            }
        }

        return lastColorCode;
    }

    public static List<String> formatString(String string, double width) {
        List<String> result = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        char lastColorCode = 65535;
        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if ((c == TextColor.SECTIONSIGN) && (i < string.length() - 1))
            {
                lastColorCode = string.charAt(i + 1);
            }

            if (Managers.TEXT.getStringWidth(current.toString() + c) < width)
            {
                current.append(c);
            }
            else
            {
                result.add(current.toString());
                current = new StringBuilder()
                        .append(TextColor.SECTIONSIGN)
                        .append(lastColorCode)
                        .append(c);
            }
        }

        if (!current.isEmpty())
        {
            result.add(current.toString());
        }

        return result;
    }

    private static String[] splitWithEmptyWords(String word) {
        List<String> result = new ArrayList<>(1);
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch == '\n') {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(ch);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

}
