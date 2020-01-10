package paulevs.betternether.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import paulevs.betternether.noise.WorleyNoiseOctaved3D;
import paulevs.betternether.structures.IStructure;
import paulevs.betternether.world.BNWorldGenerator;

public class NetherBiome
{
	private static final WorleyNoiseOctaved3D SCATTER = new WorleyNoiseOctaved3D(1337, 3);
	private static int structureID = 0;
	
	private ArrayList<StructureInfo> generatorsFloor = new ArrayList<StructureInfo>();
	private ArrayList<StructureInfo> generatorsWall = new ArrayList<StructureInfo>();
	private ArrayList<StructureInfo> generatorsCeil = new ArrayList<StructureInfo>();
	
	protected String name;
	protected NetherBiome edge;
	protected int edgeSize;
	protected List<NetherBiome> subbiomes;
	protected int sl;
	
	public NetherBiome(String name)
	{
		this.name = name;
		edge = this;
		edgeSize = 0;
		sl = 0;
		subbiomes = new ArrayList<NetherBiome>();
	}
	
	public void genSurfColumn(IWorld world, BlockPos pos, Random random) {}
	
	public void genFloorObjects(IWorld world, BlockPos pos, Random random)
	{
		for (StructureInfo info: generatorsFloor)
			if (info.canGenerate(random, pos))
				info.structure.generate(world, pos, random);
	}
	
	public void genWallObjects(IWorld world, BlockPos pos, Random random)
	{
		for (StructureInfo info: generatorsWall)
			if (info.canGenerate(random, pos))
				info.structure.generate(world, pos, random);
	}
	
	public void genCeilObjects(IWorld world, BlockPos pos, Random random)
	{
		for (StructureInfo info: generatorsCeil)
			if (info.canGenerate(random, pos))
				info.structure.generate(world, pos, random);
	}
	
	protected double getFeatureNoise(BlockPos pos, int id)
	{
		return SCATTER.GetValue(pos.getX() * 0.1, pos.getY() * 0.1 + id * 10, pos.getZ() * 0.1);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getRegistryName()
	{
		return name.toLowerCase().replace(' ', '_');
	}

	public NetherBiome getEdge()
	{
		return edge;
	}
	
	public void setEdge(NetherBiome edge)
	{
		this.edge = edge;
	}

	public int getEdgeSize()
	{
		return edgeSize;
	}
	
	public void setEdgeSize(int size)
	{
		edgeSize = size;
	}
	
	public void addSubBiome(NetherBiome biome)
	{
		subbiomes.add(biome);
		sl = subbiomes.size() << 3;
	}
	
	public NetherBiome getSubBiome(int x, int y, int z)
	{
		if (sl > 0)
		{
			int id = BNWorldGenerator.getSubBiome(x, y, z, sl);
			if (id < subbiomes.size())
				return subbiomes.get(id);
			else
				return this;
		}
		else
			return this;
	}
	
	protected void addStructure(IStructure structure, StructureType type, float density, boolean useNoise)
	{
		switch(type)
		{
		case CEIL:
			generatorsCeil.add(new StructureInfo(structure, density, useNoise));
			break;
		case FLOOR:
			generatorsFloor.add(new StructureInfo(structure, density, useNoise));
			break;
		case WALL:
			generatorsWall.add(new StructureInfo(structure, density, useNoise));
			break;
		default:
			break;
		}
		
	}
	
	protected enum StructureType
	{
		FLOOR,
		WALL,
		CEIL;
	}
	
	protected class StructureInfo
	{
		final IStructure structure;
		final float density;
		final boolean useNoise;
		final int id;
		
		StructureInfo(IStructure structure, float density, boolean useNoise)
		{
			this.structure = structure;
			this.density = density;
			this.useNoise = useNoise;
			id = structureID ++;
		}
		
		boolean canGenerate(Random random, BlockPos pos)
		{
			return (!useNoise || getFeatureNoise(pos, id) < 0.25) && random.nextFloat() < density;
		}
	}
}