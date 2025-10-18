package com.brokerx.wallet_service.adapter.web.api;

import com.brokerx.wallet_service.adapter.web.dto.ApiResponse;
import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletUseCase walletUseCase;

    public WalletController(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<WalletSuccess>> me(Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        WalletSuccess walletSuccess = walletUseCase.getWalletByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Wallet retrieved successfully",
                walletSuccess));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionSuccess>>> getTransactions(Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        List<TransactionSuccess> transactions = walletUseCase.getTransactionsByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Transactions retrieved successfully",
                transactions));
    }
    

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Map<String, String>>> credit(@RequestBody Map<String, BigDecimal> payload,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        walletUseCase.credit(Long.parseLong(userId), payload.get("amount"));

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Amount added successfully",
                Map.of("userId", userId, "amount", payload.get("amount").toString())));
    }

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Map<String, String>>> debit(@RequestBody Map<String, BigDecimal> payload,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        walletUseCase.debit(Long.parseLong(userId), payload.get("amount"));

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Amount withdrawn successfully",
                Map.of("userId", userId, "amount", payload.get("amount").toString())));
    }
}
