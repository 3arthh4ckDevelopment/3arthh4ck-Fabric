package me.earth.earthhack.impl.modules.client.rpc;

public enum LargeImage {
    Skin("skin"),
    Phobos("phobos"),
    Cats("cats");

    final String name;

    LargeImage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}

