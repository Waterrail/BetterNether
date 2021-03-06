package paulevs.betternether.blocks;

import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registers.BlocksRegister;

public class BlockMold extends BlockBaseNotFull
{
	public BlockMold(MaterialColor color)
	{
		super(FabricBlockSettings.of(Material.PLANT)
				.materialColor(color)
				.sounds(BlockSoundGroup.CROP)
				.nonOpaque()
				.noCollision()
				.breakInstantly()
				.ticksRandomly()
				.build());
		this.setRenderLayer(BNRenderLayer.CUTOUT);
		this.setDropItself(false);
	}
	
	@Environment(EnvType.CLIENT)
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos)
	{
		return 1.0F;
	}
	
	@Override
	public Block.OffsetType getOffsetType()
	{
		return Block.OffsetType.XZ;
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock() == BlocksRegister.NETHER_MYCELIUM;
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos)
	{
		if (!canPlaceAt(state, world, pos))
			return Blocks.AIR.getDefaultState();
		else
			return state;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		super.scheduledTick(state, world, pos, random);
		if (random.nextInt(16) == 0)
		{
			int c = 0;
			c = world.getBlockState(pos.north()).getBlock() == this ? c++ : c;
			c = world.getBlockState(pos.south()).getBlock() == this ? c++ : c;
			c = world.getBlockState(pos.east()).getBlock() == this ? c++ : c;
			c = world.getBlockState(pos.west()).getBlock() == this ? c++ : c;
			if (c < 2)
			{
				BlockPos npos = new BlockPos(pos);
				switch (random.nextInt(4))
				{
				case 0:
					npos = npos.add(-1, 0, 0);
					break;
				case 1:
					npos = npos.add(1, 0, 0);
					break;
				case 2:
					npos = npos.add(0, 0, -1);
					break;
				default:
					npos = npos.add(0, 0, 1);
					break;
				}
				if (world.isAir(npos) && canPlaceAt(state, world, npos))
				{
					BlocksHelper.setWithoutUpdate(world, npos, getDefaultState());
				}
			}
		}
	}
}
