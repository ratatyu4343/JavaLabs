package Task2CustomerStructure;

import java.io.Serializable;

public class Customer implements Serializable {
    private int id;
    private String name;
    private String surname;
    private String father;
    private String adress;
    private int creditNum;
    private int bankNum;
    public Customer(int i,String n,String s,String f,String a,int c,int b){
        setId(i);
        setName(n);
        setSurname(s);
        setFather(f);
        setAdress(a);
        setCreditNum(c);
        setBankNum(b);
    };

    @Override
    public String toString() {
        return String.format("N: %s S: %s F: %s A: %s, C: %d, B: %d\n", name, surname, father, adress, creditNum, bankNum);
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getFather() {
        return father;
    }
    public void setFather(String father) {
        this.father = father;
    }
    public String getAdress() {
        return adress;
    }
    public void setAdress(String adress) {
        this.adress = adress;
    }
    public int getCreditNum() {
        return creditNum;
    }
    public void setCreditNum(int creditNum) {
        this.creditNum = creditNum;
    }
    public int getBankNum() {
        return bankNum;
    }
    public void setBankNum(int bankNum) {
        this.bankNum = bankNum;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
