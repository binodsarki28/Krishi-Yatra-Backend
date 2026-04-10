package com.krishiYatra.krishiYatra.admin;

import com.krishiYatra.krishiYatra.admin.dto.AdminStatsResponse;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admin")
@Tag(name = "Admin Controller", description = "Endpoints for admin dashboard stats")
public class AdminController {

    private final FarmerRepo farmerRepo;
    private final BuyerRepo buyerRepo;
    private final DeliveryRepo deliveryRepo;
    private final AdminService adminService;

    public AdminController(FarmerRepo farmerRepo, BuyerRepo buyerRepo, DeliveryRepo deliveryRepo, AdminService adminService) {
        this.farmerRepo = farmerRepo;
        this.buyerRepo = buyerRepo;
        this.deliveryRepo = deliveryRepo;
        this.adminService = adminService;
    }

    @Operation(summary = "Get full admin dashboard data")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ServerResponse getDashboard() {
        return adminService.getAdminDashboard();
    }

    @Operation(summary = "Get counts of pending applications for the dashboard")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminStatsResponse> getStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setPendingFarmers(farmerRepo.countByStatus(VerificationStatus.PENDING));
        stats.setPendingBuyers(buyerRepo.countByStatus(VerificationStatus.PENDING));
        stats.setPendingDelivery(deliveryRepo.countByStatus(VerificationStatus.PENDING));
        return ResponseEntity.ok(stats);
    }
}
