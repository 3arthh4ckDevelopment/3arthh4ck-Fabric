package me.earth.earthhack.api.plugin;

/**
 * Plugins can be used to add functionality, modules or commands
 * to 3arthh4ck. Plugins can contain Mixins and an AccessWidener.
 * Plugins are located in the earthhack/plugins folder. A Plugin should
 * be a jar file. Dependencies like Mixin don't need to be included
 * as they are already included in the 3arthh4ck jar.
 */

// TODO: CorePlugin implementing IClassTransformer for ASM if requested?
// TODO: runtime loading/unloading
public interface Plugin
{
    /**
     * This method is called at the start of the game.
     * When this method is called, the Plugin can use
     * mixins and/or fields & methods modified by the AccessWidener.
     */
    default void load() {}

    /**
     * Loads this Plugin during runtime.
     * When this method is called, the Plugin CANNOT
     * reference any of the Plugin's mixins or
     * modified fields & methods by the AccessWidener.
     */
    default void loadRuntime() {}
}
