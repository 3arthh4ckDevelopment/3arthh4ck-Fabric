package me.earth.earthhack.impl.util.misc;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ai_24
 * @version 0.9
 * @since 20/07/2023
 *
 * i should switch to a decimal rating...
 */

public class ModulesRating {

    public List<Module> modules = new ArrayList<>();

    public ModulesRating() {
        for (Module m : Managers.MODULES.getRegistered()) {
            if (m.isVisible())
                modules.add(m);
        }
    }

    /**
     * Calculates the module name rating based on the user input
     *
     * @return A List with the active ModuleSorting
     */

    public List<Module> modulesVisibility(String input, Boolean precision, Boolean aliases) {
        List<Module> endRes = new ArrayList<>();

        String userInput = input.toLowerCase().replaceAll("\\s+", "");


        // module name rating
        int topRating = -1;
        for (Module module : modules) {
            List<String> names = moduleNames(module, aliases);
            for (String s : names) {
                int rating = ratingCalc(s, userInput);
                if (rating > topRating)
                    topRating = rating;
            }
        }

        // rating check
        for (Module module : modules) {
            List<String> names = moduleNames(module, aliases);
            for (String s : names) {
                int rating = ratingCalc(s, userInput);
                if (rating >= topRating - (precision ? 0 : 1))
                    endRes.add(module);
            }
        }

        return endRes;
    }

    private List<String> moduleNames(Module module, boolean aliases) {
        List<String> names = new ArrayList<>();
        if (module.getData().getAliases().length != 0 && aliases) {
            for (String s : module.getData().getAliases()) {
                if (s != null) {
                    names.add(s);
                }
            }
        }
        if (!Objects.equals(module.getName(), module.getDisplayName())) {
            names.add(module.getDisplayName());
        }
        names.add(module.getName());
        return names;
    }


    private int ratingCalc(String moduleName, String userInput) {
        moduleName = moduleName.toLowerCase();
        int rating = 0;

        // check if the word is literally the same
        if (moduleName.equals(userInput)) {
            return 100;
        } else  {
            if (moduleName.startsWith(userInput)) {
                rating++;
            }

            if (userInput.length() == 1 && moduleName.contains(userInput)) { // check for 1 char
                return 100;
            } else { // else we continue to try combinations of 3+ words and count how many times does it match
                for (int i = 1; i <= userInput.length(); i++) {
                    int moduleNameLength = moduleName.length();

                    if (moduleNameLength > i) {
                        int index = 0;
                        while (index <= moduleNameLength - i) {
                            if (index + i <= userInput.length()) {
                                String subSequence = userInput.substring(index, index + i);
                                if (moduleName.contains(subSequence)) {
                                    rating += subSequence.length();
                                }
                            }
                            index++;
                        }
                    }
                }
            }
        }

        return rating;
    }

}
