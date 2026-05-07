package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Last n transactions for an account (for the Dashboard)
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId ORDER BY t.createdAt DESC LIMIT :limit")
    List<Transaction> findTopNByAccountId(@Param("accountId") Long accountId, @Param("limit") int limit);

    // All transactions for an account, sorted from newest to oldest
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

}
