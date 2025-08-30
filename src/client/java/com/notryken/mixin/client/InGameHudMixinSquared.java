package com.notryken.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

@Mixin(value = InGameHud.class, priority = 1100)
public class InGameHudMixinSquared {
    @Shadow
    @Final
    private static int WIDTH;

    @Shadow
    @Final
    private static int WARNING_OFFSET;

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/ArmorHudMod;shouldShowWarning(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean wrapShouldShowWarning(
            ItemStack stack,
            Operation<Boolean> original,
            @Share("showWarning") LocalBooleanRef showWarningRef
    ) {
        showWarningRef.set(original.call(stack));
        return !stack.isEmpty() && stack.isDamageable() && stack.isItemBarVisible();
    }

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lru/berdinskiybear/armorhud/config/ArmorHudConfig;getWarningBobIntensity()I"
            )
    )
    private int wrapGetWarningBobIntensity(
            ArmorHudConfig instance,
            Operation<Integer> original,
            @Share("showWarning") LocalBooleanRef showWarningRef
    ) {
        if (showWarningRef.get()) {
            return original.call(instance);
        }
        return 0;
    }

    @TargetHandler(
            mixin = "ru.berdinskiybear.armorhud.mixin.InGameHudMixin",
            name = "drawArmorHud"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"
            )
    )
    private void wrapDrawTexture(
            DrawContext instance,
            Identifier texture,
            int x,
            int y,
            int z,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            Operation<Void> original,
            @Local ItemStack stack
    ) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        String text = String.valueOf(stack.getMaxDamage() - stack.getDamage());
        int offsetX = 0;
        offsetX -= WARNING_OFFSET;
        offsetX += WIDTH / 2;
        offsetX -= font.getWidth(text) / 2;
        instance.drawText(
                font,
                text,
                x + offsetX,
                y - 1,
                stack.getItemBarColor(),
                true
        );
    }
}
