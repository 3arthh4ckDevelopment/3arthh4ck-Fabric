package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.RenderTest;
import me.earth.earthhack.impl.modules.client.accountspoof.AccountSpoof;
import me.earth.earthhack.impl.modules.client.anticheat.AntiCheat;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.compatibility.Compatibility;
import me.earth.earthhack.impl.modules.client.configs.ConfigModule;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.modules.client.debug.Debug;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.notifications.Notifications;
import me.earth.earthhack.impl.modules.client.rpc.RPC;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.client.settings.SettingsModule;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;
import me.earth.earthhack.impl.modules.combat.holefiller.HoleFiller;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.antipackets.AntiPackets;
import me.earth.earthhack.impl.modules.misc.antipotion.AntiPotion;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.autotool.AutoTool;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.breakesp.BreakESP;
import me.earth.earthhack.impl.modules.render.fullbright.Fullbright;
import me.earth.earthhack.impl.modules.render.holeesp.HoleESP;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;
import me.earth.earthhack.impl.modules.render.norender.NoRender;

import java.util.ArrayList;

public class ModuleManager extends IterationRegister<Module>
{
    public void init()
    {
        Earthhack.getLogger().info("Initializing ModuleSorting.");

        this.forceRegister(new RenderTest());



        /* ----- CLIENT ----- */
        this.forceRegister(new AccountSpoof());
        this.forceRegister(new AntiCheat());
        this.forceRegister(new AutoConfig());
        // this.forceRegister(new Capes());
        this.forceRegister(new ClickGui());
        // this.forceRegister(new Colors());
        this.forceRegister(new Commands());
        // this.forceRegister(new PluginsGui());
        this.forceRegister(new SettingsModule());
        this.forceRegister(new ConfigModule());
        this.forceRegister(new Debug());
        this.forceRegister(new FontMod());
        this.forceRegister(new RPC());
        this.forceRegister(new HudEditor());
        this.forceRegister(new Management());
        // this.forceRegister(new NoSpoof());
        this.forceRegister(new Notifications());
        this.forceRegister(new Compatibility());
        this.forceRegister(new Safety());
        // this.forceRegister(new ServerModule());
        // this.forceRegister(new TabModule());
        this.forceRegister(new Media());

        /* ----- COMBAT ----- */
        this.forceRegister(new AntiSurround());
        // this.forceRegister(new AntiTrap());
        // this.forceRegister(new AnvilAura());
        // this.forceRegister(new AutoArmor());
        this.forceRegister(new AutoCrystal());
        this.forceRegister(new AutoTrap());
        // this.forceRegister(new BedBomb());
        // this.forceRegister(new BedBombRewrite());
        this.forceRegister(new BowSpam());
        // this.forceRegister(new Blocker());
        // this.forceRegister(new BowKiller());
        // this.forceRegister(new Criticals());
        // this.forceRegister(new CrystalBomber());
        this.forceRegister(new HoleFiller());
        this.forceRegister(new KillAura());
        this.forceRegister(new Offhand());
        // this.forceRegister(new PistonAura());
        this.forceRegister(new Surround());
        // this.forceRegister(new SelfTrap());
        // this.forceRegister(new WebAura());

        /* ------ MISC ------ */
        this.forceRegister(new Announcer());
        // this.forceRegister(new AntiAim());
        this.forceRegister(new AntiPackets());
        this.forceRegister(new AntiPotion());
        // this.forceRegister(new AntiVanish());
        // this.forceRegister(new AutoEat());
        this.forceRegister(new AutoLog());
        // this.forceRegister(new AutoReconnect());
        // this.forceRegister(new AutoRespawn());
        // this.forceRegister(new BuildHeight());
        this.forceRegister(new Chat());
        // this.forceRegister(new ChorusControl());
        // this.forceRegister(new ExtraTab());
        // this.forceRegister(new Logger());
        // this.forceRegister(new MCF());
        // this.forceRegister(new MobOwner());
        // this.forceRegister(new NoAFK());
        // this.forceRegister(new NoHandShake());
        // this.forceRegister(new NoInteract());
        this.forceRegister(new NoInterp());
        // this.forceRegister(new NoSoundLag());
        // this.forceRegister(new Nuker());
        // this.forceRegister(new Packets());
        this.forceRegister(new PingSpoof());
        // this.forceRegister(new Portals());
        // this.forceRegister(new SettingSpoof());
        // this.forceRegister(new SkinBlink());
        // this.forceRegister(new Spammer());
        // this.forceRegister(new ToolTips());
        // this.forceRegister(new TpsSync());
        this.forceRegister(new Tracker());
        // this.forceRegister(new TrueDurability());
        // this.forceRegister(new PacketDelay());


        // this.forceRegister(new Anchor());
        this.forceRegister(new AutoSprint());
        // this.forceRegister(new Avoid());
        this.forceRegister(new BlockLag());
        // this.forceRegister(new BoatFly());
        // this.forceRegister(new Clip());
        // this.forceRegister(new ElytraFlight());
        // this.forceRegister(new EntityControl());
        // this.forceRegister(new EntitySpeed());
        // this.forceRegister(new FastSwim());
        // this.forceRegister(new Flight());
        // this.forceRegister(new HighJump());
        // this.forceRegister(new ReverseStep());
        // this.forceRegister(new IceSpeed());
        // this.forceRegister(new Jesus());
        this.forceRegister(new LongJump());
        // this.forceRegister(new NoFall());
        // this.forceRegister(new NoMove());
        this.forceRegister(new NoSlowDown());
        this.forceRegister(new PacketFly());
        // this.forceRegister(new Phase());
        // this.forceRegister(new SafeWalk());
        this.forceRegister(new Speed());
        // this.forceRegister(new Stairs());
        this.forceRegister(new Step());
        // this.forceRegister(new TickShift());
        this.forceRegister(new Velocity());

        this.forceRegister(new Arrows());
        this.forceRegister(new AutoMine());
        this.forceRegister(new AutoTool());
        this.forceRegister(new Blink());
        // this.forceRegister(new BlockTweaks());
        // this.forceRegister(new Cleaner());
        // this.forceRegister(new ExpTweaks());
        this.forceRegister(new FakePlayer());
        this.forceRegister(new FastPlace());
        this.forceRegister(new FastEat());
        this.forceRegister(new Freecam());
        // this.forceRegister(new LiquidInteract());
        // this.forceRegister(new MiddleClickPearl());
        // this.forceRegister(new MultiTask());
        // this.forceRegister(new NCPTweaks());
        this.forceRegister(new NoGlitchBlocks());
        // this.forceRegister(new NoHunger());
        // this.forceRegister(new NoRotate());
        // this.forceRegister(new PhaseTrace());
        // this.forceRegister(new Reach());
        // this.forceRegister(new Replenish());
        this.forceRegister(new Scaffold());
        // this.forceRegister(new Sorter());
        this.forceRegister(new Spectate());
        this.forceRegister(new Speedmine());
        this.forceRegister(new Suicide());
        // this.forceRegister(new Swing());
        this.forceRegister(new Timer());
        this.forceRegister(new XCarry());


        // this.forceRegister(new BlockHighlight());
        // this.forceRegister(new BreadCrumbs());
        // this.forceRegister(new Chams());
        // this.forceRegister(new ESP());
        this.forceRegister(new Fullbright());
        this.forceRegister(new HoleESP());
        // this.forceRegister(new LagOMeter());
        this.forceRegister(new LogoutSpots());
        // this.forceRegister(new VoidESP());
        // this.forceRegister(new Nametags());
        // this.forceRegister(new NewChunks());
        this.forceRegister(new NoRender());
        // this.forceRegister(new Search());
        // this.forceRegister(new Skeleton());
        this.forceRegister(new BreakESP());
        // this.forceRegister(new Sounds());
        // this.forceRegister(new Tracers());
        // this.forceRegister(new CameraClip());
        // this.forceRegister(new XRay());
        // this.forceRegister(new CrystalChams());
        // this.forceRegister(new Trails());
        // this.forceRegister(new Trajectories());
        // this.forceRegister(new WayPoints());
        // this.forceRegister(new Weather());
        // this.forceRegister(new HandChams());
        // this.forceRegister(new RainbowEnchant());
        // this.forceRegister(new CrossHair());
        // this.forceRegister(new PopChams());
        // this.forceRegister(new ItemChams());
        this.forceRegister(new Ambience());
        // this.forceRegister(new ViewModel());

        Bus.EVENT_BUS.post(new PostInitEvent());
    }

    public void load()
    {
        Caches.setManager(this);
        for (Module module : getRegistered())
        {
            module.load();
        }
    }

    @Override
    public void unregister(Module module) throws CantUnregisterException
    {
        super.unregister(module);
        module.setRegistered(false);
        Bus.EVENT_BUS.unsubscribe(module);
    }

    protected void forceRegister(Module module)
    {
        registered.add(module);
        module.setRegistered(true);
        if (module instanceof Registrable)
        {
            ((Registrable) module).onRegister();
        }
    }

    public ArrayList<Module> getModulesFromCategory(Category moduleCategory) {
        final ArrayList<Module> iModules = new ArrayList<>();
        for (Module iModule : getRegistered()) {
            if (iModule.getCategory() == moduleCategory) iModules.add(iModule);
        }
        return iModules;
    }
}
