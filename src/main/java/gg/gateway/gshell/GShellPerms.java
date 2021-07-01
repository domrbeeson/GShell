package gg.gateway.gshell;

public enum GShellPerms {

    RUN,
    QUIET,
    LIST,
    STOP,
    KILL,
    LISTEN;

    // convert enum to permission node
    @Override
    public String toString() {
        return "gshell.use." + this.name().toLowerCase();
    }

}
