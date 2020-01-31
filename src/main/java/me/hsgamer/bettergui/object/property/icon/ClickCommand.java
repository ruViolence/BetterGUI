package me.hsgamer.bettergui.object.property.icon;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ClickCommand extends IconProperty<Object> {

  private List<Command> defaultCommands = new ArrayList<>();
  private Map<ClickType, List<Command>> commandsPerClickType = new EnumMap<>(ClickType.class);

  public ClickCommand(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    if (getValue() instanceof List) {
      defaultCommands.addAll(CommandBuilder.getCommands(getIcon(), (List<String>) getValue()));
    } else if (getValue() instanceof String) {
      defaultCommands.addAll(CommandBuilder.getCommands(getIcon(), (String) getValue()));
    } else if (getValue() instanceof ConfigurationSection) {
      ConfigurationSection section = (ConfigurationSection) getValue();
      for (ClickType clickType : ClickType.values()) {
        String subsection = clickType.name();
        if (section.isSet(subsection)) {
          List<Command> commands = new ArrayList<>();
          if (section.isList(subsection)) {
            commands
                .addAll(CommandBuilder.getCommands(getIcon(), section.getStringList(subsection)));
          } else {
            commands.addAll(CommandBuilder.getCommands(getIcon(), section.getString(subsection)));
          }
          commandsPerClickType.put(clickType, commands);
        }
      }
      if (section.isSet("DEFAULT")) {
        List<Command> commands = new ArrayList<>();
        if (section.isList("DEFAULT")) {
          commands.addAll(CommandBuilder.getCommands(getIcon(), section.getStringList("DEFAULT")));
        } else {
          commands.addAll(CommandBuilder.getCommands(getIcon(), section.getString("DEFAULT")));
        }
        defaultCommands.addAll(commands);
      }
    }
  }

  public <T> TaskChain<T> getTaskChain(Player player, ClickType clickType) {
    TaskChain<T> taskChain = BetterGUI.newChain();
    commandsPerClickType.getOrDefault(clickType, defaultCommands)
        .forEach(command -> command.addToTaskChain(player, taskChain));
    return taskChain;
  }
}
