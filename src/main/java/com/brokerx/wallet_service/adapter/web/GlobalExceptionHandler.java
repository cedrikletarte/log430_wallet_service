package com.brokerx.wallet_service.adapter.web;

import com.brokerx.wallet_service.domain.exception.wallet.WalletException;
import com.brokerx.wallet_service.domain.exception.walletTransaction.WalletTransactionException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard/home";
    }

    @ExceptionHandler({ WalletException.class, WalletTransactionException.class })
    public String handleWalletDomain(RuntimeException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard/home";
    }
}