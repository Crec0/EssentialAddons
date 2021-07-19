package essentialaddons.mixins;

import essentialaddons.EssentialAddonsSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), require = 0)
    private void onDecrement(ItemStack itemStack, int amount, World world) {
        if (EssentialAddonsSettings.infiniteItems) {
            if (!world.isClient) {
                ServerPlayerEntity playerEntity = (ServerPlayerEntity) (Object) this;
                int slot = playerEntity.inventory.selectedSlot + 36;
                playerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(0, slot, itemStack));
            }
        }
        else
            itemStack.decrement(1);
    }
}
