package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.blocks.CrateBlock;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;

import java.util.List;

public class CrateTileEntity extends InkColorTileEntity implements IInventory
{
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private float health;
    private float maxHealth;
    private boolean hasLoot;
    private ResourceLocation lootTable = CrateBlock.STORAGE_SUNKEN_CRATE;

    public CrateTileEntity()
    {
        super(SplatcraftTileEntitites.crateTileEntity);
    }

    public void ink(int color, float damage)
    {
        if (world != null && world.isRemote)
        {
            return;
        }

        setColor(color);
        health -= damage;
        if (health <= 0)
        {
            world.destroyBlock(getPos(), false);

            dropInventory();
        } else
        {
            world.setBlockState(pos, getBlockState().with(CrateBlock.STATE, getState()), 2);
        }

    }

    @Override
    public void onLoad()
    {
        super.onLoad();
    }

    public void dropInventory()
    {
        if (world != null && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
        {
            getDrops().forEach(stack -> Block.spawnAsEntity(world, pos, stack));
        }
    }

    public List<ItemStack> getDrops()
    {
        return hasLoot ? CrateBlock.generateLoot(world, getPos(), getBlockState(), 0f) : getInventory();
    }

    public ResourceLocation getLootTable()
    {
        return lootTable;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);

        health = nbt.getFloat("Health");
        maxHealth = nbt.getFloat("MaxHealth");
        ItemStackHelper.loadAllItems(nbt, inventory);

        if (state.getBlock() instanceof CrateBlock)
        {
            hasLoot = ((CrateBlock) state.getBlock()).hasLoot;
            if(nbt.contains("LootTable"))
                lootTable = new ResourceLocation(nbt.getString("LootTable"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putFloat("Health", health);
        nbt.putFloat("MaxHealth", maxHealth);
        ItemStackHelper.saveAllItems(nbt, inventory);

        if(hasLoot)
            nbt.putString("LootTable", lootTable.toString());

        return super.write(nbt);
    }

    @Override
    public int getSizeInventory()
    {
        return getBlockState().getBlock() instanceof CrateBlock && ((CrateBlock) getBlockState().getBlock()).hasLoot ? 0 : 1;
    }

    @Override
    public boolean isEmpty()
    {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (getBlockState().getBlock() instanceof CrateBlock && ((CrateBlock) getBlockState().getBlock()).hasLoot)
        {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!itemstack.isEmpty())
        {
            this.markDirty();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return false;
    }

    @Override
    public void clear()
    {
        inventory.clear();
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(float value)
    {
        health = value;
    }

    public void resetHealth()
    {
        setHealth(maxHealth);
        setColor(-1);
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public void setMaxHealth(float value)
    {
        maxHealth = value;
    }

    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    public int getState()
    {
        if (health == maxHealth)
        {
            setColor(-1);
        }
        return 4 - Math.round(health * 4 / maxHealth);
    }

    public void setHasLoot(boolean hasLoot)
    {
        this.hasLoot = hasLoot;
    }
}
