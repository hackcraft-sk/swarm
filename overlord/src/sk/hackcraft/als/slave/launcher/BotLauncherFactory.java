package sk.hackcraft.als.slave.launcher;

import sk.hackcraft.als.utils.components.Component;
import sk.hackcraft.als.utils.model.BotInfo;

public interface BotLauncherFactory extends Component {

    BotLauncher create(BotInfo bot, byte[] botBlob);
}
