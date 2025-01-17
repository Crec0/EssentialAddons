package essentialaddons.commands;

import carpet.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import essentialaddons.EssentialAddonsSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Box;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandNear {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("near").requires((player) -> SettingsManager.canUseCommand(player, EssentialAddonsSettings.commandNear))
                .then(argument("distance", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayer();
                        int distance = context.getArgument("distance", Integer.class);
                        Box nearPlayer = new Box(serverPlayerEntity.getX() - distance,serverPlayerEntity.getY() - distance,serverPlayerEntity.getZ() - distance,serverPlayerEntity.getX() + distance,serverPlayerEntity.getY() + distance,serverPlayerEntity.getZ() + distance);
                        List<PlayerEntity> playerEntities = serverPlayerEntity.world.getEntitiesByType(EntityType.PLAYER, nearPlayer, ServerPlayerEntity -> true);
                        if (playerEntities.size() < 2) {
                            serverPlayerEntity.sendMessage(new LiteralText("§cThere are no players near you"), false);
                            return 0;
                        }
                        final String[] names = new String[playerEntities.size() - 1];
                        int i = 0;
                        for (PlayerEntity playerEntity : playerEntities) {
                            if (playerEntity.getEntityName().equals(serverPlayerEntity.getEntityName()))
                                continue;
                            names[i] = playerEntity.getEntityName();
                            i++;
                        }
                        String formattedNames = String.join(", ", names);
                        serverPlayerEntity.sendMessage(new LiteralText("§6Players near you: §a" + formattedNames), false);
                        return 0;
                    })
                )
        );
    }
}
