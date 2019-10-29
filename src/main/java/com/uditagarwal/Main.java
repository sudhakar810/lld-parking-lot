package com.uditagarwal;

import com.uditagarwal.commands.CommandExecutor;
import com.uditagarwal.commands.CommandExecutorFactory;
import com.uditagarwal.commands.ExitCommandExecutor;
import com.uditagarwal.exception.InvalidCommandException;
import com.uditagarwal.exception.InvalidModeException;
import com.uditagarwal.model.Command;
import com.uditagarwal.service.ParkingLotService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {
    final OutputPrinter outputPrinter = new OutputPrinter();
    final ParkingLotService parkingLotService = new ParkingLotService();
    final CommandExecutorFactory commandExecutorFactory =
        new CommandExecutorFactory(parkingLotService);
    if (isInteractiveMode(args)) {
      runInteractiveMode(outputPrinter, commandExecutorFactory);
    } else if (isFileInputMode(args)) {
      runInputFileMode(args[0], outputPrinter, commandExecutorFactory);
    } else {
      throw new InvalidModeException();
    }
  }

  private static void runInteractiveMode(
      OutputPrinter outputPrinter, CommandExecutorFactory commandExecutorFactory)
      throws IOException {
    outputPrinter.welcome();
    while (true) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      final String input = reader.readLine();
      final Command command = new Command(input);
      processCommand(commandExecutorFactory, command);
      if (command.getCommandName().equals(ExitCommandExecutor.COMMAND_NAME)) {
        break;
      }
    }
  }

  private static void runInputFileMode(
      String arg, OutputPrinter outputPrinter, CommandExecutorFactory commandExecutorFactory)
      throws IOException {
    final String fileName = arg;
    final File file = new File(fileName);
    final BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      outputPrinter.invalidFile();
      return;
    }

    String input = reader.readLine();
    while (input != null) {
      final Command command = new Command(input);
      processCommand(commandExecutorFactory, command);
    }
  }

  private static void processCommand(
      CommandExecutorFactory commandExecutorFactory, Command command) {
    final CommandExecutor commandExecutor = commandExecutorFactory.getCommandExecutor(command);
    if (commandExecutor.validate(command)) {
      commandExecutor.execute(command);
    } else {
      throw new InvalidCommandException();
    }
  }

  private static boolean isFileInputMode(String[] args) {
    return args.length == 1;
  }

  private static boolean isInteractiveMode(String[] args) {
    return args.length == 0;
  }
}
