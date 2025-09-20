package net.bockie.luminesce.item.custom;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class GlimmetalIngotItem extends Item {
    public GlimmetalIngotItem(Settings settings) {
        super(settings);
    }
    public static final Supplier<BiMap<Object, Object>> LICHEN_TO_AIR = Suppliers.memoize(
            () -> ImmutableBiMap.builder()
                    .put(Blocks.GLOW_LICHEN, Blocks.AIR).buildOrThrow()
    );
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        if (!context.getWorld().isClient && player instanceof ServerPlayerEntity) {
        }
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        return (ActionResult) getLichenState(blockState).map(state -> {
            ItemStack itemStack = context.getStack();
            if (player instanceof ServerPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, blockPos, itemStack);
            }
            if (!world.isClient() && player instanceof ServerPlayerEntity) {
                givePlayerLivenedIngot((ServerPlayerEntity) player);
                //world.breakBlock(blockPos, false);
            }
            itemStack.decrement(1);
            world.setBlockState(blockPos, state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, blockState));
            world.syncWorldEvent(player, WorldEvents.PLANT_FERTILIZED, blockPos, 0);
            return ActionResult.success(world.isClient);
        }).orElse(ActionResult.PASS);
    }

    public static Optional<BlockState> getLichenState(BlockState state) {
        return Optional.ofNullable((Block) ((BiMap<?, ?>) LICHEN_TO_AIR.get()).get(state.getBlock())).map(block -> block.getStateWithProperties(state));
    }

    private void givePlayerLivenedIngot(ServerPlayerEntity player) {
        ItemStack livenedIngot = new ItemStack(Registries.ITEM.get(new Identifier("luminesce:livened_glimmetal_ingot")));
        boolean wasAdded = player.getInventory().insertStack(livenedIngot);
        if (!wasAdded) {
            // If the player's inventory is full, drop the item at the player's location
            player.dropItem(livenedIngot, false);
        }
    }
}
