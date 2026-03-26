package com.aerohide.offlinelan.mixins;

import com.aerohide.offlinelan.Offlinelan;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject( method = "publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", at = @At("HEAD"))
    private void onOpenToLan(GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        IntegratedServer integratedServer = (IntegratedServer) (Object) this;
        integratedServer.setUsesAuthentication(Offlinelan.onlineMode);
        // Offlinelan.LOG.info(integratedServer.isOnlineMode());
        if (!integratedServer.usesAuthentication())
            Offlinelan.LOG.info("The server will make no attempt to authenticate usernames. Beware.");
    }

}
