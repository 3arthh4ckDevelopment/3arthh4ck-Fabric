package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.hud.text.arraylist.HudArrayList;
import me.earth.earthhack.impl.hud.text.binds.Binds;
import me.earth.earthhack.impl.hud.text.coordinates.Coordinates;
import me.earth.earthhack.impl.hud.text.cps.Cps;
import me.earth.earthhack.impl.hud.text.degrees.Degrees;
import me.earth.earthhack.impl.hud.text.direction.Direction;
import me.earth.earthhack.impl.hud.text.durabilitynotifier.DurabilityNotifier;
import me.earth.earthhack.impl.hud.text.fps.FPS;
import me.earth.earthhack.impl.hud.text.greeter.Greeter;
import me.earth.earthhack.impl.hud.text.ping.Ping;
import me.earth.earthhack.impl.hud.text.pops.Pops;
import me.earth.earthhack.impl.hud.text.potions.Potions;
import me.earth.earthhack.impl.hud.text.safetyhud.SafetyHud;
import me.earth.earthhack.impl.hud.text.serverbrand.ServerBrand;
import me.earth.earthhack.impl.hud.text.session.Session;
import me.earth.earthhack.impl.hud.text.speed.HudSpeed;
import me.earth.earthhack.impl.hud.text.time.Time;
import me.earth.earthhack.impl.hud.text.tps.Tps;
import me.earth.earthhack.impl.hud.text.watermark.Watermark;
import me.earth.earthhack.impl.hud.visual.ShulkerSpy;
import me.earth.earthhack.impl.hud.visual.armor.Armor;
import me.earth.earthhack.impl.hud.visual.compass.Compass;
import me.earth.earthhack.impl.hud.visual.inventory.Inventory;
import me.earth.earthhack.impl.hud.visual.model.Model;
import me.earth.earthhack.impl.hud.visual.pvpresources.PvpResources;
import me.earth.earthhack.impl.hud.visual.skeetline.SkeetLine;
import me.earth.earthhack.impl.hud.visual.targethud.TargetHud;
import me.earth.earthhack.impl.hud.visual.textradar.TextRadar;
import me.earth.earthhack.impl.hud.visual.totem.Totem;

import java.util.ArrayList;
import java.util.Comparator;

public class HudElementManager extends IterationRegister<HudElement> {

    private int currentZ = 0;

    public void init()
    {
        Earthhack.getLogger().info("Initializing HUD elements...");
        this.forceRegister(new Watermark());
        this.forceRegister(new Greeter());
        this.forceRegister(new Coordinates());
        this.forceRegister(new Ping());
        this.forceRegister(new Tps());
        this.forceRegister(new FPS());
        this.forceRegister(new ServerBrand());
        this.forceRegister(new Direction());
        this.forceRegister(new Totem());
        this.forceRegister(new Model());
        this.forceRegister(new Armor());
        this.forceRegister(new Time());
        this.forceRegister(new TargetHud());
        this.forceRegister(new PvpResources());
        this.forceRegister(new Pops());
        this.forceRegister(new Cps());
        this.forceRegister(new DurabilityNotifier());
        this.forceRegister(new TextRadar());
        this.forceRegister(new HudArrayList());
        this.forceRegister(new Session());
        this.forceRegister(new Compass());
        this.forceRegister(new Binds());
        this.forceRegister(new SkeetLine());
        this.forceRegister(new SafetyHud());
        this.forceRegister(new Degrees());
        this.forceRegister(new Inventory());
        this.forceRegister(new HudSpeed());
        this.forceRegister(new ShulkerSpy());

        this.forceRegister(Potions.getInstance());

        //this.forceRegister(new ImageRender());
    }

    public void load()
    {
        for (HudElement element : getRegistered())
        {
            element.load();
        }
        registered.sort(Comparator.comparing(HudElement::getZ));
    }

    @Override
    public void unregister(HudElement element) throws CantUnregisterException
    {
        super.unregister(element);
        Bus.EVENT_BUS.unsubscribe(element);
    }

    @Override
    public void register(HudElement element) throws AlreadyRegisteredException {
        try {
            super.register(element);
        } catch (Exception e) {
            Earthhack.getLogger().warn("Failed to register hud element: " + element.getName());
            throw e;
        }
    }

    private void forceRegister(HudElement element)
    {
        registered.add(element);
        if (element instanceof Registrable)
        {
            ((Registrable) element).onRegister();
        }
        element.setZ(currentZ);
        currentZ++;
    }

    public ArrayList<HudElement> getModulesFromCategory(HudCategory hudCategory) {
        final ArrayList<HudElement> iHudElements = new ArrayList<>();
        for (HudElement iHudElement : getRegistered()) {
            if (iHudElement.getCategory() == hudCategory) iHudElements.add(iHudElement);
        }
        return iHudElements;
    }

}
