package com.techelevator.view;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public void printUsers(User[] users) {
		System.out.println("--------------------------------------------");
		System.out.println("Users ID       Name");
		System.out.println("--------------------------------------------");
		for (User user : users) {
			System.out.println(user.getId() + "     :     " + user.getUsername());
		}
		System.out.println("--------------------------------------------" + "\n");
	}

	public void printTransfers(Transfer[] transfers) {
		System.out.println("--------------------------------------------");
		System.out.println("Transfers ID      From/To        Amount");
		System.out.println("--------------------------------------------");
		User user = null;
		for (Transfer transfer : transfers) {
		String displayTransfers = String.format("%5s",transfer.getTransferId()) + " From:"+ String.format("%10s",transfer.getAccountFromUsername()) +" $"+ String.format("%5s",transfer.getAmount());
			String displayFromTransfer =  String.format("%5s",transfer.getTransferId()) + " From:"+ String.format("%10s",transfer.getAccountToUsername()) +" $"+ String.format("%5s",transfer.getAmount());
			System.out.println(displayTransfers);
			System.out.println(displayFromTransfer);
			System.out.println("--------------------------------------------" + "\n");
		}

	}

	public void printTransferDetails(Transfer transfer) {
		System.out.println("--------------------------------------------");
		System.out.println("Transfer Details                            ");
		System.out.println("--------------------------------------------");

		System.out.println(transfer.toString());
	}

}
