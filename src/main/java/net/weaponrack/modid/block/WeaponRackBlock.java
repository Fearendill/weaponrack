// Déclaration du package pour organiser le code
package net.weaponrack.modid.block;

// Import des classes nécessaires pour la sérialisation
import com.mojang.serialization.MapCodec;
// Import des classes de base pour les blocs Minecraft
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
// Import de l'entité de bloc personnalisée
import net.weaponrack.modid.block.entity.WeaponRackBlockEntity;
// Import pour les annotations nullables
import org.jetbrains.annotations.Nullable;

// Déclaration de la classe WeaponRackBlock qui étend BlockWithEntity
public class WeaponRackBlock extends BlockWithEntity {
    
    // Propriété pour la direction du bloc (nord, sud, est, ouest)
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    // Propriété booléenne pour indiquer si le rack est au mur
    public static final BooleanProperty ON_WALL = BooleanProperty.of("on_wall");
    // Codec pour la sérialisation du bloc
    public static final MapCodec<WeaponRackBlock> CODEC = createCodec(WeaponRackBlock::new);
    
    // Définition de la forme du rack au sol (mode vertical)
    private static final VoxelShape SHAPE_FLOOR = VoxelShapes.union(
        // Colonne basse
        Block.createCuboidShape(5.9, 2.93, 14.95, 10.1, 7.13, 16.55), // Base
        // Création du support bas
        Block.createCuboidShape(4, 1.5, 6.8, 12, 2.5, 9.2), // Support bas
        // Création du pied gauche
        Block.createCuboidShape(3.5, 2.5, 7, 4.8, 8.5, 9), // Pied gauche
        // Création du pied droit
        Block.createCuboidShape(11.2, 2.5, 7, 12.5, 8.5, 9), // Pied droit
        // Création du support haut
        Block.createCuboidShape(3, 8.5, 6, 13, 9.5, 10), // Support haut
        // Création des supports verticaux hauts gauche
        Block.createCuboidShape(3.5, 9.5, 6.5, 5, 11, 7.5), // Création des supports verticaux hauts droit
        Block.createCuboidShape(11, 9.5, 6.5, 12.5, 11, 7.5) // Supports verticaux hauts );
);

    
// Définition de la forme du rack au mur orienté nord (référence - côté sud du bloc, Z=16)
private static final VoxelShape SHAPE_WALL_NORTH = VoxelShapes.union(
    // Colonne principale basse
    Block.createCuboidShape(5.9, 2.93, 14.45, 10.1, 7.13, 16.0),
    // Colonne principale haute
    Block.createCuboidShape(5.9, 11.33, 14.45, 10.1, 13.43, 16.0),
    // Traverse centrale
    Block.createCuboidShape(3.8, 7.13, 14.45, 12.2, 11.33, 16.0),
    // Tige gauche
    Block.createCuboidShape(5.47, 6.71, 12.56, 6.52, 7.76, 14.45),
    // Tige droite
    Block.createCuboidShape(9.47, 6.71, 12.56, 10.52, 7.76, 14.45)
);

// Définition de la forme du rack au mur orienté sud (côté nord du bloc, Z=0)
private static final VoxelShape SHAPE_WALL_SOUTH = VoxelShapes.union(
    // Colonne principale basse
    Block.createCuboidShape(5.9, 2.93, 0.0, 10.1, 7.13, 1.55),
    // Colonne principale haute
    Block.createCuboidShape(5.9, 11.33, 0.0, 10.1, 13.43, 1.55),
    // Traverse centrale
    Block.createCuboidShape(3.8, 7.13, 0.0, 12.2, 11.33, 1.55),
    // Tige gauche
    Block.createCuboidShape(5.47, 6.71, 1.55, 6.52, 7.76, 3.44),
    // Tige droite
    Block.createCuboidShape(9.47, 6.71, 1.55, 10.52, 7.76, 3.44)
);

// Définition de la forme du rack au mur orienté ouest (côté est du bloc, X=16)
private static final VoxelShape SHAPE_WALL_WEST = VoxelShapes.union(
    // Colonne principale basse
    Block.createCuboidShape(14.45, 2.93, 5.9, 16.0, 7.13, 10.1),
    // Colonne principale haute
    Block.createCuboidShape(14.45, 11.33, 5.9, 16.0, 13.43, 10.1),
    // Traverse centrale
    Block.createCuboidShape(14.45, 7.13, 3.8, 16.0, 11.33, 12.2),
    // Tige gauche (en regardant depuis l'ouest)
    Block.createCuboidShape(12.56, 6.71, 9.48, 14.45, 7.76, 10.53),
    // Tige droite (en regardant depuis l'ouest)
    Block.createCuboidShape(12.56, 6.71, 5.48, 14.45, 7.76, 6.53)
);

// Définition de la forme du rack au mur orienté est (côté ouest du bloc, X=0)
private static final VoxelShape SHAPE_WALL_EAST = VoxelShapes.union(
    // Colonne principale basse
    Block.createCuboidShape(0.0, 2.93, 5.9, 1.55, 7.13, 10.1),
    // Colonne principale haute
    Block.createCuboidShape(0.0, 11.33, 5.9, 1.55, 13.43, 10.1),
    // Traverse centrale
    Block.createCuboidShape(0.0, 7.13, 3.8, 1.55, 11.33, 12.2),
    // Tige gauche (en regardant depuis l'est)
    Block.createCuboidShape(1.55, 6.71, 5.47, 3.44, 7.76, 6.52),
    // Tige droite (en regardant depuis l'est)
    Block.createCuboidShape(1.55, 6.71, 9.47, 3.44, 7.76, 10.52)
);
    
    // Constructeur de la classe WeaponRackBlock
    public WeaponRackBlock(Settings settings) {
        // Appel du constructeur de la classe parente
        super(settings);
        // Définition de l'état par défaut du bloc
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ON_WALL, false)
        );
    }

    // Méthode pour obtenir le codec de sérialisation
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        // Retourne le codec défini
        return CODEC;
    }

    // Méthode pour ajouter les propriétés d'état du bloc
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Ajout des propriétés FACING et ON_WALL
        builder.add(FACING, ON_WALL);
    }

// Annotation pour indiquer que la méthode peut retourner null
    @Nullable
    // Méthode pour déterminer l'état de placement du bloc
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Obtention de la face sur laquelle le joueur clique
        Direction side = ctx.getSide();

        // Vérification si le placement est mural (face horizontale)
        if (side.getAxis().isHorizontal()) {
            // Retourne l'état pour placement mural
            return getDefaultState()
                    // Définit que c'est au mur
                    .with(ON_WALL, true)
                    // Définit la direction vers la face cliquée
                    .with(FACING, side);
        }

        // Placement au sol
        return getDefaultState()
                // Définit que ce n'est pas au mur
                .with(ON_WALL, false)
                // Définit la direction opposée à celle du joueur
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // Méthode pour gérer la rotation du bloc
    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        // Retourne l'état avec la direction tournée
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    // Méthode pour gérer le miroir du bloc
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        // Retourne l'état avec la direction tournée selon le miroir
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // Méthode pour obtenir la forme de contour du bloc
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
            BlockPos pos, ShapeContext context) {
        // Vérification si le bloc est au mur
        if (state.get(ON_WALL)) {
            // Sélection de la forme selon la direction
            return switch (state.get(FACING)) {
                // Cas nord
                case NORTH -> SHAPE_WALL_NORTH;
                // Cas sud
                case SOUTH -> SHAPE_WALL_SOUTH;
                // Cas est
                case EAST -> SHAPE_WALL_EAST;
                // Cas ouest
                case WEST -> SHAPE_WALL_WEST;
                // Défaut nord
                default -> SHAPE_WALL_NORTH;
            };
        }
        // Retourne la forme au sol
        return SHAPE_FLOOR;
    }

    // Méthode pour obtenir la forme de collision du bloc
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Utilise la même forme que le contour
        return getOutlineShape(state, world, pos, context);
    }

    // Méthode pour vérifier si le bloc peut être placé à cette position
    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true; // Toujours vrai pour simplifier
    }

    // Méthode pour mettre à jour l'état du bloc en fonction des voisins
    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos) {
        return state;
    }

    // Annotation pour indiquer que la méthode peut retourner null
    @Nullable
    // Méthode pour créer l'entité de bloc
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Retourne une nouvelle entité WeaponRackBlockEntity
        return new WeaponRackBlockEntity(pos, state);
    }

    // Méthode pour obtenir le type de rendu du bloc
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        // Retourne le rendu modèle
        return BlockRenderType.MODEL;
    }

    // Méthode appelée lorsque le joueur utilise le bloc
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, 
                                  PlayerEntity player, BlockHitResult hit) {
        // Vérification si c'est côté client
        if (world.isClient) {
            // Retourne succès côté client
            return ActionResult.SUCCESS;
        }

        // Obtention de l'entité de bloc
        BlockEntity blockEntity = world.getBlockEntity(pos);
        // Vérification si c'est une WeaponRackBlockEntity
        if (!(blockEntity instanceof WeaponRackBlockEntity rack)) {
            // Passe si ce n'est pas le bon type
            return ActionResult.PASS;
        }

        // Obtention de l'item tenu par le joueur
        ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);
        // Obtention de l'item dans le rack
        ItemStack rackItem = rack.getWeaponItem();

        // Vérification si le rack contient un item
        if (!rackItem.isEmpty()) {
            // Vérification si la main du joueur est vide
            if (heldItem.isEmpty()) {
                // Place l'item du rack dans la main du joueur
                player.setStackInHand(Hand.MAIN_HAND, rackItem.copy());
                // Joue un son de ramassage
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 
                    0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                // Drop un seul item du rack dans le monde
                Block.dropStack(world, pos, rackItem.copy());
                rackItem.decrement(1);
            }
            // Vide le rack
            rack.setWeaponItem(ItemStack.EMPTY);
            rack.markDirty();
            // Retourne succès
            return ActionResult.CONSUME;
        }

        // Vérification si le rack peut accepter l'item tenu
        if (rack.canAcceptItem(heldItem)) {
            // Place l'item dans le rack
            rack.setWeaponItem(heldItem.copy());
            // Décrémente l'item dans la main si pas en créatif
            if (!player.isCreative()) {
                heldItem.decrement(1);
            }
            // Joue un son de placement
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 
                0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            // Retourne succès
            return ActionResult.SUCCESS;
        }

        // Retourne passe si rien n'a été fait
        return ActionResult.PASS;
    }

    // Méthode appelée lorsque le bloc est placé
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // Appel de la méthode parente
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    // Méthode appelée lorsque l'état du bloc est remplacé
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // Vérification si le bloc est remplacé par un autre bloc
        if (!state.isOf(newState.getBlock())) {
            // Obtention de l'entité de bloc
            BlockEntity blockEntity = world.getBlockEntity(pos);
            // Vérification si c'est une WeaponRackBlockEntity
            if (blockEntity instanceof WeaponRackBlockEntity rack) {
                // Obtention de l'item dans le rack
                ItemStack weaponItem = rack.getWeaponItem();
                // Vérification si l'item n'est pas vide
                if (!weaponItem.isEmpty()) {
                    // Drop l'item dans le monde
                    Block.dropStack(world, pos, weaponItem);
                }
            }
        }
        // Appel de la méthode parente
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    // Méthode pour obtenir l'item à ramasser
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        // Obtention de l'entité de bloc
        BlockEntity blockEntity = world.getBlockEntity(pos);
        // Vérification si c'est une WeaponRackBlockEntity
        if (blockEntity instanceof WeaponRackBlockEntity rack) {
            // Obtention de l'item dans le rack
            ItemStack weaponItem = rack.getWeaponItem();
            // Vérification si l'item n'est pas vide
            if (!weaponItem.isEmpty()) {
                // Retourne une copie de l'item
                return weaponItem.copy();
            }
        }
        // Appel de la méthode parente
        return super.getPickStack(world, pos, state);
    }
// Fermeture de la classe WeaponRackBlock
}