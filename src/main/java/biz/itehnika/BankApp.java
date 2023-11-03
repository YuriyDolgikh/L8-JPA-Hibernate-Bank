package biz.itehnika;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;


public class BankApp {

    static EntityManagerFactory emf;
    static EntityManager em;
    static Utilities utilities;

    public static void main(String[] args) {
        try{
            emf = Persistence.createEntityManagerFactory("Bank");
            em = emf.createEntityManager();
            Scanner scanner = new Scanner(System.in);
            utilities = new Utilities(em, scanner);

            while (true){
                System.out.println("----------------------------------------------------------");
                System.out.println("0  => Add random clients and accounts.");
                System.out.println("1  => Add new client.");
                System.out.println("2  => Delete client.");
                System.out.println("3  => Add new account for client.");
                System.out.println("4  => Delete account from client.");
                System.out.println("5  => Show all clients.");
                System.out.println("6  => Show client by phone number.");
                System.out.println("7  => Show all accounts.");
                System.out.println("8  => Show accounts by client.");
                System.out.println("9  => Show all transactions.");
                System.out.println("10 => Deposit found into client's account.");
                System.out.println("11 => Transfer funds between different clients' accounts.");
                System.out.println("12 => Currency exchange within one client's accounts.");
                System.out.println("13 => Show the total in UAH of all accounts of one client.");
                System.out.println("14 => Update exchange rates in DB.");
                System.out.println("15 => Show currency exchange rates.");
                System.out.println("----------------------------------------------------------");
                System.out.print("Input the number of operation or click [Enter] for exit =>");

                String caseString = scanner.nextLine();
                switch (caseString){
                    case "" -> {return;}
                    case "0" -> addRandomClientsWithAccounts(scanner);
                    case "1" -> utilities.createNewClient();
                    case "2" -> deleteClient(scanner);
                    case "3" -> addAccountForClient(scanner);
                    case "4" -> delAccountFromClient(scanner);
                    case "5" -> showAllClients();
                    case "6" -> showClientByPhoneNumber(scanner);
                    case "7" -> showAllAccounts();
                    case "8" -> showAccountsByClientId(scanner);
                    case "9" -> showAllTransaction();
                    case "10"-> depositIntoAccount(scanner);
                    case "11"-> transferFunds(scanner);
                    case "12"-> exchangeCurrency(scanner);
                    case "13"-> showTotalInUAH(scanner);
                    case "14"-> utilities.updateCurrencyRateInDB();
                    case "15"-> showCurrencyRates();
                    default -> {break;}
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }
    }

    public static void addRandomClientsWithAccounts(Scanner scanner){
        System.out.print("How many clients do you want to generate?");
        int clientsNum = Integer.parseInt(scanner.nextLine());
        utilities.createRandomClientsList(clientsNum);
        System.out.println(clientsNum + " Clients with accounts added.");
    }

    public static void deleteClient(Scanner scanner){
        String caseNumStr;
        while (true){
            System.out.print("Do You want to delete the client by Id [1] or phone number [2]?");
            caseNumStr = scanner.nextLine();
            if (caseNumStr.equals("1")){
                System.out.print("Input the client ID:");
                Long clientId = Long.parseLong(scanner.nextLine());
                utilities.deleteClientById(clientId);
                break;
            }else if (caseNumStr.equals("2")){
                System.out.print("Input the phone number:");
                String phoneNumber = scanner.nextLine();
                utilities.deleteClientByPhoneNumber(phoneNumber);
                break;
            }
        }
    }

    public static void addAccountForClient(Scanner scanner){
        System.out.print("Input client Id You want to create new account:");
        Long clientId = Long.parseLong(scanner.nextLine());
        if (utilities.isClientExistById(clientId)){
            utilities.createAccountForClient(clientId);
        }else {
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }
    }

    public static void delAccountFromClient(Scanner scanner){
        System.out.print("Input client Id You want to delete an account:");
        Long clientId = Long.parseLong(scanner.nextLine());
        if (utilities.isClientExistById(clientId)){
            utilities.deleteAccountFromClient(clientId);
        }else {
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }
    }

    public static void showAllClients(){
        if (utilities.getAllClients() != null){
            for (Client client : utilities.getAllClients()){
                System.out.println(client);
            }
        }
    }

    public static void showClientByPhoneNumber(Scanner scanner){
        System.out.print("Input client's phone number:");
        String phoneNumber = scanner.nextLine();
        utilities.getClientByPhoneNumber(phoneNumber);
    }

    public static void showAllAccounts(){
        for (Account account : utilities.getAllAccounts()){
            System.out.println(account);
        }
    }

    public static void showAccountsByClientId(Scanner scanner){
        System.out.print("Input client Id for which You want to show accounts:");
        Long clientId = Long.parseLong(scanner.nextLine());
        if (!utilities.isClientExistById(clientId)){
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }else {
            List<Account> accounts = utilities.getAccountsByClientId(clientId);
            if (accounts != null || !accounts.isEmpty()){
                System.out.println("The client with Id = " + clientId + " has accounts:");
                for (Account account : utilities.getAccountsByClientId(clientId)){
                    System.out.println(account);
                }
            }else {
                System.out.println("Client with 'Id' =  " + clientId + " has no accounts!");
            }
        }
    }

    public static void showAllTransaction(){
        if (utilities.getAllTransaction() != null){
            for (Transaction transaction : utilities.getAllTransaction()){
                System.out.println(transaction);
            }
        }
    }

    public static void depositIntoAccount(Scanner scanner){
        System.out.print("Input client Id for deposit operation:");
        Long clientId = Long.parseLong(scanner.nextLine());
        utilities.depositIntoAccount(clientId);
    }

    public static void transferFunds(Scanner scanner){
        System.out.print("Input sender client Id:");
        Long senderClientId = Long.parseLong(scanner.nextLine());
        System.out.print("Input receiver client Id:");
        Long receiverClientId = Long.parseLong(scanner.nextLine());
        if (!utilities.isClientExistById(senderClientId)){
            System.out.println("Client sender with 'Id' =  " + senderClientId + " is not exist!");
        }else if (!utilities.isClientExistById(receiverClientId)){
            System.out.println("Client receiver with 'Id' =  " + receiverClientId + " is not exist!");
        }else {
            utilities.transferMoney(senderClientId, receiverClientId);
        }
    }

    public  static void exchangeCurrency(Scanner scanner){
        System.out.print("Input client Id for exchange operation:");
        Long clientId = Long.parseLong(scanner.nextLine());
        if (!utilities.isClientExistById(clientId)){
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }else {
            utilities.exchangeCurrency(clientId);
        }
    }

    public static void showTotalInUAH(Scanner scanner){
        System.out.print("Input client Id for getting the total in UAH from all accounts:");
        Long clientId = Long.parseLong(scanner.nextLine());
        if (!utilities.isClientExistById(clientId)){
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }else {
            System.out.println("Total of all accounts of one client = " + utilities.getTotalSumInUAH(clientId) + " UAH");
        }
    }

    private static void showCurrencyRates() {
        Currency ratesEUR = utilities.getCurrencyRate("EUR");
        if (ratesEUR != null){
            System.out.println(ratesEUR);
        }
        Currency ratesUSD = utilities.getCurrencyRate("USD");
        if (ratesUSD != null){
            System.out.println(ratesUSD);
        }

    }

}
