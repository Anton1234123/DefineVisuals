package moscow.definevisuals.systems.modules.modules.player.hwhelper;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class TrapStructure {
    private final List<BlockDefinition> blocks = new ArrayList<>();

    public static class BlockDefinition {
        public final Block block;
        public final int x, y, z;

        public BlockDefinition(Block block, int x, int y, int z) {
            this.block = block;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public TrapStructure addBlock(Block block, int x, int y, int z) {
        blocks.add(new BlockDefinition(block, x, y, z));
        return this;
    }

    public List<BlockDefinition> getBlocks() {
        return blocks;
    }

    public static TrapStructure createExplosiveTrap() {
        TrapStructure structure = new TrapStructure();
        

        // Первый слой (y=-1) - относительно центра (0,0,0)
        // Центр (0,0,0) - блок спавна 88 -61 37
        structure.addBlock(Blocks.DIRT, 0, -1, 0); // 88 -61 37
        structure.addBlock(Blocks.DIRT, -1, -1, 0); // 87 -61 37
        structure.addBlock(Blocks.DIRT, -1, -1, 1); // 87 -61 38
        structure.addBlock(Blocks.STONE, -1, -1, -1); // 87 -61 36
        structure.addBlock(Blocks.COBBLESTONE, 0, -1, -1); // 88 -61 36
        structure.addBlock(Blocks.STONE, 1, -1, -1); // 89 -61 36
        structure.addBlock(Blocks.STONE, 0, -1, 1); // 88 -61 38
        structure.addBlock(Blocks.STONE, 1, -1, 1); // 89 -61 38
        structure.addBlock(Blocks.COBBLESTONE, -2, -1, -1); // 86 -61 36
        structure.addBlock(Blocks.STONE, -2, -1, 0); // 86 -61 37
        structure.addBlock(Blocks.COBBLESTONE, -2, -1, 1); // 86 -61 38
        structure.addBlock(Blocks.COBBLESTONE, -2, -1, 2); // 86 -61 39
        structure.addBlock(Blocks.COBBLESTONE, -1, -1, 2); // 87 -61 39
        structure.addBlock(Blocks.GRASS_BLOCK, -2, -1, 3); // 86 -61 40
        structure.addBlock(Blocks.GRASS_BLOCK, -3, -1, 2); // 85 -61 39
        
        // Второй слой (y=0)
        structure.addBlock(Blocks.COBBLESTONE, 1, 0, 0); // 89 -60 37
        structure.addBlock(Blocks.STONE, 2, 0, 0); // 90 -60 37
        structure.addBlock(Blocks.OBSIDIAN, 2, 0, 1); // 90 -60 38
        structure.addBlock(Blocks.OBSIDIAN, 2, 0, -1); // 90 -60 36
        structure.addBlock(Blocks.OBSIDIAN, 2, 0, -2); // 90 -60 35
        structure.addBlock(Blocks.STONE, 2, 0, -3); // 90 -60 34
        structure.addBlock(Blocks.COBBLESTONE, 3, 0, 0); // 91 -60 37
        structure.addBlock(Blocks.GRASS_BLOCK, 2, 0, 2); // 90 -60 39
        structure.addBlock(Blocks.STONE, 2, 0, 3); // 90 -60 40
        structure.addBlock(Blocks.GRASS_BLOCK, 1, 0, 2); // 89 -60 39
        structure.addBlock(Blocks.STONE, 1, 0, 3); // 89 -60 40
        structure.addBlock(Blocks.STONE, 1, 0, -2); // 89 -60 35
        structure.addBlock(Blocks.GRASS_BLOCK, 1, 0, -3); // 89 -60 34
        structure.addBlock(Blocks.STONE, 0, 0, -3); // 88 -60 34
        structure.addBlock(Blocks.OBSIDIAN, 0, 0, -2); // 88 -60 35
        structure.addBlock(Blocks.OBSIDIAN, 0, 0, 2); // 88 -60 39
        structure.addBlock(Blocks.STONE, 0, 0, 3); // 88 -60 40
        structure.addBlock(Blocks.GRASS_BLOCK, 0, 0, 4); // 88 -60 41
        structure.addBlock(Blocks.GRASS_BLOCK, -1, 0, 4); // 87 -60 41
        structure.addBlock(Blocks.GRASS_BLOCK, -1, 0, 3); // 87 -60 40
        structure.addBlock(Blocks.COBBLESTONE, -1, 0, -2); // 87 -60 35
        structure.addBlock(Blocks.GRASS_BLOCK, -1, 0, -3); // 87 -60 34
        structure.addBlock(Blocks.COBBLESTONE, -2, 0, -3); // 86 -60 34
        structure.addBlock(Blocks.GRASS_BLOCK, -2, 0, -2); // 86 -60 35
        structure.addBlock(Blocks.GRASS_BLOCK, -2, 0, 4); // 86 -60 41
        structure.addBlock(Blocks.STONE_BRICKS, -3, 0, 3); // 85 -60 40
        structure.addBlock(Blocks.COBWEB, -3, 0, 2); // 85 -60 39
        structure.addBlock(Blocks.STONE_BRICKS, -3, 0, 1); // 85 -60 38
        structure.addBlock(Blocks.STONE, -3, 0, 0); // 85 -60 37
        structure.addBlock(Blocks.STONE, -3, 0, -1); // 85 -60 36
        structure.addBlock(Blocks.STONE_BRICKS, -3, 0, -2); // 85 -60 35
        structure.addBlock(Blocks.COBBLESTONE, -3, 0, -3); // 85 -60 34
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 0, -2); // 84 -60 35
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 0, -1); // 84 -60 36
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 0, 0); // 84 -60 37
        
        // Третий слой (y=1)
        structure.addBlock(Blocks.DIRT, -4, 1, -3); // 84 -59 34
        structure.addBlock(Blocks.DIRT, -5, 1, -2); // 83 -59 35
        structure.addBlock(Blocks.DIRT, -5, 1, -1); // 83 -59 36
        structure.addBlock(Blocks.DIRT, -5, 1, 0); // 83 -59 37
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 1, 1); // 84 -59 38
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 1, 2); // 84 -59 39
        structure.addBlock(Blocks.OBSIDIAN, -4, 1, 3); // 84 -59 40
        structure.addBlock(Blocks.DIRT, -3, 1, 4); // 85 -59 41
        structure.addBlock(Blocks.STONE_BRICKS, -2, 1, 4); // 86 -59 41
        structure.addBlock(Blocks.DIRT, -1, 1, 5); // 87 -59 42
        structure.addBlock(Blocks.DIRT, 0, 1, 5); // 88 -59 42
        structure.addBlock(Blocks.GRASS_BLOCK, 1, 1, 4); // 89 -59 41
        structure.addBlock(Blocks.OBSIDIAN, 2, 1, 4); // 90 -59 41
        structure.addBlock(Blocks.STONE, 3, 1, 3); // 91 -59 40
        structure.addBlock(Blocks.DIRT, 3, 1, 2); // 91 -59 39
        structure.addBlock(Blocks.DIRT, 3, 1, 1); // 91 -59 38
        structure.addBlock(Blocks.COBBLESTONE_WALL, 3, 1, 0); // 91 -59 37
        structure.addBlock(Blocks.DIRT, 3, 1, -1); // 91 -59 36
        structure.addBlock(Blocks.DIRT, 3, 1, -2); // 91 -59 35
        structure.addBlock(Blocks.STONE_BRICKS, 3, 1, -3); // 91 -59 34
        structure.addBlock(Blocks.COBBLESTONE_WALL, 2, 1, -3); // 90 -59 34
        structure.addBlock(Blocks.STONE_BRICKS, 2, 1, -4); // 90 -59 33
        structure.addBlock(Blocks.DIRT, 1, 1, -4); // 89 -59 33
        structure.addBlock(Blocks.GRASS_BLOCK, 0, 1, -4); // 88 -59 33
        structure.addBlock(Blocks.DIRT, -1, 1, -4); // 87 -59 33
        structure.addBlock(Blocks.DIRT, -2, 1, -4); // 86 -59 33
        structure.addBlock(Blocks.DIRT, -3, 1, -4); // 85 -59 33
        
        // Четвертый слой (y=2)
        structure.addBlock(Blocks.STONE, -4, 2, -3); // 84 -58 34
        structure.addBlock(Blocks.DIRT, -5, 2, -2); // 83 -58 35
        structure.addBlock(Blocks.DIRT, -5, 2, -1); // 83 -58 36
        structure.addBlock(Blocks.DIRT, -5, 2, 0); // 83 -58 37
        structure.addBlock(Blocks.DIRT, -5, 2, 1); // 83 -58 38
        structure.addBlock(Blocks.COBBLESTONE_WALL, -4, 2, 1); // 84 -58 38
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 2, 2); // 84 -58 39
        structure.addBlock(Blocks.DIRT, -4, 2, 3); // 84 -58 40
        structure.addBlock(Blocks.DIRT, -3, 2, 4); // 85 -58 41
        structure.addBlock(Blocks.OBSIDIAN, -2, 2, 4); // 86 -58 41
        structure.addBlock(Blocks.DIRT, -1, 2, 5); // 87 -58 42
        structure.addBlock(Blocks.DIRT, 0, 2, 5); // 88 -58 42
        structure.addBlock(Blocks.DIRT, 1, 2, 5); // 89 -58 42
        structure.addBlock(Blocks.COBWEB, 1, 2, 4); // 89 -58 41
        structure.addBlock(Blocks.DIRT, 2, 2, 4); // 90 -58 41
        structure.addBlock(Blocks.COBBLESTONE_WALL, 3, 2, 3); // 91 -58 40
        structure.addBlock(Blocks.DIRT, 3, 2, 2); // 91 -58 39
        structure.addBlock(Blocks.GRASS_BLOCK, 3, 2, 1); // 91 -58 38
        structure.addBlock(Blocks.COBBLESTONE_WALL, 3, 2, 0); // 91 -58 37
        structure.addBlock(Blocks.GRASS_BLOCK, 3, 2, -1); // 91 -58 36
        structure.addBlock(Blocks.DIRT, 3, 2, -2); // 91 -58 35
        structure.addBlock(Blocks.STONE_BRICKS, 3, 2, -3); // 91 -58 34
        structure.addBlock(Blocks.COBBLESTONE_WALL, 2, 2, -3); // 90 -58 34
        structure.addBlock(Blocks.STONE_BRICKS, 2, 2, -4); // 90 -58 33
        structure.addBlock(Blocks.DIRT, 1, 2, -4); // 89 -58 33
        structure.addBlock(Blocks.COBWEB, 0, 2, -4); // 88 -58 33
        structure.addBlock(Blocks.DIRT, -1, 2, -4); // 87 -58 33
        structure.addBlock(Blocks.DIRT, -2, 2, -4); // 86 -58 33
        structure.addBlock(Blocks.DIRT, -3, 2, -4); // 85 -58 33
        
        // Пятый слой (y=3)
        structure.addBlock(Blocks.DIRT, -1, 3, -4); // 87 -57 33
        structure.addBlock(Blocks.STONE_BRICKS, -2, 3, -4); // 86 -57 33
        structure.addBlock(Blocks.DIRT, -3, 3, -4); // 85 -57 33
        structure.addBlock(Blocks.DIRT, -4, 3, -4); // 84 -57 33
        structure.addBlock(Blocks.DIRT, -5, 3, -3); // 83 -57 34
        structure.addBlock(Blocks.DIRT, -5, 3, -1); // 83 -57 36
        structure.addBlock(Blocks.DIRT, -5, 3, 0); // 83 -57 37
        structure.addBlock(Blocks.DIRT, -5, 3, 1); // 83 -57 38
        structure.addBlock(Blocks.COBWEB, -4, 3, 2); // 84 -57 39
        structure.addBlock(Blocks.DIRT, -5, 3, 2); // 83 -57 39
        structure.addBlock(Blocks.GRASS_BLOCK, -3, 3, 3); // 85 -57 40
        structure.addBlock(Blocks.DIRT, -2, 3, 4); // 86 -57 41
        structure.addBlock(Blocks.STONE_BRICKS, -1, 3, 5); // 87 -56 42
        structure.addBlock(Blocks.DIRT, -1, 3, 5); // 87 -57 42
        structure.addBlock(Blocks.STONE_BRICKS, 0, 3, 5); // 88 -57 42
        structure.addBlock(Blocks.STONE_BRICKS, 1, 3, 5); // 89 -57 42
        structure.addBlock(Blocks.DIRT, 2, 3, 4); // 90 -57 41
        structure.addBlock(Blocks.GRASS_BLOCK, 3, 3, 3); // 91 -57 40
        structure.addBlock(Blocks.GRASS_BLOCK, 3, 3, 2); // 91 -57 39
        structure.addBlock(Blocks.COBWEB, 3, 3, -1); // 91 -57 36
        structure.addBlock(Blocks.STONE_BRICKS, 3, 3, -2); // 91 -57 35
        structure.addBlock(Blocks.STONE_BRICKS, 3, 3, -3); // 91 -57 34
        structure.addBlock(Blocks.GRASS_BLOCK, 2, 3, -4); // 90 -57 33
        structure.addBlock(Blocks.DIRT, 1, 3, -4); // 89 -57 33
        structure.addBlock(Blocks.DIRT, 0, 3, -4); // 88 -57 33
        
        // Шестой слой (y=4)
        structure.addBlock(Blocks.GRASS_BLOCK, -3, 4, -4); // 85 -56 33
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 4, -4); // 84 -56 33
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, -3); // 83 -56 34
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, -2); // 83 -56 35
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, -1); // 83 -56 36
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, 0); // 83 -56 37
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, 1); // 83 -56 38
        structure.addBlock(Blocks.GRASS_BLOCK, -5, 4, 2); // 83 -56 39
        structure.addBlock(Blocks.GRASS_BLOCK, -4, 4, 3); // 84 -56 40
        structure.addBlock(Blocks.GRASS_BLOCK, -3, 4, 4); // 85 -56 41
        structure.addBlock(Blocks.GRASS_BLOCK, -2, 4, 4); // 86 -56 41
        structure.addBlock(Blocks.GRASS_BLOCK, 0, 4, 5); // 88 -56 42
        structure.addBlock(Blocks.GRASS_BLOCK, 1, 4, 5); // 89 -56 42
        structure.addBlock(Blocks.GRASS_BLOCK, 2, 4, 4); // 90 -56 41
        structure.addBlock(Blocks.GRASS_BLOCK, 1, 4, -4); // 89 -56 33
        structure.addBlock(Blocks.GRASS_BLOCK, 0, 4, -4); // 88 -56 33
        structure.addBlock(Blocks.GRASS_BLOCK, -1, 4, -4); // 87 -56 33
        
        return structure;
    }

    public static TrapStructure createNormalTrap() {
        TrapStructure structure = new TrapStructure();
        
        // Пол (y=0) - полный 5x5 с миксом эндерняка и эндер кирпичей
        structure.addBlock(Blocks.END_STONE, -2, 0, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 0, -2);
        structure.addBlock(Blocks.END_STONE, 0, 0, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 0, -2);
        structure.addBlock(Blocks.END_STONE, 2, 0, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 0, -1);
        structure.addBlock(Blocks.END_STONE, -1, 0, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 0, 0, -1);
        structure.addBlock(Blocks.END_STONE, 1, 0, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 0, -1);
        structure.addBlock(Blocks.END_STONE, -2, 0, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 0, 0);
        structure.addBlock(Blocks.END_STONE, 0, 0, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 0, 0);
        structure.addBlock(Blocks.END_STONE, 2, 0, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 0, 1);
        structure.addBlock(Blocks.END_STONE, -1, 0, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 0, 0, 1);
        structure.addBlock(Blocks.END_STONE, 1, 0, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 0, 1);
        structure.addBlock(Blocks.END_STONE, -2, 0, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 0, 2);
        structure.addBlock(Blocks.END_STONE, 0, 0, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 0, 2);
        structure.addBlock(Blocks.END_STONE, 2, 0, 2);
        
        // Стены y=1 - микс блоков (не трогаем)
        structure.addBlock(Blocks.END_STONE, -2, 1, -2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, -1, 1, -2);
        structure.addBlock(Blocks.END_STONE_BRICK_SLAB, 0, 1, -2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, 1, 1, -2);
        structure.addBlock(Blocks.END_STONE, 2, 1, -2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, -2, 1, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 1, 0);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, -2, 1, 1);
        structure.addBlock(Blocks.END_STONE, -2, 1, 2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, -1, 1, 2);
        structure.addBlock(Blocks.END_STONE_BRICK_SLAB, 0, 1, 2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, 1, 1, 2);
        structure.addBlock(Blocks.END_STONE, 2, 1, 2);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, 2, 1, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 1, 0);
        structure.addBlock(Blocks.END_STONE_BRICK_STAIRS, 2, 1, -1);
        
        // Стены y=2 - микс блоков (не трогаем)
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 2, -2);
        structure.addBlock(Blocks.END_STONE, -1, 2, -2);
        structure.addBlock(Blocks.END_STONE_BRICK_SLAB, 0, 2, -2);
        structure.addBlock(Blocks.END_STONE, 1, 2, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 2, -2);
        structure.addBlock(Blocks.END_STONE, -2, 2, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 2, 0);
        structure.addBlock(Blocks.END_STONE, -2, 2, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 2, 2);
        structure.addBlock(Blocks.END_STONE, -1, 2, 2);
        structure.addBlock(Blocks.END_STONE_BRICK_SLAB, 0, 2, 2);
        structure.addBlock(Blocks.END_STONE, 1, 2, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 2, 2);
        structure.addBlock(Blocks.END_STONE, 2, 2, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 2, 0);
        structure.addBlock(Blocks.END_STONE, 2, 2, -1);
        
        // Стены y=3 - микс блоков (новый слой стен)
        structure.addBlock(Blocks.END_STONE, -2, 3, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 3, -2);
        structure.addBlock(Blocks.END_STONE, 0, 3, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 3, -2);
        structure.addBlock(Blocks.END_STONE, 2, 3, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 3, -1);
        structure.addBlock(Blocks.END_STONE, -2, 3, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 3, 1);
        structure.addBlock(Blocks.END_STONE, -2, 3, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 3, 2);
        structure.addBlock(Blocks.END_STONE, 0, 3, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 3, 2);
        structure.addBlock(Blocks.END_STONE, 2, 3, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 3, 1);
        structure.addBlock(Blocks.END_STONE, 2, 3, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 3, -1);
        
        // Крыша (y=4) - полный 5x5 с миксом эндерняка и эндер кирпичей
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 4, -2);
        structure.addBlock(Blocks.END_STONE, -1, 4, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 0, 4, -2);
        structure.addBlock(Blocks.END_STONE, 1, 4, -2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 4, -2);
        structure.addBlock(Blocks.END_STONE, -2, 4, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 4, -1);
        structure.addBlock(Blocks.END_STONE, 0, 4, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 4, -1);
        structure.addBlock(Blocks.END_STONE, 2, 4, -1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 4, 0);
        structure.addBlock(Blocks.END_STONE, -1, 4, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, 0, 4, 0);
        structure.addBlock(Blocks.END_STONE, 1, 4, 0);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 4, 0);
        structure.addBlock(Blocks.END_STONE, -2, 4, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -1, 4, 1);
        structure.addBlock(Blocks.END_STONE, 0, 4, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, 1, 4, 1);
        structure.addBlock(Blocks.END_STONE, 2, 4, 1);
        structure.addBlock(Blocks.END_STONE_BRICKS, -2, 4, 2);
        structure.addBlock(Blocks.END_STONE, -1, 4, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 0, 4, 2);
        structure.addBlock(Blocks.END_STONE, 1, 4, 2);
        structure.addBlock(Blocks.END_STONE_BRICKS, 2, 4, 2);
        
        return structure;
    }
}
