package me.earth.earthhack.impl.managers.config.helpers;

/*
public class HudConfigHelper extends AbstractConfigHelper<ElementConfig> {

    private final Register<HudElement> elements;

    public HudConfigHelper(Register<HudElement> elements)
    {
        this("hud", "hud", elements);
    }

    public HudConfigHelper(String name, String path, Register<HudElement> elements)
    {
        super(name, path);
        this.elements = elements;
    }

    @Override
    protected ElementConfig create(String name)
    {
        return ElementConfig.create(name.toLowerCase(), elements);
    }

    @Override
    protected JsonObject toJson(ElementConfig config)
    {
        JsonObject object = new JsonObject();
        for (HudValuePreset preset : config.getPresets())
        {
            JsonObject presetObject = preset.toJson();
            object.add(preset.getModule().getName(), presetObject);
        }

        return object;
    }

    @Override
    protected ElementConfig readFile(InputStream stream, String name)
    {
        JsonObject object = Jsonable.PARSER
                                    .parse(new InputStreamReader(stream))
                                    .getAsJsonObject();

        List<HudValuePreset> presets = new ArrayList<>(object.entrySet().size());
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            HudElement module = elements.getObject(entry.getKey());
            if (module == null)
            {
                Earthhack.getLogger().error("Config: Couldn't find element: "
                                            + entry.getKey());
                continue;
            }

            HudValuePreset preset =
                    new HudValuePreset(name, module, "A config Preset.");
            JsonObject element = entry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> s : element.entrySet())
            {
                boolean generated = module.getSetting(s.getKey()) == null;
                Setting<?> setting = module.getSettingConfig(s.getKey());
                if (setting == null)
                {
                    Earthhack.getLogger().error(
                        "Config: Couldn't find setting: " + s.getKey()
                            + " in element: " + module.getName() + ".");
                    continue;
                }

                preset.getValues().put(setting.getName(), s.getValue());
                if (generated
                        && GeneratedSettings.getGenerated(module)
                                            .remove(setting))
                {
                    module.unregister(setting);
                }
            }

            presets.add(preset);
        }

        ElementConfig config = new ElementConfig(name);
        config.setPresets(presets);
        return config;
    }

}
*/