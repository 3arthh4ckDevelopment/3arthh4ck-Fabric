package me.earth.earthhack.impl.hud.visual.imagerender;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.ListSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.image.EfficientTexture;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.NameableImage;
import me.earth.earthhack.impl.util.text.ChatIDs;
import org.lwjgl.opengl.GL11;

public class ImageRender extends HudElement {

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Image));


    public final Setting<GifImage> gif =
            register(new ListSetting<>("Gif", Managers.FILES.getInitialGif(), Managers.FILES.getGifs()));


    private final Setting<NameableImage> image =
            register(new ListSetting<>("Name", Managers.FILES.getInitialImage(), Managers.FILES.getImages()));
    private final Setting<Float> width =
            register(new NumberSetting<>("Width", 1.0f, 0.0f, 500.0f));
    private final Setting<Float> height =
            register(new NumberSetting<>("Height", 1.0f, 0.0f, 500.0f));
    private final Setting<Float> scale =
            register(new NumberSetting<>("Scale", 1.0f, 0.0f, 30.0f));
    private final Setting<Boolean> reload =
            register(new BooleanSetting("Reload", false));

    private void render() {
        if (mc.player != null && mc.world != null) {
            if (mode.getValue() == Mode.Image && image.getValue().getTexture() != null) {
                // GL11.glPushMatrix();
                // GL11.glScalef(scale.getValue(), scale.getValue(), 1.0f);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, image.getValue().getTexture().getGlTextureId());
                Render2DUtil.drawCompleteImage(getX(), getY(), width.getValue(), height.getValue());
                // GL11.glScalef(1.0f, 1.0f, 1.0f);
                // GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                // GL11.glPopMatrix();
            } else if (mode.getValue() == Mode.Gif) {
                EfficientTexture texture = gif.getValue().getDynamicTexture();
                if (texture != null) {
                    // GL11.glPushMatrix();
                    // GL11.glScalef(scale.getValue(), scale.getValue(), 1.0f);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
                    Render2DUtil.drawCompleteImage(getX(), getY(), width.getValue(), height.getValue());
                    // GL11.glScalef(1.0f, 1.0f, 1.0f);
                    // GL11.glPopMatrix();
                    // GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                }
            }
        }
    }

    public ImageRender() {
        super("ImageRender",  HudCategory.Visual, 280, 280);
        this.setData(new SimpleHudData(this, "Displays an image."));

        this.reload.addObserver(event -> {
            event.setCancelled(true);
            Managers.FILES.init();
            Managers.CHAT.sendDeleteMessage("Reloaded resources", this.getName(), ChatIDs.COMMAND);
        });
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth("aaaaaa");
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringWidth("aaaaaa");
    }

    private enum Mode {
        Image,
        Gif
    }

}
