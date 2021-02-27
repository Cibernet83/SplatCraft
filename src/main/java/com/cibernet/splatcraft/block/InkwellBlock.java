package com.cibernet.splatcraft.block;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.block.entity.InkwellBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class InkwellBlock extends AbstractInkableBlock implements Waterloggable {
    public static final String id = "inkwell";

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
        Block.createCuboidShape(1.0D, 12.0D, 1.0D, 14.0D, 13.0D, 14.0D),
        Block.createCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    );

    public InkwellBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }

    /*@Override
    public float[] getBeaconColorMultiplier(BlockState state, WorldView world, BlockPos pos, BlockPos beaconPos) {
        return ColorUtils.hexToRGB(getColor((World) world, pos));
    } TODO */

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);

        if (entity instanceof ItemEntity) {
            AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(entity.getBlockPos().down());
            if (blockEntity != null) {
                ItemStack stack = ((ItemEntity) entity).getStack();
                Item stackItem = stack.getItem();
                if (stackItem instanceof BlockItem && ((BlockItem) stackItem).getBlock() instanceof InkedBlock && ColorUtils.getInkColor(stack) != blockEntity.getInkColor() && !ColorUtils.isColorLocked(stack)) {
                    ((ItemEntity) entity).setStack(ColorUtils.setInkColor(stack, blockEntity.getInkColor()));
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new InkwellBlockEntity();
    }

    @Override
    public boolean canClimb() {
        return false;
    }
    @Override
    public boolean canSwim() {
        return false;
    }
    @Override
    public boolean canDamage() {
        return true;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean countsTowardsTurf(World world, BlockPos pos) {
        return false;
    }
}