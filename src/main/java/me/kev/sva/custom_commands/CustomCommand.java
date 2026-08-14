package me.kev.sva.custom_commands;

import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * Abstract base class for handling individual commands.
 * Each command implementation defines its own name, matching logic, and
 * execution.
 */
public abstract class CustomCommand {
  protected final CommandSender sender;

  public CustomCommand(CommandSender sender) {
    this.sender = sender;
  }

  /**
   * Returns the name of this command (e.g., "reload", "playerchatmode").
   */
  public abstract String getName();

  /**
   * Checks if the given arguments match this command.
   * 
   * @param args the command arguments
   * @return true if this command should handle the arguments
   */
  public abstract boolean matches(String[] args);

  /**
   * Executes this command.
   * 
   * @param args the command arguments
   * @return true if the command was handled, false otherwise
   */
  public abstract boolean execute(String[] args);

  /**
   * Provides tab completions for this command.
   * 
   * @param args the partial command arguments
   * @return a list of completion suggestions
   */
  public abstract List<String> getTabCompletions(String[] args);

  /**
   * Checks if the sender has permission to execute this command.
   * Subclasses can override for command-specific permissions.
   */
  protected boolean hasPermission() {
    return sender.hasPermission("sva.admin");
  }
}
