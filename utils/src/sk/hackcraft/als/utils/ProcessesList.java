package sk.hackcraft.als.utils;

public interface ProcessesList {

    boolean has(String processName);

    boolean has(int pid);

    int getPid(String processName);
}
