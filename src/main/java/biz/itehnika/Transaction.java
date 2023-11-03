package biz.itehnika;


import javax.persistence.*;

@Entity
@Table(name = "transactions")
@NamedQuery(name = "getAllTransactions", query = "SELECT c FROM Transaction c")

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sum;

    @ManyToOne()
    @JoinColumn(name = "sender_client_id", referencedColumnName = "id")
    private Client senderClient;

    @ManyToOne()
    @JoinColumn(name = "receiver_client_id", referencedColumnName = "id")
    private Client receiverClient;

    @ManyToOne()
    @JoinColumn(name = "sender_account_id", referencedColumnName = "id")
    private Account senderAccount;

    @ManyToOne()
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;

    public Transaction() {
    }

    public Transaction(Long sum, Client senderClient, Client receiverClient, Account senderAccount, Account receiverAccount) {
        this.sum = sum;
        this.senderClient = senderClient;
        this.receiverClient = receiverClient;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Client getSenderClient() {
        return senderClient;
    }

    public void setSenderClient(Client senderClient) {
        this.senderClient = senderClient;
    }

    public Client getReceiverClient() {
        return receiverClient;
    }

    public void setReceiverClient(Client receiverClient) {
        this.receiverClient = receiverClient;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sum=" + sum +
                ", senderClient=" + senderClient +
                ", receiverClient=" + receiverClient +
                ", senderAccount=" + senderAccount +
                ", receiverAccount=" + receiverAccount +
                '}';
    }
}
