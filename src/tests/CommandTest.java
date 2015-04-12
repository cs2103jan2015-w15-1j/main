package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import main.java.Command;

import org.junit.Test;

//@author A0121520A
public class CommandTest {

    @Test
    public void testGetAllCommandTypes() {
       ArrayList<String> allCommands = Command.getAllCommandTypes();
       assertEquals("Correct num of commands", 14, allCommands.size());
    }

    @Test
    public void testAddCommandType() {
        Command command = new Command("add test input one");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "ADD", commandTypeString);
        
        command = new Command("adD test input one");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "ADD", commandTypeString);
    }
    
    @Test
    public void testDeleteCommandType() {
        Command command = new Command("delete 1");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "DELETE", commandTypeString);
        
        command = new Command("DElete 1");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "DELETE", commandTypeString);
    }
    
    @Test
    public void testEditCommandType() {
        Command command = new Command("edit 1");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "EDIT", commandTypeString);
        
        command = new Command("eDit 1");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "EDIT", commandTypeString);
    }
    
    @Test
    public void testDisplayCommandType() {
        Command command = new Command("display completed");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "DISPLAY", commandTypeString);
        
        command = new Command("DISPlay");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "DISPLAY", commandTypeString);
    }
    
    @Test
    public void testCompleteCommandType() {
        Command command = new Command("complete 3");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "COMPLETE", commandTypeString);
        
        command = new Command("Complete 4");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "COMPLETE", commandTypeString);
    }
    
    @Test
    public void testInvalidCommandType() {
        Command command = new Command("addd test input one");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "INVALID", commandTypeString);
        
        command = new Command("find");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "INVALID", commandTypeString);
    }

    @Test
    public void testGetArguments() {
        Command command = new Command("add test input one");
        String arguments = command.getArguments();
        assertEquals("Correct arguments", "test input one", arguments);
        
        command = new Command("add  ");
        arguments = command.getArguments();
        assertEquals("Correct arguments", "", arguments);
    }

}
