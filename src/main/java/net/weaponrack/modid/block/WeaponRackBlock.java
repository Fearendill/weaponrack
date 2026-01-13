package net.weaponrack.modid.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.weaponrack.modid.block.entity.WeaponRackBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WeaponRackBlock extends BlockWithEntity {
    
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<WeaponRackBlock> CODEC = createCodec(WeaponRackBlock::new);
    
    // Définir les formes pour chaque direction
    private static final VoxelShape SHAPE_NORTH_SOUTH = VoxelShapes.union(
    // Base du rack (partie inférieure)
    Block.createCuboidShape(3.8, 0, 12.56, 12.2, 2, 16.55),
    // Poteau vertical
    Block.createCuboidShape(5.9, 2, 14.45, 10.1, 14, 16.55),
    // Supports horizontaux
    Block.createCuboidShape(3.8, 7.13, 14.45, 12.2, 11.33, 16.55),
    // Bras latéraux (pour les épées)
    Block.createCuboidShape(3.59, 10.91, 14.87, 6.74, 13.01, 16.27),
    Block.createCuboidShape(9.26, 10.91, 14.87, 12.41, 13.01, 16.27)
);

private static final VoxelShape SHAPE_EAST_WEST = VoxelShapes.union(
    // Base du rack (partie inférieure)
    Block.createCuboidShape(12.56, 0, 3.8, 16.55, 2, 12.2),
    // Poteau vertical
    Block.createCuboidShape(14.45, 2, 5.9, 16.55, 14, 10.1),
    // Supports horizontaux
    Block.createCuboidShape(14.45, 7.13, 3.8, 16.55, 11.33, 12.2),
    // Bras latéraux (pour les épées)
    Block.createCuboidShape(14.87, 10.91, 3.59, 16.27, 13.01, 6.74),
    Block.createCuboidShape(14.87, 10.91, 9.26, 16.27, 13.01, 12.41)
);
    
    public WeaponRackBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // HITBOX PERSONNALISÉE
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        return (facing == Direction.NORTH || facing == Direction.SOUTH) ? SHAPE_NORTH_SOUTH : SHAPE_EAST_WEST;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WeaponRackBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, 
                                  PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof WeaponRackBlockEntity rack)) {
            return ActionResult.PASS;
        }

        ItemStack heldStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack storedStack = rack.getStack();

        // Place sword
        if (storedStack.isEmpty() && heldStack.getItem() instanceof SwordItem) {
            rack.setStack(heldStack.copyWithCount(1));
            if (!player.isCreative()) {
                heldStack.decrement(1);
            }
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        // Remove sword
        if (!storedStack.isEmpty() && heldStack.isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, storedStack.copy());
            rack.setStack(ItemStack.EMPTY);
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}