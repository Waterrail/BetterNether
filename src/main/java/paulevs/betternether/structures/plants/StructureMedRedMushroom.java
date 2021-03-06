package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IWorld;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockRedLargeMushroom;
import paulevs.betternether.blocks.shapes.TripleShape;
import paulevs.betternether.registers.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureMedRedMushroom implements IStructure
{
	Mutable npos = new Mutable();
	
	@Override
	public void generate(IWorld world, BlockPos pos, Random random)
	{
		Block under;
		if (world.getBlockState(pos.down()).getBlock() == BlocksRegister.NETHER_MYCELIUM)
		{
			for (int i = 0; i < 10; i++)
			{
				int x = pos.getX() + (int) (random.nextGaussian() * 2);
				int z = pos.getZ() + (int) (random.nextGaussian() * 2);
				int y = pos.getY() + random.nextInt(6);
				for (int j = 0; j < 6; j++)
				{
					npos.set(x, y - j, z);
					if (npos.getY() > 31)
					{
						under = world.getBlockState(npos.down()).getBlock();
						if (under == BlocksRegister.NETHER_MYCELIUM)
						{
							grow(world, npos, random);
						}
					}
					else
						break;
				}
			}
		}
	}

	public void grow(IWorld world, BlockPos pos, Random random)
	{
		int size = 1 + random.nextInt(4);
		for (int y = 1; y <= size; y++)
			if (!world.isAir(pos.up(y)))
			{
				if (y == 1)
					return;
				size = y - 1;
				break;
			}
		BlockState middle = BlocksRegister.RED_LARGE_MUSHROOM.getDefaultState().with(BlockRedLargeMushroom.SHAPE, TripleShape.MIDDLE);
		for (int y = 1; y < size; y++)
			BlocksHelper.setWithoutUpdate(world, pos.up(y), middle);
		BlocksHelper.setWithoutUpdate(world, pos.up(size), BlocksRegister.RED_LARGE_MUSHROOM.getDefaultState().with(BlockRedLargeMushroom.SHAPE, TripleShape.TOP));
		BlocksHelper.setWithoutUpdate(world, pos, BlocksRegister.RED_LARGE_MUSHROOM.getDefaultState().with(BlockRedLargeMushroom.SHAPE, TripleShape.BOTTOM));
	}
}
