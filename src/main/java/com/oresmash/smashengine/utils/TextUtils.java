package com.oresmash.smashengine.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TextUtils {

    private static final Map<Character, String> smallCapsMap = new HashMap<>();
    static {
        smallCapsMap.put('a', "ᴀ");
        smallCapsMap.put('b', "ʙ");
        smallCapsMap.put('c', "ᴄ");
        smallCapsMap.put('d', "ᴅ");
        smallCapsMap.put('e', "ᴇ");
        smallCapsMap.put('f', "ꜰ");
        smallCapsMap.put('g', "ɢ");
        smallCapsMap.put('h', "ʜ");
        smallCapsMap.put('i', "ɪ");
        smallCapsMap.put('j', "ᴊ");
        smallCapsMap.put('k', "ᴋ");
        smallCapsMap.put('l', "ʟ");
        smallCapsMap.put('m', "ᴍ");
        smallCapsMap.put('n', "ɴ");
        smallCapsMap.put('o', "ᴏ");
        smallCapsMap.put('p', "ᴘ");
        smallCapsMap.put('q', "ǫ");
        smallCapsMap.put('r', "ʀ");
        smallCapsMap.put('s', "ꜱ");
        smallCapsMap.put('t', "ᴛ");
        smallCapsMap.put('u', "ᴜ");
        smallCapsMap.put('v', "ᴠ");
        smallCapsMap.put('w', "ᴡ");
        smallCapsMap.put('x', "x");
        smallCapsMap.put('y', "ʏ");
        smallCapsMap.put('z', "ᴢ");
    }

    public String toSmallCaps(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            sb.append(smallCapsMap.getOrDefault(c, String.valueOf(c)));  // Default to the original character if no mapping exists
        }
        return sb.toString();
    }

    /**
     * Colorizes the input text using MiniMessage and removes the italic decoration if it is absent.
     *
     * @param text The text to colorize.
     * @return The colorized text component.
     */
    public Component colorize(String text) {
        return MiniMessage.miniMessage().deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public String decimal(double value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative.");
        }

        StringBuilder pattern = new StringBuilder("#,##0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            pattern.append("#".repeat(decimalPlaces));
        }

        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(value);
    }

    public String character(double value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative.");
        }

        if (value < 1_000) {
            return decimal(value, decimalPlaces);
        }

        String[] units = {"k", "M", "B", "T", "Qd", "Qu", "Sx", "Sp", "Oc", "Nn", "Dc", "Ud", "Dd", "Td", "Qt"};
        int unitIndex = (int) (Math.log10(value) / 3) - 1;
        unitIndex = Math.min(unitIndex, units.length - 1);

        double unitValue = value / Math.pow(10, 3 * (unitIndex + 1));
        String formattedValue = decimal(unitValue, decimalPlaces);

        return formattedValue + units[unitIndex];
    }

    public String capitalize(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s");
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return result.toString().trim();
    }

    public String time(Integer minutes) {
        int weeks = minutes / 10080;
        minutes = minutes % 10080;
        int days = minutes / 1440;
        minutes = minutes % 1440;
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        StringBuilder result = new StringBuilder();
        if (weeks > 0) {
            result.append(weeks).append("w");
        }

        if (days > 0) {
            result.append(days).append("d");
        }

        if (hours > 0) {
            result.append(hours).append("h");
        }

        if (remainingMinutes > 0) {
            result.append(remainingMinutes).append("m");
        }

        return result.toString();
    }

    public String random(int length) {
        if(length < 0) throw new IllegalArgumentException("Attempted to generate a random character string with a negative length!");

        StringBuilder output = new StringBuilder();

        Random random = new Random();
        for(int i = 0; i < length; i++) {
            output.append((char) (random.nextInt(26) + 'a'));
        }

        return output.toString();
    }

    public String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public Location stringToLocation(String locationString) {
        String[] parts = locationString.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

}
