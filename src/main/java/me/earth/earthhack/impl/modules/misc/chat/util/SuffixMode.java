package me.earth.earthhack.impl.modules.misc.chat.util;

import me.earth.earthhack.impl.Earthhack;

public enum SuffixMode {
    None(null),
    Earth("³ᴀʀᴛʜʜ⁴ᴄᴋ"),
    CuteEarth("(っ◔◡◔)っ ♥ 3arthh4ck " + Earthhack.VERSION + " ♥"),
    Phobos("ᴘʜᴏʙᴏꜱ"),
    Rusher("\u02B3\u1D58\u02E2\u02B0\u1D49\u02B3\u02B0\u1D43\u1D9C\u1D4F"),
    Future("\uA730\u1D1C\u1D1B\u1D1C\u0280\u1D07"),
    Konas("Konas owns me and all \u2022\u1D17\u2022"),
    GameSense("\u0262\u1D00\u1D0D\u1D07\uA731\u1D07\u0274\uA731\u1D07"),
    KamiBlue("\u1D0B\u1D00\u1D0D\u026A \u0299\u029F\u1D1C\u1D07"),
    Custom(null);

    private final String suffix;

    SuffixMode(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
