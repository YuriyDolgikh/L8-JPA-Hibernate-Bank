package biz.itehnika;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Utilities implements Utils{

    EntityManager em;
    EntityTransaction transaction;
    CriteriaBuilder builder;
    Scanner scanner;

    public Utilities(EntityManager em, Scanner scanner) {
        this.em = em;
        transaction = em.getTransaction();
        builder = em.getCriteriaBuilder();
        this.scanner = scanner;
    }

    @Override
    public void updateCurrencyRateInDB() {
        Currency ratesEUR = new Currency();
        ratesEUR.getActualRates("EUR");
        Currency ratesUSD = new Currency();
        ratesUSD.getActualRates("USD");
        transaction.begin();
        TypedQuery<Currency> typedQuery = em.createNamedQuery("getAllCurrencys", Currency.class);
        List<Currency> currencies = typedQuery.getResultList();
        try{
            if (!currencies.isEmpty()){
                for (Currency currency : currencies){
                    if (currency.getName().equals("USD")){
                        currency.setBuyRate(ratesUSD.getBuyRate());
                        currency.setSellRate(ratesUSD.getSellRate());

                    }
                    if (currency.getName().equals("EUR")){
                        currency.setBuyRate(ratesEUR.getBuyRate());
                        currency.setSellRate(ratesEUR.getSellRate());

                    }
                    em.merge(currency);
                }
            }else {
                em.persist(ratesEUR);
                em.persist(ratesUSD);
            }
            transaction.commit();
            System.out.println("The currency rates updated.");
        }catch (Exception e){
            transaction.rollback();
            System.out.println("Something went wrong!");
        }
        em.clear();
    }

    @Override
    public Currency getCurrencyRate(String currencyName) {
        CriteriaQuery<Currency> critQuery = builder.createQuery(Currency.class);
        Root<Currency> root = critQuery.from(Currency.class);
        critQuery.select(root).where(builder.like(root.get("name"), currencyName));
        TypedQuery<Currency> query = em.createQuery(critQuery);
        Currency currency;
        try {
            currency = query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Currency rates is not exist in database. Download the rates!");
            em.clear();
            return null;
        }
        return currency;
    }

    @Override
    public boolean isClientExist(Client client) {
        CriteriaQuery<Client> critQuery = builder.createQuery(Client.class);
        Root<Client> root = critQuery.from(Client.class);
        critQuery.select(root).where(builder.like(root.get("phoneNumber"), client.getPhoneNumber()));
        TypedQuery<Client> query = em.createQuery(critQuery);
        try {
            Client responseClient = query.getSingleResult();
        } catch (NoResultException e) {
            em.clear();
            return false;
        }
        em.clear();
        return true;
    }

    @Override
    public boolean isClientExistById(Long clientId) {
        CriteriaQuery<Client> critQuery = builder.createQuery(Client.class);
        Root<Client> root = critQuery.from(Client.class);
        critQuery.select(root).where(builder.equal(root.get("id"), clientId.toString()));
        TypedQuery<Client> query = em.createQuery(critQuery);
        try {
            Client responseClient = query.getSingleResult();
        } catch (NoResultException e) {
            em.clear();
            return false;
        }
        em.clear();
        return true;
    }

    @Override
    public boolean isClientOwnsAccount(Long clientId, Long accountId) {
        Client client = getClientById(clientId);
        if (client != null){
            List<Account> accounts = client.getAccounts();
            if (accounts != null || accounts.size() != 0){
                for (Account account : accounts){
                    if (account.getId() == accountId){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isClientHasAccountByCurrency(Long clientId, String currencyName) {
        List<Account> accounts = getAccountsByClientId(clientId);
        if (accounts == null || accounts.size() == 0){
            return false;
        }
        for (Account account : accounts){
            if (account.getCurrencyName().equals(currencyName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void createRandomClientsList(int quantityClients) {
        Random random = new Random();
        List<Client> clients = getAllClients();
        Integer clientsListSize = clients == null ? 0 : clients.size();
        transaction.begin();
        try{
            for (int i = clientsListSize; i < quantityClients + clientsListSize; i++){
                Client client = new Client("Client" + i, generatePhoneNumber(i));
                client.addAccount(new Account("UAH", (long) random.nextInt(100000)));
                client.addAccount(new Account("USD", (long) random.nextInt(100000)));
                client.addAccount(new Account("EUR", (long) random.nextInt(100000)));
                em.persist(client);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        em.clear();
    }

    @Override
    public void createNewClient() {
        String name;
        String phoneNumber;
        System.out.println("Input the client's full name:");
        name = scanner.nextLine();
        System.out.println("Input the client's phoneNumber:");
        phoneNumber = scanner.nextLine();
        Client client = new Client(name, phoneNumber);
        if(!isClientExist(client)){
            transaction.begin();
            try{
                em.persist(client);
                transaction.commit();
                System.out.println(client + " Added.");
            }catch (Exception e){
                transaction.rollback();
                e.printStackTrace();
            }
            em.clear();
        }else {
            System.out.println("Client with given phone number already exist!");
        }

    }

    @Override
    public void deleteClientById(Long id) {
        Client client = getClientById(id);
        transaction.begin();
        try {
            em.remove(client);
            transaction.commit();
            System.out.println("Client with 'Id' =  " + id + " deleted!");
        }catch (Exception e){
            transaction.rollback();
            System.out.println("Client with 'Id' =  " + id + " is not exist!");
        }
        em.clear();
    }

    @Override
    public void deleteClientByPhoneNumber(String phoneNumber) {
        Client client = getClientByPhoneNumber(phoneNumber);
        if (client != null){
            transaction.begin();
            try {
                em.remove(client);
                transaction.commit();
                System.out.println("Client with phone number =  " + phoneNumber + " deleted!");
            }catch (Exception e){
                transaction.rollback();
                e.printStackTrace();
            }
            em.clear();
        }
    }

    @Override
    public List<Client> getAllClients() {
       TypedQuery<Client> typedQuery = em.createNamedQuery("getAllClients", Client.class);
        List<Client> clients = typedQuery.getResultList();
        if (clients.isEmpty()){
            System.out.println("There are no clients in bank!");
            return null;
        }
        return clients;
    }

    @Override
    public Client getClientById(Long id) {
        Client client = null;
        if (isClientExistById(id)){
            client = em.getReference(Client.class, id);
        } else {
            System.out.println("Client with 'Id' =  " + id + " is not exist!");
        }
        return client;
    }

    @Override
    public Client getClientByPhoneNumber(String phoneNumber) {
        CriteriaQuery<Client> critQuery = builder.createQuery(Client.class);
        Root<Client> root = critQuery.from(Client.class);
        critQuery.select(root).where(builder.like(root.get("phoneNumber"), phoneNumber));
        TypedQuery<Client> query = em.createQuery(critQuery);
        try {
            Client client = query.getSingleResult();
            return client;
        } catch (NoResultException e) {
            System.out.println("Client with phone number '" + phoneNumber + "' is not exist!");;
        }
        return  null;
    }

    @Override
    public void createAccountForClient(Long clientId) {
        List<Account> accounts = getAccountsByClientId(clientId);
        if (accounts == null || accounts.isEmpty()){
            System.out.println("The client with Id = " + clientId + " has no accounts.");
        }else {
            System.out.println("The client with Id = " + clientId + " has accounts:");
            for (Account account : accounts) {
                System.out.println(account);
            }
        }
        System.out.println("Choose the currency [1 -> 'UAH'], [2 -> 'USD'], [3 -> 'EUR']:");
        String  curNum = scanner.nextLine();
        String currencyName;
        if (curNum.equals("1")){
            currencyName = "UAH";
        } else if (curNum.equals("2")) {
            currencyName = "USD";
        } else if (curNum.equals("3")) {
            currencyName = "EUR";
        }else {
            System.out.println("Wrong currency number");
            return;
        }
        for (Account account : accounts){
            if (account.getCurrencyName().equals(currencyName)){
                System.out.println("The client with Id = " + clientId + " has account with currency '" + currencyName + "' !");
                return;
            }
        }
        Client client = getClientById(clientId);
        Account account = new Account(currencyName, 0L);
        client.addAccount(account);
        transaction.begin();
        try {
            em.merge(client);
            transaction.commit();
            System.out.println("New account was added.");
        }catch (Exception e){
            transaction.rollback();
            e.printStackTrace();
        }
        em.clear();
    }

    @Override
    public void deleteAccountFromClient(Long clientId) {
        List<Account> accounts = getAccountsByClientId(clientId);
        if (accounts == null || accounts.size() == 0){
            System.out.println("The client with Id = " + clientId + " has no accounts.");
        }else {
            System.out.println("The client with Id = " + clientId + " has accounts:");
            for (Account account : accounts){
                System.out.println(account);
            }
            System.out.println("Input Id of account You want to delete:");
            Long accountId = Long.parseLong(scanner.nextLine());
            if (!isClientOwnsAccount(clientId,accountId)){
                System.out.println("The client with Id = " + clientId + " has no account with Id = " + accountId + ".");
                return;
            }
            transaction.begin();
                try {
                    Client client = em.getReference(Client.class, clientId);
                    accounts = client.getAccounts();
                    for (Account account : accounts){
                        if (account.getId() == accountId){
                            client.delAccount(account);
                            em.merge(client);
                            break;
                        }
                    }
                    transaction.commit();
                    System.out.println("Account with 'Id' =  " + accountId + " deleted!");
                    }catch (Exception e){
                        transaction.rollback();
                        e.printStackTrace();
                    }
            em.clear();
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        TypedQuery<Account> typedQuery = em.createNamedQuery("getAllAccounts", Account.class);
        List<Account> accounts = typedQuery.getResultList();
        if (accounts.isEmpty()){
            System.out.println("There are no accounts in the bank!");
            return null;
        }
        return accounts;
    }

    @Override
    public List<Account> getAccountsByClientId(Long clientId) {
        Client client = getClientById(clientId);
        if (client != null){
            return client.getAccounts();
        }
        return null;
    }

    @Override
    public void depositIntoAccount(Long clientId) {
        if (!isClientExistById(clientId)){
            System.out.println("Client with 'Id' =  " + clientId + " is not exist!");
        }else{
            List<Account> accounts = getAccountsByClientId(clientId);
            if (accounts == null || accounts.isEmpty()){
                System.out.println("The client with Id = " + clientId + " has no accounts.");
                return;
            }else {
                System.out.println("The client with Id = " + clientId + " has accounts:");
                for (Account account : accounts) {
                    System.out.println(account);
                }
            }
            System.out.println("Choose the currency [1 -> 'UAH'], [2 -> 'USD'], [3 -> 'EUR']:");
            String  curNum = scanner.nextLine();
            String currencyName;
            if (curNum.equals("1")){
                currencyName = "UAH";
            } else if (curNum.equals("2")) {
                currencyName = "USD";
            } else if (curNum.equals("3")) {
                currencyName = "EUR";
            }else {
                System.out.println("Wrong currency number");
                return;
            }
            boolean isCurrencyPresent = false;
            for (Account account : accounts){
                if (account.getCurrencyName().equals(currencyName)){
                    isCurrencyPresent = true;
                    break;
                }
            }
            if (!isCurrencyPresent){
                System.out.println("The client has no account with currency '" + currencyName + "'.");
                return;
            }
            System.out.println("Input sum in '" + currencyName + "' You want to add to account:");
            Long sum = Long.parseLong(scanner.nextLine());
            transaction.begin();
            Client client;
            try {
                client = getClientById(clientId);
                accounts = client.getAccounts();
                for (Account account : accounts){
                    if (account.getCurrencyName().equals(currencyName)){
                        account.setAmount(sum);
                        break;
                    }
                }
                em.merge(client);
                transaction.commit();
                System.out.println(sum + " " + currencyName + " deposited into account.");
            }catch (Exception e){
                transaction.rollback();
                e.printStackTrace();
            }
            em.clear();
        }
    }

    @Override
    public Long getTotalSumInUAH(Long clientId) {
        List<Account> accounts = getAccountsByClientId(clientId);
        Double totalSum = 0.0;
        if (accounts == null || accounts.isEmpty()){
            System.out.println("The client with Id = " + clientId + " has no accounts.");
            return null;
        }
        for (Account account : accounts){
            if (account.getCurrencyName().equals("USD")){
                totalSum += getCurrencyRate("USD").getBuyRate() * account.getAmount();
            }else if (account.getCurrencyName().equals("EUR")){
                totalSum += getCurrencyRate("EUR").getBuyRate() * account.getAmount();
            }else if (account.getCurrencyName().equals("UAH")){
                totalSum += account.getAmount();
            }
        }
        return totalSum.longValue();
    }

    @Override
    public void transferMoney(Long senderClientId, Long receiverClientId) {
        List<Account> senderAccounts = getAccountsByClientId(senderClientId);
        List<Account> receiverAccounts = getAccountsByClientId(receiverClientId);
        if (senderAccounts == null || senderAccounts.isEmpty()){
            System.out.println("The client sender with Id = " + senderClientId + " has no accounts.");
            return;
        }else if (receiverAccounts == null || receiverAccounts.isEmpty()){
            System.out.println("The client receiver with Id = " + receiverClientId + " has no accounts.");
            return;
        }
        System.out.println("The client sender with Id = " + senderClientId + " has accounts:");
        for (Account account : senderAccounts){
            System.out.println(account);
        }
        System.out.println("Input the sender's account Id:");
        Long senderAccountId = Long.parseLong(scanner.nextLine());
        String currencyForTransfer = "";
        Long maxSumForTransfer = 0L;
        for (Account account : senderAccounts){
            if (account.getId() == senderAccountId){
                currencyForTransfer = account.getCurrencyName();
                maxSumForTransfer = account.getAmount();
            }
        }
        Long receiverAccountId = 0L;
        if (!isClientHasAccountByCurrency(receiverClientId,currencyForTransfer)){
            System.out.println("The client receiver with Id = " + receiverClientId + " has no account in currency '" + currencyForTransfer + "'");
            return;
        }else{
            for (Account account : receiverAccounts){
                if (account.getCurrencyName().equals(currencyForTransfer)){
                    receiverAccountId = account.getId();
                }
            }
        }
        System.out.println("Input the sum in '" + currencyForTransfer + "' for transfer:");
        Long sumForTransfer = 0L;
        do{
            sumForTransfer = Long.parseLong(scanner.nextLine());
            if (sumForTransfer <= maxSumForTransfer){
                break;
            }
            System.out.println("There are not enough funds in the account!");
            System.out.println("Input new sum:");
        }while (true);
        transaction.begin();
        try {
            Client senderClient = em.getReference(Client.class, senderClientId);
            Client receiverClient = em.getReference(Client.class, receiverClientId);
            Account senderAccount = em.getReference(Account.class,senderAccountId);
            Account receiverAccount = em.getReference(Account.class, receiverAccountId);
            Transaction bankTransaction = new Transaction(sumForTransfer,senderClient,receiverClient,senderAccount,receiverAccount);
            senderAccount.setAmount(maxSumForTransfer - sumForTransfer);
            receiverAccount.setAmount(receiverAccount.getAmount() + sumForTransfer);

            em.persist(bankTransaction);
            transaction.commit();
            System.out.println("Transaction successful.");
        }catch (Exception e){
            transaction.rollback();
            e.printStackTrace();
        }
        em.clear();
    }

    @Override
    public void exchangeCurrency(Long clientId) {
        System.out.println("What currency will be exchanged?");
        List<Account> accounts = getAccountsByClientId(clientId);
        Long accountIdUAH = 0L;
        Long accountIdForeignCurrency = 0L;
        Account accountUAH = null;
        Account accountForeignCurrency = null;
        boolean isAccountExistInUAH = false;
        if (accounts == null || accounts.isEmpty()){
            System.out.println("The client with Id = " + clientId + " has no accounts.");
            return;
        }else {
            System.out.println("The client with Id = " + clientId + " has accounts:");
            for (Account account : accounts) {
                System.out.println(account);
                if (account.getCurrencyName().equals("UAH")){
                    isAccountExistInUAH = true;
                    accountIdUAH =account.getId();
                    accountUAH = account;
                }
            }
        }
        if (!isAccountExistInUAH){
            System.out.println("The client with Id = " + clientId + " has no account in 'UAH'.");
            System.out.println("Exchange is impossible!");
            return;
        }
        System.out.println("Choose the currency [1 -> 'USD'], [2 -> 'EUR']:");
        String  curNum = scanner.nextLine();
        String currencyName;
        if (curNum.equals("1")) {
            currencyName = "USD";
        } else if (curNum.equals("2")) {
            currencyName = "EUR";
        }else {
            System.out.println("Wrong currency number");
            return;
        }
        boolean isCurrencyPresent = false;
        for (Account account : accounts){
            if (account.getCurrencyName().equals(currencyName)){
                isCurrencyPresent = true;
                accountIdForeignCurrency = account.getId();
                accountForeignCurrency = account;
                break;
            }
        }
        if (!isCurrencyPresent){
            System.out.println("The client has no account with currency '" + currencyName + "'.");
            return;
        }
        String typeOperation;
        System.out.println("Is the client buy or sell currency? [1 -> 'Buy'], [2 -> 'Sell']:");
        String  operNum = scanner.nextLine();
        if (operNum.equals("1")) {
            typeOperation = "Buy";
        } else if (operNum.equals("2")) {
            typeOperation = "Sell";
        }else {
            System.out.println("Wrong operation number!");
            return;
        }
        System.out.println("Input sum in '" + currencyName + "' Client want to " + typeOperation + ":");

        if (typeOperation.equals("Buy")){
            /*         Client Buy currency           */
            Double sellRate = getCurrencyRate(currencyName).getSellRate();
            Long maxSumForBuy = (long) (accountUAH.getAmount() / sellRate);
            Long sumForBuy = 0L;
            do{
                sumForBuy = Long.parseLong(scanner.nextLine());
                if (sumForBuy <= maxSumForBuy){
                    break;
                }
                System.out.println("There are not enough funds in the account!");
                System.out.println("Input new sum:");
            }while (true);

            transaction.begin();
            try {
                Client client = em.getReference(Client.class, clientId);
                Account accUAH = em.getReference(Account.class, accountIdUAH);
                Account accForeighCur = em.getReference(Account.class, accountIdForeignCurrency);
                Transaction bankTransaction = new Transaction(sumForBuy,client,client,accUAH,accForeighCur);
                accUAH.setAmount(maxSumForBuy - (long)(sumForBuy * sellRate));
                accForeighCur.setAmount(accForeighCur.getAmount() + sumForBuy);

                em.persist(bankTransaction);
                transaction.commit();
                System.out.println("Exchange transaction successful.");
                System.out.println("The client bought " + sumForBuy + " " + currencyName);
            }catch (Exception e){
                transaction.rollback();
                e.printStackTrace();
            }
            em.clear();

        }else{
            /*         Client Sell currency           */
            Double buyRate = getCurrencyRate(currencyName).getBuyRate();
            Long maxSumForSell = accountForeignCurrency.getAmount();
            Long sumForSell = 0L;
            do{
                sumForSell = Long.parseLong(scanner.nextLine());
                if (sumForSell <= maxSumForSell){
                    break;
                }
                System.out.println("There are not enough funds in the account!");
                System.out.println("Input new sum:");
            }while (true);

            transaction.begin();
            try {
                Client client = em.getReference(Client.class, clientId);
                Account accUAH = em.getReference(Account.class, accountIdUAH);
                Account accForeighCur = em.getReference(Account.class, accountIdForeignCurrency);
                Transaction bankTransaction = new Transaction(sumForSell,client,client,accUAH,accForeighCur);
                accUAH.setAmount(accUAH.getAmount() + (long)(sumForSell * buyRate));
                accForeighCur.setAmount(accForeighCur.getAmount() - sumForSell);

                em.persist(bankTransaction);
                transaction.commit();
                System.out.println("Exchange transaction successful.");
                System.out.println("The client sold " + sumForSell + " " + currencyName);
            }catch (Exception e){
                transaction.rollback();
                e.printStackTrace();
            }
            em.clear();
        }
    }

    @Override
    public List<Transaction> getAllTransaction() {
        TypedQuery query = em.createNamedQuery("getAllTransactions", Transaction.class);
        List<Transaction> bankTransactions = query.getResultList();
        if (bankTransactions.isEmpty()){
            System.out.println("There are no transaction in the bank!");
            return null;
        }
        return bankTransactions;
    }

    private String generatePhoneNumber(int i){
        String str = "0";
        String endStr = String.valueOf(i);
        str = "+380501" + str.repeat(6 - endStr.length()) + endStr;
        return str;
    }

}
