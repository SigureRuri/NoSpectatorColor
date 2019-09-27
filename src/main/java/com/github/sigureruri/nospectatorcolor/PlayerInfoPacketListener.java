package com.github.sigureruri.nospectatorcolor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ListIterator;

public class PlayerInfoPacketListener extends PacketAdapter {


    public PlayerInfoPacketListener(NoSpectatorColor plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        // パケットをコピーし、新たなパケットを作成する。
        // ディープコピーだとStackOverFlowErrorが出るのでShallowCloneを使用。
        PacketContainer packet = event.getPacket().shallowClone();
        PlayerInfoAction action = packet.getPlayerInfoAction().read(0);

        if (action == PlayerInfoAction.UPDATE_GAME_MODE || action == PlayerInfoAction.ADD_PLAYER) {

            List<PlayerInfoData> infoDataList = packet.getPlayerInfoDataLists().read(0);
            ListIterator<PlayerInfoData> infoDataIterator = infoDataList.listIterator();
            Player player = event.getPlayer();

            // 全員に送信されたパケットをイテレータで回す。
            while (infoDataIterator.hasNext()) {
                PlayerInfoData infoData = infoDataIterator.next();
                WrappedGameProfile profile = infoData.getProfile();
                NativeGameMode gameMode = infoData.getGameMode();

                // ゲームモードを変更したプレイヤーが、パーミッションを所持していなければ実行
                Player executedPlayer = Bukkit.getPlayer(profile.getName());
                if (executedPlayer != null && !executedPlayer.hasPermission("nospectatorcolor.invalid")) {
                    // プレイヤーがcheck権限を所持していなければ続く
                    if (!player.hasPermission("nospectatorcolor.check")) {

                        // ゲームモードを変更した本人のパケットは変更しない。
                        // 本人も変更すると中途半端なスペクテイターモードのようになる。
                        if (gameMode == NativeGameMode.SPECTATOR && !profile.getName().equals(player.getName())) {
                            PlayerInfoData newInfoData = new PlayerInfoData(
                                    profile,
                                    infoData.getLatency(),
                                    NativeGameMode.SURVIVAL,
                                    infoData.getDisplayName()
                            );
                            infoDataIterator.set(newInfoData);
                        }
                    }
                }
            }

            packet.getPlayerInfoDataLists().write(0, infoDataList);
            event.setPacket(packet);

        }
    }
}
