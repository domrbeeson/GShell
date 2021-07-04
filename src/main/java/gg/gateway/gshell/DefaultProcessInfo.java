package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DefaultProcessInfo implements GProcessInfo {

    private final CommandSender executor;
    private final String[] args;
    private final Process process;
    private final HashSet<CommandSender> listeners = new HashSet<>();
    private final Date startTime = new Date();

    public DefaultProcessInfo(Process process, CommandSender executor, String...args) {
        this.process = process;
        this.executor = executor;
        this.args = args;
    }

    public CommandSender getExecutor() { return executor; }
    public String[] getArgs() { return args; }
    public Process getProcess() { return process; }
    public Set<CommandSender> getOutputListeners() { return listeners; }
    public Date getStartTime() { return startTime; }

    public boolean registerOutputListener(CommandSender listener) { return listeners.add(listener); }
    public boolean unregisterOutputListener(CommandSender listener) { return listeners.remove(listener); }

}
