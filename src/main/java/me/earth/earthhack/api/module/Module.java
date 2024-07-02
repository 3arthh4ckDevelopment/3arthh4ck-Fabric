package me.earth.earthhack.api.module;

import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.bind.Toggle;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Hideable;
import me.earth.earthhack.api.util.interfaces.Nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Module.
 */
public abstract class Module extends SettingContainer
        implements Globals, Subscriber, Hideable, Displayable, Nameable
{
    /** Listeners for the EventBus. */
    protected final List<Listener<?>> listeners = new ArrayList<>();
    private final AtomicBoolean enableCheck = new AtomicBoolean();
    private final AtomicBoolean inOnEnable  = new AtomicBoolean();

    private final Setting<String> name;
    private final Setting<Bind> bind =
            register(new BindSetting("Bind", Bind.none()));
    private final Setting<Hidden> hidden =
            register(new EnumSetting<>("Hidden", Hidden.Visible))
                    .setComplexity(Complexity.Medium);
    private final Setting<Boolean> enabled =
            register(new BooleanSetting("Enabled", false));
    private final Setting<Toggle> bindMode =
            register(new EnumSetting<>("Toggle", Toggle.Normal))
                    .setComplexity(Complexity.Medium);
    private final Setting<Boolean> visibility =
            register(new BooleanSetting("Visible", true))
                    .setVisibility(false);

    private Category category;
    private ModuleData<?> data;
    public boolean searchVisibility = visibility.getValue();

    /**
     * Creates a new Module. It's important that the given name
     * does not contain any whitespaces and that no modules with the
     * same name exist. A modules name is its unique identifier.
     *
     * @param name the name for the new module.
     * @param category the category of the new module.
     */
    public Module(String name, Category category) {
        this.name = register(new StringSetting("Name", name))
                .setComplexity(Complexity.Medium);
        this.category = category;
        this.data     = new DefaultData<>(this);
        this.enabled.addObserver(this::onEnabledEvent);
    }

    protected void onEnabledEvent(SettingEvent<Boolean> event) {
        if (event.isCancelled())
            return;

        enableCheck.set(event.getValue());
        if (event.getValue() && !Bus.EVENT_BUS.isSubscribed(this)) {
            inOnEnable.set(true);
            onEnable();
            inOnEnable.set(false);
            if (enableCheck.get()) {
                Bus.EVENT_BUS.subscribe(this);
            }
        }
        else if (!event.getValue()
                && (Bus.EVENT_BUS.isSubscribed(this) || inOnEnable.get()))
        {
            Bus.EVENT_BUS.unsubscribe(this);
            onDisable();
        }
    }

    /**
     *
     * @return the actual name for the module. (e.g. When display name is Aura, this would return KillAura.)
     */
    @Override
    public String getName()
    {
        return name.getInitial();
    }

    /**
     *
     * @return the display name for the module. This is user-set.
     */
    @Override
    public String getDisplayName()
    {
        return name.getValue();
    }

    /**
     * Set the display name for the module
     */
    @Override
    public void setDisplayName(String name)
    {
        this.name.setValue(name);
    }

    /**
     * Change the module state (enabled/disabled)
     */
    public final void toggle() {
        if (isEnabled())
            disable();
        else
            enable();
    }

    public final void enable() {
        if (!isEnabled())
            enabled.setValue(true);
    }

    public final void disable() {
        if (isEnabled())
            enabled.setValue(false);
    }

    public final void load() {
        if (this.isEnabled() && !Bus.EVENT_BUS.isSubscribed(this)) {
            Bus.EVENT_BUS.subscribe(this);
        }

        onLoad();
    }

    public boolean isEnabled()
    {
        return enableCheck.get();
    }

    /**
     *
     * @return the display info (Stuff in the ArrayList between [] brackets. (e.g. AutoCrystal [target].))
     * This would return the target of the AutoCrystal, if this was used to check AutoCrystal's DisplayInfo.
     */
    public String getDisplayInfo()
    {
        return null;
    }

    /**
     *
     * @return the category of the module
     */
    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    /**
     *
     * @return the description of the module.
     */
    public ModuleData<?> getData()
    {
        return data;
    }

    public void setData(ModuleData<?> data) {
        if (data != null)
            this.data = data;
    }

    /**
     *
     * @return the key-bind for the module.
     */
    public Bind getBind()
    {
        return bind.getValue();
    }

    /**
     *
     * @param bind The key-bind we set for a module.
     */
    public void setBind(Bind bind)
    {
        this.bind.setValue(bind);
    }

    public Toggle getBindMode()
    {
        return bindMode.getValue();
    }

    @Override
    public void setHidden(Hidden hidden)
    {
        this.hidden.setValue(hidden);
    }

    /**
     *
     * @return whether the module is hidden in the Arraylist.
     */
    @Override
    public Hidden isHidden()
    {
        return hidden.getValue();
    }

    public boolean isVisible()
    {
        return visibility.getValue();
    }

    public void setShown(boolean shown)
    {
        visibility.setValue(shown);
    }

    protected void onEnable()
    {
        /* Implemented by the module */
    }

    protected void onDisable()
    {
        /* Implemented by the module */
    }

    protected void onLoad()
    {
        /* Implemented by the module */
    }

    /**
     *
     * @return the listeners the module is using.
     * For example, for AutoCrystal this would return all its listeners, like ListenerDestroyBlock or ListenerExplosion.
     */
    @Override
    public Collection<Listener<?>> getListeners()
    {
        return listeners;
    }

    @Override
    public int hashCode()
    {
        return this.name.getInitial().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        }
        else if (o instanceof Module module) {
            String name = this.name.getInitial();
            return name != null && name.equals(module.name.getInitial()) && this.getClass() == o.getClass() && this.category == module.category;
        }

        return false;
    }

}
