package paulevs.betternether.blocks;

import java.util.EnumMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import paulevs.betternether.blockentities.BlockEntityChestOfDrawers;
import paulevs.betternether.registers.BlocksRegister;

public class BlockChestOfDrawers extends BlockWithEntity
{
	private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.createCuboidShape(0, 0, 8, 16, 16, 16),
			Direction.SOUTH, Block.createCuboidShape(0, 0, 0, 16, 16, 8),
			Direction.WEST, Block.createCuboidShape(8, 0, 0, 16, 16, 16),
			Direction.EAST, Block.createCuboidShape(0, 0, 0, 8, 16, 16)));
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty OPEN = BooleanProperty.of("open");

	public BlockChestOfDrawers()
	{
		super(FabricBlockSettings.copy(BlocksRegister.CINCINNASITE).nonOpaque().build());
		this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager)
	{
		stateManager.add(FACING, OPEN);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos)
	{
		return BOUNDING_SHAPES.get(state.get(FACING));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		return new BlockEntityChestOfDrawers();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
	{
		if (itemStack.hasCustomName())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BlockEntityChestOfDrawers)
			{
				((BlockEntityChestOfDrawers) blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient)
		{
			return ActionResult.SUCCESS;
		}
		else
		{
			NameableContainerProvider nameableContainerProvider = this.createContainerProvider(state, world, pos);
			if (nameableContainerProvider != null)
			{
				player.openContainer(nameableContainerProvider);
			}
			return ActionResult.SUCCESS;
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}
}