package com.aerohide.offlinelan.mixins;

import com.aerohide.offlinelan.Offlinelan;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject( method = "openToLan(Lnet/minecraft/world/GameMode;ZI)Z", at = @At("HEAD"))
    private void onOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setOnlineMode(Offlinelan.onlineMode);
        // Offlinelan.LOG.info(integratedServer.isOnlineMode());
        if (!integratedServer.isOnlineMode())
            Offlinelan.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }

}
