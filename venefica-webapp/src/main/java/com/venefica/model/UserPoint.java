/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venefica.model;

import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;

/**
 * This entity stores all the user points (pending, insider, exclusive).
 * 
 * TODO: not yet finished.
 * 
 * @author gyuszi
 */
@Entity
//@SequenceGenerator(name = "userpoint_gen", sequenceName = "userpoint_seq", allocationSize = 1)
@Table(name = "user_point")
public class UserPoint {
    
//    private static final BigDecimal HUNDRED = new BigDecimal(100);
    
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userpoint_gen")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne(optional = false)
    @PrimaryKeyJoinColumn
    @ForeignKey(name = "userpoint_user_fk")
    private User user;
    
    @OneToMany(mappedBy = "userPoint")
    private Set<UserTransaction> transactions;
    
    private BigDecimal givingNumber;
    private BigDecimal receivingNumber;
    
    public UserPoint() {
    }
    
    public UserPoint(int givingNumber, int receivingNumber) {
        this.givingNumber = new BigDecimal(givingNumber);
        this.receivingNumber = new BigDecimal(receivingNumber);
    }
    
    // helper methods

    public void addGivingNumber(BigDecimal n) {
        n = checkNumber(n);
        givingNumber = checkNumber(givingNumber);
        givingNumber = givingNumber.add(n);
    }
    
    public void addReceivingNumber(BigDecimal n) {
        n = checkNumber(n);
        receivingNumber = checkNumber(receivingNumber);
        receivingNumber = receivingNumber.add(n);
    }
    
    private BigDecimal checkNumber(BigDecimal n) {
        if ( n == null ) {
            n = BigDecimal.ZERO;
        }
        return n;
    }
    
    public BigDecimal getPendingGivingNumber() {
        if ( transactions == null || transactions.isEmpty() ) {
            return BigDecimal.ZERO;
        }
        BigDecimal pendingGivingNumber = BigDecimal.ZERO;
        for ( UserTransaction transaction : transactions ) {
            if ( !transaction.isApproved() ) {
                continue;
            } else if ( transaction.isFinalized() ) {
                continue;
            }
            pendingGivingNumber = pendingGivingNumber.add(transaction.getPendingGivingNumber() != null ? transaction.getPendingGivingNumber() : BigDecimal.ZERO);
        }
        return pendingGivingNumber;
    }
    
    public BigDecimal getPendingReceivingNumber() {
        if ( transactions == null || transactions.isEmpty() ) {
            return BigDecimal.ZERO;
        }
        BigDecimal pendingReceivingNumber = BigDecimal.ZERO;
        for ( UserTransaction transaction : transactions ) {
            if ( !transaction.isApproved() ) {
                continue;
            } else if ( transaction.isFinalized() ) {
                continue;
            }
            pendingReceivingNumber = pendingReceivingNumber.add(transaction.getPendingReceivingNumber() != null ? transaction.getPendingReceivingNumber() : BigDecimal.ZERO);
        }
        return pendingReceivingNumber;
    }
    
//    private boolean containsTransaction(Ad ad) {
//        if ( transactions == null || transactions.isEmpty() ) {
//            return false;
//        }
//        for ( UserTransaction transaction : transactions ) {
//            if ( transaction.getAd() != null && transaction.getAd().equals(ad) ) {
//                return true;
//            }
//        }
//        return false;
//    }
//    
//    private boolean containsTransaction(Request request) {
//        if ( transactions == null || transactions.isEmpty() ) {
//            return false;
//        }
//        for ( UserTransaction transaction : transactions ) {
//            if ( transaction.getRequest()!= null && transaction.getRequest().equals(request) ) {
//                return true;
//            }
//        }
//        return false;
//    }
    
    // static helpers
    
    public static BigDecimal getGivingNumber(Ad ad) {
        Integer quantity = ad.getAdData().getQuantity();
        BigDecimal number = ad.getNumber();
        
        if ( quantity == null || quantity <= 0 ) {
            quantity = 1;
        }
        
        number = number.multiply(new BigDecimal(quantity));
        return number;
    }
    
    public static BigDecimal getReceivingNumber(Request request) {
        Ad ad = request.getAd();
        BigDecimal number = ad.getNumber();
        return number;
    }
    
    // getters/setters
    
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    private void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<UserTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<UserTransaction> transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getGivingNumber() {
        return givingNumber;
    }

    private void setGivingNumber(BigDecimal givingNumber) {
        this.givingNumber = givingNumber;
    }

    public BigDecimal getReceivingNumber() {
        return receivingNumber;
    }

    private void setReceivingNumber(BigDecimal receivingNumber) {
        this.receivingNumber = receivingNumber;
    }
}
