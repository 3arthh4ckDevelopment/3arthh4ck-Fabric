package me.earth.earthhack.api.config.preset;

/*
public class ElementConfig extends IdentifiedNameable implements Config {

    private List<HudValuePreset> presets = new ArrayList<>();

    public ElementConfig(String name)
    {
        super(name);
    }

    public void setPresets(List<HudValuePreset> presets)
    {
        if (presets != null)
        {
            this.presets = presets;
        }
    }

    public List<HudValuePreset> getPresets()
    {
        return presets;
    }

    @Override
    public void apply()
    {
        for (ModulePreset<HudElement> preset : presets)
        {
            preset.apply();
        }
    }

    public static ElementConfig create(String name, Register<HudElement> elements)
    {
        ElementConfig config = new ElementConfig(name);
        for (HudElement element : elements.getRegistered())
        {
            HudValuePreset preset = HudValuePreset.snapshot(name, element);
            config.presets.add(preset);
        }

        return config;
    }

}
*/