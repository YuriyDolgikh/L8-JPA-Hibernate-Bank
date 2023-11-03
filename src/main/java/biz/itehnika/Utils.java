package biz.itehnika;

import java.util.List;

public interface Utils {

    public void updateCurrencyRateInDB();
    public Currency getCurrencyRate(String currencyName);

    public boolean isClientExist(Client client);
    public boolean isClientExistById(Long clientId);
    public boolean isClientOwnsAccount(Long clientID, Long accountId);
    public boolean isClientHasAccountByCurrency(Long clientId, String currencyName);

    public void createRandomClientsList(int quantityClients);
    public void createNewClient();
    public void deleteClientById(Long id);
    public void deleteClientByPhoneNumber(String phoneNumber);

    public List<Client> getAllClients();
    public Client getClientById(Long id);
    public Client getClientByPhoneNumber(String phoneNumber);

    public void createAccountForClient(Long clientId);
    public void deleteAccountFromClient(Long clientId);

    public List<Account> getAllAccounts();
    public List<Account> getAccountsByClientId(Long clientId);
    public void depositIntoAccount(Long clientId);
    public Long getTotalSumInUAH(Long clientId);

    public void transferMoney(Long senderClientId, Long recipientClientId);
    public void exchangeCurrency(Long clientId);            // for one client
    public List<Transaction> getAllTransaction();

}
