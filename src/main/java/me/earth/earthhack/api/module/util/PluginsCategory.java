package me.earth.earthhack.api.module.util;

import me.earth.earthhack.api.module.Module;

import java.util.HashMap;
import java.util.Map;

public class PluginsCategory {
    private static final PluginsCategory INSTANCE = new PluginsCategory();

    private final Category pluginCategory = new Category("Plugins", 6);
    private final Map<Module, Category> pluginModulesMap = new HashMap<>();

    public Category[] getCategories() {
        Category[] categories = new Category[Category.values().length + 1];
        System.arraycopy(Category.values(), 0, categories, 0, Category.values().length);
        categories[Category.values().length] = getCategory();
        return categories;
    }

    public static PluginsCategory getInstance() {
        return INSTANCE;
    }

    public void addPluginModule(Module module) {
        pluginModulesMap.put(module, module.getCategory());
    }

    public Category getCategory() {
        return pluginCategory;
    }

    public Map<Module, Category> getPluginsModuleList() {
        return pluginModulesMap;
    }
}
