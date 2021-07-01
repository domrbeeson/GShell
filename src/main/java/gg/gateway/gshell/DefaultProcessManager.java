package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import gg.gateway.gshell.interfaces.GProcessManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultProcessManager implements GProcessManager {

    private final ConcurrentHashMap<Long, GProcessInfo> processes = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    private long programCounter = 0;

    public DefaultProcessManager(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    public long startProcess(CommandSender executor, boolean autoListenToOutput, String...args) {

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            return -1;
        }

        // java 8 doesn't have process.pid() so just increment program counter instead
        // it's probably certain that nobody will reach the 64 bit limit
        programCounter++;
        long id = programCounter;

        GProcessInfo info = new DefaultProcessInfo(
                process,
                executor,
                args
        );
        processes.put(id, info);

        // handle process output in new thread
        scheduler.runTaskAsynchronously(plugin, () -> {

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            try {
                while ((line = reader.readLine()) != null) {

                    final String outputLine = line;
                    info.getOutputListeners().forEach(listener -> listener.sendMessage(outputLine));

                }

                process.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {

                // process has exited, do cleanup
                String message = GShellMessages.PROCESS_EXIT_MESSAGE(id, process.exitValue());

                info.registerOutputListener(info.getExecutor()); // make sure person that executed the command knows the process has ended, even if they ran it quietly
                info.getOutputListeners().forEach(listener -> listener.sendMessage(message));

                processes.remove(id);
            }

        });

        return id;
    }

    public boolean stopProcess(long id, boolean force) {
        if (!isIdRunning(id)) return true;

        Process process = processes.get(id).getProcess();
        if (!process.isAlive()) {
            processes.remove(id);
            return true;
        }

        if (force) process.destroyForcibly();
        else process.destroy();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        processes.remove(id);
        return true;
    }

    public GProcessInfo getProcessInfo(long id) {
        return processes.get(id);
    }

    public boolean isIdRunning(long id) {
        return processes.containsKey(id);
    }

    public Map<Long, GProcessInfo> listRunningProcesses() {
        return processes;
    }

}
