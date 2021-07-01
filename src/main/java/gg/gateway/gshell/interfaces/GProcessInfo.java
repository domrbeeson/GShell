package gg.gateway.gshell.interfaces;

import org.bukkit.command.CommandSender;

import java.util.Set;

public interface GProcessInfo {

    public Process getProcess();
    public String[] getArgs();
    public CommandSender getExecutor();
    public Set<CommandSender> getOutputListeners();

    public boolean registerOutputListener(CommandSender listener);
    public boolean unregisterOutputListener(CommandSender listener);
}
