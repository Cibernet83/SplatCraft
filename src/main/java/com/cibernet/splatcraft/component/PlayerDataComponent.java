package com.cibernet.splatcraft.component;

import com.cibernet.splatcraft.init.SplatcraftComponents;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

public class PlayerDataComponent implements Component, AutoSyncedComponent {
    private final Object provider;

    private boolean initialized = false;
    private boolean isSquid = false;
    private InkColor inkColor = InkColors.NONE;
    private int squidSubmergeMode = -2;

    public PlayerDataComponent(Object provider) {
        this.provider = provider;
    }

    @Environment(EnvType.SERVER)
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider;
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.checkForInitialization();

        tag.putBoolean("Initialized", this.initialized);
        tag.putBoolean("IsSquid", this.isSquid);
        tag.putString("InkColor", this.inkColor.toString());
        tag.putByte("SquidSubmergeMode", (byte) this.squidSubmergeMode);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.initialized = tag.getBoolean("Initialized");
        this.isSquid = tag.getBoolean("IsSquid");
        this.inkColor = InkColor.getFromId(tag.getString("InkColor"));
        this.squidSubmergeMode = tag.getByte("SquidSubmergeMode");

        this.checkForInitialization();
    }

    protected void checkForInitialization() {
        if (!this.initialized) {
            this.inkColor = ColorUtils.getRandomStarterColor(new Random());
            this.initialized = true;
        }
    }

    // getters/setters
    public boolean isSquid() {
        return this.isSquid;
    }
    public void setIsSquid(boolean isSquid) {
        this.isSquid = isSquid;
    }
    public static void toggleSquidForm(PlayerEntity player) {
        PlayerDataComponent data = SplatcraftComponents.PLAYER_DATA.get(player);
        data.setIsSquid(!data.isSquid());
    }

    public void setInkColor(InkColor inkColor) {
        this.inkColor = inkColor;
    }
    public InkColor getInkColor() {
        return this.inkColor;
    }

    public void setSquidSubmergeMode(int squidSubmergeMode) {
        this.squidSubmergeMode = squidSubmergeMode;
    }
    public int getSquidSubmergeMode() {
        return this.squidSubmergeMode;
    }
}