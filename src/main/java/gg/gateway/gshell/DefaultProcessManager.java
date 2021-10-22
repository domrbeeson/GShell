package gg.gateway.gshell;

import gg.gateway.gshell.interfaces.GProcessInfo;
import gg.gateway.gshell.interfaces.GProcessManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		final Date startTime = new Date();
		try {
			process = builder.start();
		} catch (IOException e) {
			return -1;
		}

		// because java 8 could be used, check whether process.pid() exists
		// if it doesn't then just increment the program counter for a basic pid
		long pid = -1;
		try {
			pid = process.pid();
		} catch (Exception e) {}
		final long id = pid != -1 ? pid : programCounter++;

		GProcessInfo info = new DefaultProcessInfo(
				process,
				executor,
				args
		);
		processes.put(id, info);

		// handle process output in new thread
		scheduler.runTaskAsynchronously(plugin, () -> {

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			PrintWriter logWriter = null;

			String line;
			try {

				boolean isPlayer = executor instanceof Player;
				String executorId = isPlayer ? ((Player) executor).getUniqueId().toString() : "CONSOLE";
				String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
				String logName = executorId + " " + dateString;

				logWriter = new PrintWriter(new File(GShell.PROCESS_LOGS_FOLDER + "/" + logName + ".txt"));
				logWriter.println("Executor: " + executorId + (isPlayer ? " (" + ((Player) executor).getName() + ")" : ""));
				logWriter.println("Run date: " + dateString);
				logWriter.println("Command:  " + String.join(" ", args));
				logWriter.println("Output:");

				while ((line = reader.readLine()) != null) {

					final String outputLine = line;

					info.getOutputListeners().forEach(listener -> listener.sendMessage(outputLine));
					logWriter.println(line);

				}

				process.waitFor();

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {

				// process has exited, do cleanup
				if (logWriter != null) logWriter.close();

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
