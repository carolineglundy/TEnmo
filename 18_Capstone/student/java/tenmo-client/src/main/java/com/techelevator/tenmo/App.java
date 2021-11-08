package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TEBucksService;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
	private TEBucksService teBucksService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TEBucksService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TEBucksService teBucksService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.teBucksService = teBucksService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		System.out.println("Your balance is $"+teBucksService.getBalance(currentUser.getToken()));
		
	}

	private void viewTransferHistory() {
		Transfer[] transferList = teBucksService.viewApprovedTransfers((currentUser.getToken()));
		console.printTransfers(transferList);
		 int transferId = console.getUserInputNumber("Please enter transfer ID to view details (0 to cancel) ").intValue();
			if (transferId == 0 ) {
				mainMenu();
			} else{
				Transfer transfer = teBucksService.viewTransferById(currentUser.getToken(), transferId);
				if (transfer == null) {
					mainMenu();
				}
				console.printTransferDetails(transfer);

			}
	}

	private void viewPendingRequests() {
		Transfer[] transferArray = teBucksService.viewPendingTransfers((currentUser.getToken()));
		List<Transfer> transferList = new ArrayList<Transfer>(Arrays.asList(transferArray));
		for (Transfer transfer : transferList) {
			console.printTransfer(transfer);
			int choiceId = console.getUserInputNumber("1: Approve\n" +
					"2: Reject\n" +
					"0: Don't approve or reject\n" +
					"---------\n" +
					"Please choose an option: ").intValue();
			if (choiceId == 0) {
			}
			else if (choiceId < 0 || choiceId > 2) {
				System.out.println("\n"+"Please choose (0 1 OR 2): The choice you entered " + choiceId + " is not valid.");
				console.printTransferList(transferList);
				choiceId = console.getUserInputNumber("\n" +"1: Approve\n" +
						"2: Reject\n" +
						"0: Don't approve or reject\n" +
						"---------\n" +
						"Please choose an option: ").intValue();
			}
				else if (choiceId == 2) {
					//set status to rejected and no balances change
						teBucksService.updateTransfer(currentUser.getToken(), transfer, choiceId);
					transferList.remove(transfer);
					break;
				} else {
					// set status to approved and balances change
					teBucksService.updateTransfer(currentUser.getToken(), transfer, choiceId);
					transferList.remove(transfer);
					break;
				}
			}
	}

	private void sendBucks() {
		User[] userList = teBucksService.listUsers(currentUser.getToken());
		console.printUsers(userList);
		int userId = console.getUserInputNumber("Enter ID of user you are sending to (0 to cancel)").intValue();
		if (userId == 0) {
			mainMenu();
		}
		BigDecimal amount = BigDecimal.valueOf(console.getUserInputNumber("Enter amount"));
		Transfer newTransfer = new Transfer();
		newTransfer.setAccountTo(userId);
		newTransfer.setAmount(amount);
		Transfer transferEnteredByUser = teBucksService.sendTransfer(currentUser.getToken(), newTransfer);
	}

	private void requestBucks() {
		//System.out.println("No");
		User[] userList = teBucksService.listUsers(currentUser.getToken());
		console.printUsers(userList);
		int userId = console.getUserInputNumber("Enter ID of user you are sending to (0 to cancel)").intValue();
		if (userId == 0) {
			mainMenu();
		}
		BigDecimal amount = BigDecimal.valueOf(console.getUserInputNumber("Enter amount"));
		Transfer newTransfer = new Transfer();
		newTransfer.setAccountFrom(userId);

		newTransfer.setAmount(amount);
		Transfer transferEnteredByUser = teBucksService.requestTransfer(currentUser.getToken(), newTransfer);
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		//changed this because it was annoying if you accidentally clicked login and didn't register you had to shut to program down
		//while (currentUser == null) //will keep looping until user is logged in
		//{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		//}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	} 
}
