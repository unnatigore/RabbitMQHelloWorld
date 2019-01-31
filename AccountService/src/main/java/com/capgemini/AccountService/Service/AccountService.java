package com.capgemini.AccountService.Service;

import java.util.List;
import com.capgemini.AccountService.Entity.SavingsAccount;

public interface AccountService  {

	SavingsAccount addSavingsAccount(SavingsAccount savingsAccount);

	List<SavingsAccount> getListOfAccounts();

	void updateBalance(SavingsAccount savingsAccount);

	SavingsAccount getAccountById(int accountId);

}
