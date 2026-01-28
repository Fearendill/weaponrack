package net.weaponrack.modid.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.weaponrack.modid.register.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class WeaponRackBlockEntity extends BlockEntity {
    private ItemStack weaponItem = ItemStack.EMPTY;

    public WeaponRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEAPON_RACK, pos, state);
    }

    public void setWeaponItem(ItemStack stack) {
    this.weaponItem = stack;
    
    if (world instanceof ServerWorld serverWorld) {
        serverWorld.getChunkManager().markForUpdate(pos);
        world.updateListeners(pos, getCachedState(), getCachedState(), 2);
    }
}

    public ItemStack getWeaponItem() {
        return weaponItem == null ? ItemStack.EMPTY : weaponItem;
    }

    public boolean canAcceptItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!weaponItem.isEmpty()) {
            nbt.put("weapon", weaponItem.encode(registryLookup));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("weapon")) {
            weaponItem = ItemStack.fromNbt(registryLookup, nbt.getCompound("weapon")).orElse(ItemStack.EMPTY);
        } else {
            weaponItem = ItemStack.EMPTY;
        }
    }

    @Nullable
    @Override
        public Packet<ClientPlayPacketListener> toUpdatePacket() {
            return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

}
