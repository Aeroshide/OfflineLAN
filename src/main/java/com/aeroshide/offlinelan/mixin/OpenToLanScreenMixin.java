package com.aeroshide.offlinelan.mixin;

import com.aeroshide.offlinelan.OfflineLAN;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.3 {
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.WorldOptionsScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} elif >=26.2 {
/*import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.MultiplayerOptionsScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?} elif >=1.19 {
/*import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.ShareToLanScreen;
*///?} elif >=1.17 {
/*import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.TranslatableComponent;
*///?} else {
/*import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.TranslatableComponent;
*///?}

//? if >=26.3 {
@Mixin(WorldOptionsScreen.class)
//?} elif >=26.2 {
/*@Mixin(MultiplayerOptionsScreen.class)
*///?} else {
/*@Mixin(ShareToLanScreen.class)
*///?}
public abstract class OpenToLanScreenMixin extends Screen {

    protected OpenToLanScreenMixin(Component title) {
        super(title);
    }

//? if >=26.3 {
    @Shadow protected abstract void updateApplyChangesActiveState();
    @Shadow private MinecraftServer.MultiplayerScope wantedMultiplayerScope;
    @Shadow private void sendPublishMessage(Component message) {}
    @Unique private boolean initialOnlineMode;
    @Unique private GridLayout.RowHelper capturedRowHelper;

    @ModifyVariable(method = "multiplayerOptions", at = @At("STORE"), ordinal = 0)
    private GridLayout.RowHelper captureRowHelper(GridLayout.RowHelper rowHelper) {
        this.capturedRowHelper = rowHelper;
        return rowHelper;
    }

    @Inject(method = "multiplayerOptions", at = @At("TAIL"))
    private void addOfflineModeToggle(LinearLayout content, IntegratedServer server, CallbackInfo ci) {
        this.initialOnlineMode = OfflineLAN.onlineModeOption.getOnlineMode();
        CycleButton<Boolean> toggleButton = CycleButton.onOffBuilder(OfflineLAN.onlineModeOption.getOnlineMode())
                .create(
                        Component.translatable("offlinelan.toggleText"),
                        (button, value) -> {
                            OfflineLAN.onlineModeOption.setOnlineMode(value);
                            this.updateApplyChangesActiveState();
                        }
                );
        if (this.capturedRowHelper != null) {
            this.capturedRowHelper.addChild(toggleButton);
            this.capturedRowHelper = null;
        } else {
            content.addChild(toggleButton);
        }
    }

    @Inject(method = "hasSettingsChanges", at = @At("RETURN"), cancellable = true)
    private void checkOfflineModeChanges(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (OfflineLAN.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "lanPortChanged", at = @At("RETURN"), cancellable = true)
    private void forceRepublishOnOfflineModeChange(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
            if (OfflineLAN.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "publish",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/WorldOptionsScreen;sendPublishMessage(Lnet/minecraft/network/chat/Component;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void appendOfflineLanMessage(IntegratedServer singleplayerServer, MinecraftServer.MultiplayerScope scope, CallbackInfo ci) {
        if (!OfflineLAN.onlineModeOption.getOnlineMode()) {
            this.sendPublishMessage(Component.translatable("offlinelan.chatMessage"));
        }
    }
//?} elif >=26.2 {
    /*@Shadow protected abstract void updateApplyChangesActiveState();
    @Shadow private MinecraftServer.MultiplayerScope wantedMultiplayerScope;
    @Shadow private void sendPublishMessage(Component message) {}
    @Unique private boolean initialOnlineMode;
    @Unique private LinearLayout capturedContentLayout;

    @ModifyVariable(method = "init", at = @At("STORE"), ordinal = 0)
    private LinearLayout captureContentLayout(LinearLayout content) {
        this.capturedContentLayout = content;
        return content;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;visitWidgets(Ljava/util/function/Consumer;)V"))
    private void addOfflineModeToggleToLayout(CallbackInfo ci) {
        this.initialOnlineMode = OfflineLAN.onlineModeOption.getOnlineMode();
        if (this.capturedContentLayout != null) {
            this.capturedContentLayout.addChild(
                    CycleButton.onOffBuilder(OfflineLAN.onlineModeOption.getOnlineMode())
                            .create(
                                    Component.translatable("offlinelan.toggleText"),
                                    (button, value) -> {
                                        OfflineLAN.onlineModeOption.setOnlineMode(value);
                                        this.updateApplyChangesActiveState();
                                    }
                            )
            );
        }
    }

    @Inject(method = "hasSettingsChanges", at = @At("RETURN"), cancellable = true)
    private void checkOfflineModeChanges(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (OfflineLAN.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "lanPortChanged", at = @At("RETURN"), cancellable = true)
    private void forceRepublishOnOfflineModeChange(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.wantedMultiplayerScope == MinecraftServer.MultiplayerScope.LAN) {
            if (OfflineLAN.onlineModeOption.getOnlineMode() != this.initialOnlineMode) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "publish",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/MultiplayerOptionsScreen;sendPublishMessage(Lnet/minecraft/network/chat/Component;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void appendOfflineLanMessage(IntegratedServer singleplayerServer, MinecraftServer.MultiplayerScope scope, CallbackInfo ci) {
        if (!OfflineLAN.onlineModeOption.getOnlineMode()) {
            this.sendPublishMessage(Component.translatable("offlinelan.chatMessage"));
        }
    }
*///?} elif >=1.19 {
    /*@Inject(method = "init", at = @At("TAIL"))
    private void addOfflineModeToggle(CallbackInfo ci) {
        this.addRenderableWidget(
                CycleButton.onOffBuilder(OfflineLAN.onlineModeOption.getOnlineMode())
                        .create(
                                this.width / 2 - 75,
                                190,
                                150,
                                20,
                                Component.translatable("offlinelan.toggleText"),
                                (button, value) -> OfflineLAN.onlineModeOption.setOnlineMode(value)
                        )
        );
    }
*///?} elif >=1.17 {
    /*@Inject(method = "init", at = @At("TAIL"))
    private void addOfflineModeToggle(CallbackInfo ci) {
        this.addRenderableWidget(
                CycleButton.onOffBuilder(OfflineLAN.onlineModeOption.getOnlineMode())
                        .create(
                                this.width / 2 - 75,
                                130,
                                150,
                                20,
                                new TranslatableComponent("offlinelan.toggleText"),
                                (button, value) -> OfflineLAN.onlineModeOption.setOnlineMode(value)
                        )
        );
    }
*///?} elif >=1.16 {
    /*@Inject(method = "init", at = @At("TAIL"))
    private void addOfflineModeToggle(CallbackInfo ci) {
        this.addButton(new Button(
                this.width / 2 - 75,
                130,
                150,
                20,
                getButtonMessage(),
                button -> {
                    OfflineLAN.onlineModeOption.setOnlineMode(!OfflineLAN.onlineModeOption.getOnlineMode());
                    button.setMessage(getButtonMessage());
                }
        ));
    }

    private Component getButtonMessage() {
        return new TranslatableComponent("offlinelan.toggleText")
                .append(": ")
                .append(new TranslatableComponent(OfflineLAN.onlineModeOption.getOnlineMode() ? "options.on" : "options.off"));
    }
*///?} else {
    /*@Inject(method = "init", at = @At("TAIL"))
    private void addOfflineModeToggle(CallbackInfo ci) {
        this.addButton(new Button(
                this.width / 2 - 75,
                130,
                150,
                20,
                getButtonMessage(),
                button -> {
                    OfflineLAN.onlineModeOption.setOnlineMode(!OfflineLAN.onlineModeOption.getOnlineMode());
                    button.setMessage(getButtonMessage());
                }
        ));
    }

    private String getButtonMessage() {
        return new TranslatableComponent("offlinelan.toggleText")
                .append(": ")
                .append(new TranslatableComponent(OfflineLAN.onlineModeOption.getOnlineMode() ? "options.on" : "options.off"))
                .getString();
    }
*///?}
}
