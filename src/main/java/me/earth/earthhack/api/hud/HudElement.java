package me.earth.earthhack.api.hud;

import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.hud.data.DefaultHudData;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * hud element
 * @author megyn
 */
public abstract class HudElement extends SettingContainer
        implements Globals, Subscriber, Nameable {
    private final Setting<Boolean> enabled =
            register(new BooleanSetting("Enabled", false));
    /**
     * These may not be settings in the future, so getting/settings their values will be done through wrapper methods.
     * These should really not be accessed directly for the time being.
     */
    private final Setting<Float> x =
            register(new NumberSetting<>("X", 2.0f, -20.0f, 2000.0f))
                    .setComplexity(Complexity.Dev);
    private final Setting<Float> y =
            register(new NumberSetting<>("Y", 2.0f, -20.0f, 2000.0f))
                    .setComplexity(Complexity.Dev);
    private final Setting<Integer> z =
            register(new NumberSetting<>("Z", 0, -2000, 2000))
                    .setComplexity(Complexity.Dev); // Z level determines rendering order.
    private final Setting<Float> textScale =
            register(new NumberSetting<>("Scale", 1.0f, 0.1f, 3.0f))
                    .setComplexity(Complexity.Dev);


    protected static final TextRenderer RENDERER = Managers.TEXT;
    protected final List<Listener<?>> listeners = new ArrayList<>();
    private final AtomicBoolean enableCheck = new AtomicBoolean();
    private final AtomicBoolean inOnEnable  = new AtomicBoolean();
    private HudElement snappedTo;
    private SnapAxis axis = SnapAxis.NONE;
    private ModuleData<?> data;

    private final String name;
    private final HudCategory category;
    private float width  = 100;
    private float height = 100;

    // private float scale  = 1.0f;

    // private final boolean scalable; // TODO: hud element scaling AFTER everything else works!
    // private boolean scaling = false;
    // private GuiUtil.Edge currentEdge;

    private boolean dragging;
    private float draggingX;
    private float draggingY;
    protected float animationY = 0;

    /**
     * Creates a new HudElement. It's important that the given name
     * does not contain any whitespaces and that no hud elements with the
     * same name exist. A hud element's name is its unique identifier.
     *
     * @param name name of the hud element
     * @param x x of the element
     * @param y y of the element
     */

    public HudElement(String name, HudCategory category, float x, float y) {
        this.name = name;
        this.category = category;
        this.data = new DefaultHudData<>(this);
        this.enabled.addObserver(this::onEnabledEvent);
        this.x.setValue(x);
        this.y.setValue(y);
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

    // TODO: maybe a bit of abstraction with these?
    public final void toggle()
    {
        if (isEnabled())
            this.disable();
        else
            this.enable();
    }

    public final void enable()
    {
        if (!isEnabled())
            enabled.setValue(true);
    }

    public final void disable()
    {
        if (isEnabled())
            enabled.setValue(false);
    }

    public final void load()
    {
        if (this.isEnabled() && !Bus.EVENT_BUS.isSubscribed(this)) {
            Bus.EVENT_BUS.subscribe(this);
        }

        onLoad();
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

    public HudCategory getCategory() {
        return category;
    }

    public ModuleData<?> getData()
    {
        return data;
    }

    public void setData(ModuleData<?> data) {
        if (data != null)
            this.data = data;
    }

    public void guiUpdate(int mouseX, int mouseY) {
        if (dragging) {
            setX(mouseX - draggingX);
            setY(mouseY - draggingY);
        }
    }

    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Render2DUtil.drawBorderedRect(context.getMatrices(), x.getValue(), y.getValue(), x.getValue() + width, y.getValue() + height, 1.0f, 0x00000000, 0xaa000000);
    }

    public void guiKeyPressed(char eventChar, int key) {}

    public void guiMouseClicked(double mouseX, double mouseY, int mouseButton) {
        // currentEdge = GuiUtil.getHoveredEdge(this, mouseX, mouseY, 5);
        if (GuiUtil.isHovered(this, mouseX, mouseY)) {
            setDragging(true);
            draggingX = (float) mouseX - getX();
            draggingY = (float) mouseY - getY();
        }
    }

    public void guiMouseReleased(double mouseX, double mouseY, int mouseButton) {
        setDragging(false);
        // scaling = false;
    }

    public void hudUpdate() {}

    public abstract void hudDraw(DrawContext context);

    public boolean isOverlapping(HudElement other) {
        double[] rec1 = new double[]{this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight()};
        double[] rec2 = new double[]{other.getX(), other.getY(), other.getX() + other.getWidth(), other.getY() + other.getHeight()};
        if (rec1[0] == rec1[2] || rec1[1] == rec1[3] ||
                rec2[0] == rec2[2] || rec2[1] == rec2[3]) {
            // the line cannot have positive overlap
            return false;
        }

        return !(rec1[2] <= rec2[0] ||   // left
                rec1[3] <= rec2[1] ||   // bottom
                rec1[0] >= rec2[2] ||   // right
                rec1[1] >= rec2[3]);    // top
    }

    @Override
    public Collection<Listener<?>> getListeners() {
        return this.listeners;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else if (o instanceof HudElement)
        {
            String name = this.name;
            return name != null && name.equals(((HudElement) o).name);
        }

        return false;
    }

    public float getX() {
        return x.getValue();
    }

    public void setX(float x) {
        this.x.setValue(MathHelper.clamp(x, 0, Render2DUtil.getScreenWidth()));
    }

    public float getY() {
        return y.getValue();
    }

    public void setY(float y) {
        this.y.setValue(MathHelper.clamp(y, 0, Render2DUtil.getScreenHeight()));
    }

    public float getZ() {
        return z.getValue();
    }

    public void setZ(int z) {
        this.z.setValue(z);
    }

    public float getScale() {
        return textScale.getValue();
    }

    public void setScale(float scale) {
        this.textScale.setValue(scale);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDraggingX() {
        return draggingX;
    }

    public void setDraggingX(float draggingX) {
        this.draggingX = draggingX;
    }

    public float getDraggingY() {
        return draggingY;
    }

    public void setDraggingY(float draggingY) {
        this.draggingY = draggingY;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
        if (!dragging) {
            for (HudElement element : Managers.ELEMENTS.getRegistered()
                    .stream()
                    .sorted(Comparator.comparing(HudElement::getZ)).collect(Collectors.toList())) {
                if (this.isOverlapping(element)) {
                    this.setSnappedTo(element);
                    if (this.getY() < element.getY() + element.getHeight() / 2) {
                        setAxis(SnapAxis.TOP);
                    } else {
                        setAxis(SnapAxis.BOTTOM);
                    }
                } else {
                    setAxis(SnapAxis.NONE);
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    public HudElement getSnappedTo() {
        return snappedTo;
    }

    public void setSnappedTo(HudElement snappedTo) {
        this.snappedTo = snappedTo;
    }

    public SnapAxis getAxis() {
        return axis;
    }

    public void setAxis(SnapAxis axis) {
        this.axis = axis;
    }
}
