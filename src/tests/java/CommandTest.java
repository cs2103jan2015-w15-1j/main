package tests.java;

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
    
    //@author A0121813U
    @Test
    public void testIncompleteCommandType() {
        Command command = new Command("incomplete 1");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "INCOMPLETE", commandTypeString);
        
        command = new Command("incomplete 1231");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "INCOMPLETE", commandTypeString);
    }
    
    @Test
    public void testUndoCommandType() {
        Command command = new Command("undo");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "UNDO", commandTypeString);
        
        command = new Command("UndO");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "UNDO", commandTypeString);
    }
    
    @Test
    public void testSearchCommandType() {
        Command command = new Command("search blah blah blah");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "SEARCH", commandTypeString);
        
        command = new Command("Search 12345");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "SEARCH", commandTypeString);
    }
    
    @Test
    public void testHelpCommandType() {
        Command command = new Command("help");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "HELP", commandTypeString);
        
        command = new Command("HeLP");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "HELP", commandTypeString);
    }
    
    @Test
    public void testExitCommandType() {
        Command command = new Command("exit");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "EXIT", commandTypeString);
        
        command = new Command("eXIT");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "EXIT", commandTypeString);
    }
    
    @Test
    public void testClearCommandType() {
        Command command = new Command("clear");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "CLEAR", commandTypeString);
        
        command = new Command("CLEAr");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "CLEAR", commandTypeString);
    }

    @Test
    public void testSetCommandType() {
        Command command = new Command("set directory");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "SET", commandTypeString);
        
        command = new Command("SET directory");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "SET", commandTypeString);
    }

    @Test
    public void testMoveCommandType() {
        Command command = new Command("move here");
        String commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "MOVE", commandTypeString);
        
        command = new Command("Move there");
        commandTypeString = command.getCommandType().toString();
        assertEquals("Correct command type", "MOVE", commandTypeString);
    }
}
