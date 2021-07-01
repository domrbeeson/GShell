package gg.gateway.gshell.interfaces;

import org.bukkit.command.CommandSender;

import java.util.Map;

public interface GProcessManager {

    long startProcess(CommandSender executor, boolean autoListenToOutput, String...args);
    boolean stopProcess(long id, boolean force);
    GProcessInfo getProcessInfo(long id);
    boolean isIdRunning(long id);
    Map<Long, GProcessInfo> listRunningProcesses();

}
