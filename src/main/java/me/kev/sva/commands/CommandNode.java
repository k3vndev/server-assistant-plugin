package me.kev.sva.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.utils.MessageSender;

/**
 * Base class for a command node.
 *
 * Each node represents either a command/subcommand or a command argument.
 * Nodes only operate on the arguments that remain after their own name.
 */
public abstract class CommandNode {

  protected final CommandSender sender;
  protected final ServerAssistantPlugin plugin;

  protected CommandNode(CommandSender sender, ServerAssistantPlugin plugin) {
    this.plugin = plugin;
    this.sender = sender;
  }

  /**
   * Gets this command's name.
   */
  public abstract String getName();

  /**
   * Gets the subcommands available under this command.
   */
  public List<CommandNode> getSubCommands() {
    return List.of();
  }

  /**
   * Gets the literal options available as arguments for this command.
   */
  public List<String> getOptions() {
    return List.of();
  }

  protected boolean executeSubcommands(List<String> args) {
    for (CommandNode command : getSubCommands()) {
      if (command.matches(args)) {
        args.removeFirst();
        command.execute(args);
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the first argument matches this command.
   */
  public boolean matches(List<String> args) {
    if (args.size() == 0)
      return false;

    String first = args.getFirst();
    return first.equalsIgnoreCase(getName());
  }

  /**
   * Executes this command using the remaining arguments.
   */
  public abstract boolean execute(List<String> args);

  /**
   * Provides tab completions for the remaining arguments.
   *
   * The last argument is treated as the current partial input.
   * If a matching subcommand exists, its completions are used instead.
   */
  public List<String> getTabCompletions(List<String> args) {
    if (args == null || args.isEmpty() || !hasPermission()) {
      return getCompletions("");
    }

    // There is more than one argument, so try to delegate
    // to the matching subcommand.
    if (args.size() > 1) {
      for (CommandNode subCommand : getSubCommands()) {
        if (subCommand.matches(args)) {
          return subCommand.getTabCompletions(
              args.subList(1, args.size()));
        }
      }

      return List.of();
    }

    // One argument remaining: complete it against this node's
    // subcommands and command options.
    return getCompletions(args.getFirst());
  }

  /**
   * Builds completions from subcommand names and command options.
   */
  private List<String> getCompletions(String partial) {
    List<String> completions = new ArrayList<>();

    for (CommandNode subCommand : getSubCommands()) {
      completions.add(subCommand.getName());
    }

    completions.addAll(getOptions());

    List<String> matches = new ArrayList<>();

    StringUtil.copyPartialMatches(
        partial,
        completions,
        matches);

    Collections.sort(matches);

    return matches;
  }

  /**
   * Checks if the sender has permission to execute this command.
   * Subclasses can override this for command-specific permissions.
   */
  protected boolean hasPermission() {
    return sender.hasPermission("sva.admin");
  }

  protected void throwMissingArgumentsError() {
    MessageSender.Error(sender, "Missing arguments for command: " + getName());
    printValidArgumentsList();
  }

  protected void throwInvalidArgumentError() {
    MessageSender.Error(sender, "Invalid argument for command: " + getName());
    printValidArgumentsList();
  }

  protected void throwExtraArgumentsError() {
    MessageSender.Error(sender, "Extra arguments provided for command: " + getName());
    printValidArgumentsList();
  }

  /**
   * Prints the valid subcommands and options for this command.
   */
  protected void printValidArgumentsList() {
    List<CommandNode> subCommands = getSubCommands();
    List<String> options = getOptions();

    if (subCommands.isEmpty() && options.isEmpty()) {
      MessageSender.Error(sender, "This command has no valid arguments.");
      return;
    }

    StringBuilder message = new StringBuilder();
    message.append("Valid arguments for ").append(getName()).append(": ");

    // Add subcommands
    if (!subCommands.isEmpty()) {
      List<String> subCommandNames = new ArrayList<>();
      for (CommandNode subCommand : subCommands) {
        subCommandNames.add(subCommand.getName());
      }
      message.append("[").append(String.join(" | ", subCommandNames)).append("]");
    }

    // Add options
    if (!options.isEmpty()) {
      if (!subCommands.isEmpty()) {
        message.append(" or ");
      }
      message.append("[").append(String.join(" | ", options)).append("]");
    }

    MessageSender.Error(sender, message.toString());
  }
}