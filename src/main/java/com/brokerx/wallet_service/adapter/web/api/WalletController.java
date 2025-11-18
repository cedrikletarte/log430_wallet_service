package com.brokerx.wallet_service.adapter.web.api;

import com.brokerx.wallet_service.adapter.web.dto.ApiResponse;
import com.brokerx.wallet_service.adapter.web.dto.WalletOperationRequest;
import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.command.PositionSuccess;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.application.port.in.useCase.WalletWithIdempotencyUseCase;

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
    private final WalletWithIdempotencyUseCase walletWithIdempotencyUseCase;

    public WalletController(WalletUseCase walletUseCase, WalletWithIdempotencyUseCase walletWithIdempotencyUseCase) {
        this.walletUseCase = walletUseCase;
        this.walletWithIdempotencyUseCase = walletWithIdempotencyUseCase;
    }

    /* Retrieve the wallet details of the authenticated user */
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

    /* Retrieve the transactions of the authenticated user */
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

    /* Retrieve the positions of the authenticated user */
    @GetMapping("/positions")
    public ResponseEntity<ApiResponse<List<PositionSuccess>>> getPositions(Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        List<PositionSuccess> positions = walletUseCase.getPositionsByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Positions retrieved successfully",
                positions));
    }

    /* Credit the wallet of the authenticated user */
    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Map<String, String>>> credit(@RequestBody WalletOperationRequest payload,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        walletWithIdempotencyUseCase.creditWithIdempotency(Long.parseLong(userId), payload.getAmount(), payload.getIdempotencyKey());

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Amount added successfully",
                Map.of("userId", userId, "amount", payload.getAmount().toString())));
    }

    /* Debit the wallet of the authenticated user */
    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Map<String, String>>> debit(@RequestBody WalletOperationRequest payload,
            Authentication authentication) {
        String userId = authentication.getPrincipal().toString();
        walletWithIdempotencyUseCase.debitWithIdempotency(Long.parseLong(userId), payload.getAmount(), payload.getIdempotencyKey());

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                null,
                "Amount withdrawn successfully",
                Map.of("userId", userId, "amount", payload.getAmount().toString())));
    }
}
