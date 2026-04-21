package com.project3.Command;

public interface Command {
    void execute() throws Exception;
    void undo();
}
